package com.zdawn.commons.dict;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zdawn.commons.dict.model.DataField;
import com.zdawn.commons.dict.model.DictData;
import com.zdawn.commons.dict.model.MetaDict;
import com.zdawn.commons.dict.model.PropertyValue;
import com.zdawn.commons.dict.model.RecordData;
import com.zdawn.util.Utils;
import com.zdawn.util.json.jackson.JsonUtil;
import com.zdawn.util.spring.SpringHelper;
import com.zdawn.util.vo.VOUtil;

/**
 * 数据字典服务
 * @author zhaobs
 * 提供下拉列表格式Json数据
 * 提供树格式Json数据，树格式数据没提供扩展属性
 */
@Controller
@RequestMapping("/DataDictRest")
public class DataDictRestController {
	private final String startWith = "9";
	private final String equal = "1";
	/**
	 * 自定义查询编码数据
	 * 请求参数说明
	 *<br>bmName 编码表名
	 *<br>字段名 值也是字段名
	 *<br>字段名+Value 过滤值
	 *<br>字段名+Operator 操作符（支持1等于和9startWith）
	 */
	@RequestMapping(value="/getCustomJsonData.do",method = {RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView getCustomJsonData(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView model = new ModelAndView("jackson");
		try {
			ApplicationContext context = SpringHelper.getContext();
			String bmName = request.getParameter("bmName");
			if(bmName==null) throw new Exception("数据字典表名不存在");
			DataDictionary dict = context.getBean("dataDictionary", DataDictionary.class);
			MetaDict dicMeta  = dict.getMetaDict(bmName);
			String condition = spellBMCondition(request,dicMeta);
			DictData data = null;
			if(condition==null) {
				data = dict.getDictData(bmName);
			}else{
				data = dict.getDictData(bmName,condition);
			}
			ArrayList<Map<String,String>> bmData = new ArrayList<Map<String,String>>();
			List<RecordData> list = data.getListRecordDatas();
			int size = dicMeta.getListDataFields().size();
			for (RecordData recordData : list) {
				Map<String,String> temp = new HashMap<String,String>(size);
				List<DataField> listField = dicMeta.getListDataFields();
				for (DataField dataField : listField) {
					Object obj = recordData.getValue(dataField.getFieldName());
					if(dataField.getDataType().equals("date")){
						temp.put(dataField.getFieldName(), obj!=null ? 
								Utils.transformDate(dataField.getToStringformat(), (Date)obj):"");
					}else{
						temp.put(dataField.getFieldName(), obj!=null ? obj.toString():"");
					}
				}
				bmData.add(temp);
			}
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(true, "查询成功",bmData));
		}catch (Exception e) {
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
		}
        return model; 
	}
	//如果null，没有条件
	private String spellBMCondition(HttpServletRequest request, MetaDict dicMeta) {
		StringBuffer sb = new StringBuffer();
		List<DataField> fields = dicMeta.getListDataFields();
		for (DataField dataField : fields) {
			String fieldName = request.getParameter(dataField.getFieldName());
			fieldName = fieldName==null ? "":fieldName;
			String fieldOperator = request.getParameter(dataField.getFieldName()+"Operator");
			fieldOperator = fieldOperator==null ? "1":fieldOperator;
			String fieldValue = request.getParameter(dataField.getFieldName()+"Value");
			fieldValue = fieldValue==null ? "":fieldValue;
			if(fieldName.length()>0 && fieldValue.length()>0){
				String temp = "";
				if(startWith.equals(fieldOperator)){
					temp = fieldName+" like '"+fieldValue+"%'";
				}else if(equal.equals(fieldOperator)){
					if(dataField.getDataType().equals("string")){
						temp = fieldName+"='"+fieldValue+"'";
					}else if(dataField.getDataType().equals("number")){
						temp = fieldName+"="+fieldValue;
					}
				}
				if(sb.length()==0) sb.append(temp);
				else sb.append(" and ").append(temp);
			}
		}
		return sb.length()==0 ? null:sb.toString();
	}
	/**
	 * 请求参数说明
	 * bmNames 编码表名
	 */
	@RequestMapping(value="/getComboboxJson.do",method = {RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView getComboboxJson(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView model = new ModelAndView("jackson");
		
		try {
			Map<String,ArrayList<PropertyValue>> map = new HashMap<String,ArrayList<PropertyValue>>();
			ApplicationContext context = SpringHelper.getContext();
			String[] bmNames = request.getParameterValues("bmNames");
			String ddNames = request.getParameter("ddNames");
			DataDictionary dict = context.getBean("dataDictionary", DataDictionary.class);
			// ddcache
			if(ddNames!=null){
				List<Map<String,String>> list = JsonUtil.parseArrayOrSingleRequestData(null,ddNames);
				for(Map<String,String> tmp :list){
					String bmTableName = tmp.get("table");
					if(bmTableName==null)
						continue;
					DictData data = dict.getDictData(bmTableName);
					DataField df = data.getMetaDict().getUniqueDataField();
					map.put(bmTableName, getBMJsonData(df,data));
				}
			}else if(bmNames!=null){// bmNames
				for (int i = 0; i < bmNames.length; i++) {
					String bmTableName = bmNames[i];
					DictData data = dict.getDictData(bmTableName);
					DataField df = data.getMetaDict().getUniqueDataField();
					map.put(bmTableName, getBMJsonData(df,data));
				}
			}else{
				throw new Exception("数据字典表名不存在");
			}
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(true, "查询成功",map));
		}catch (Exception e) {
			e.printStackTrace();
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
		}
        return model; 
    }
	/**
	 * 请求参数说明
	 * bmName 树形结构编码表名
	 * nodeID 节点ID
	 * nest 递归读取子节点 true,false
	 * onlyChild 只获取子节点数据 true只获取子节点数据 false获取节点本身和子节点数据。
	 * <br>自定义treeNode key 通过请求参数自定义
	 * <br>nodeIdKey 节点id key
	 * <br>nodeTextKey 节点名称 key
	 * <br>childrenKey 子节点集合key
	 * <br>hasChildrenKey 是否子节点key
	 */
	@RequestMapping(value="/getTreeJson.do",method = {RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView getTreeJson(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView model = new ModelAndView("jackson");
		try {
			ApplicationContext context = SpringHelper.getContext();
			String bmName = request.getParameter("bmName");
			if(bmName==null) throw new Exception("数据字典表名不存在");
			String nodeID = request.getParameter("nodeID");
			String nest = request.getParameter("nest");
			nest = nest ==null ? "true":nest;
			String onlyChild = request.getParameter("onlyChild");
			onlyChild = onlyChild ==null ? "false":onlyChild;
			Map<String,String> treeKey = initTreeKey(request);
			DataDictionary dict = context.getBean("dataDictionary", DataDictionary.class);
			DictData data = dict.getDictData(bmName);
			List<Map<String, Object>> obj = new ArrayList<Map<String, Object>>();
			if(onlyChild.equals("true")){
				obj = getTreeDatas(nodeID, data, Boolean.parseBoolean(nest),treeKey);
				model.addObject("ResponseMessage",VOUtil.createResponseMessage(true, "查询成功",obj));
			}else{
				if(nodeID==null) throw new Exception("节点标识不存在");
				obj.add(getTreeNodeData(nodeID, data, Boolean.parseBoolean(nest),treeKey));
				model.addObject("ResponseMessage",VOUtil.createResponseMessage(true, "查询成功",obj));
			}
		}catch (Exception e) {
			e.printStackTrace();
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
		}
        return model; 
	}
	private Map<String,String> initTreeKey(HttpServletRequest request){
		Map<String,String> treeKey = new HashMap<String, String>();
		treeKey.put("nodeIdKey", "id");
		treeKey.put("nodeTextKey", "text");
		treeKey.put("childrenKey", "children");
		treeKey.put("hasChildrenKey", "hasChildren");
		Set<String> set = treeKey.keySet();
		for (String key : set) {
			String temp = request.getParameter(key);
			if(Utils.isEmpty(temp)) continue;
			treeKey.put(key, temp);
		}
		return treeKey;
	}
	private ArrayList<PropertyValue> getBMJsonData(DataField df,
			DictData data) {
		ArrayList<PropertyValue> al = new ArrayList<PropertyValue>();
		List<RecordData> listRecordData = data.getListRecordDatas();
		List<DataField> listDispField = data.getListDispField();
		for (int i = 0; i < listRecordData.size(); i++) {
			RecordData row = listRecordData.get(i);
			Object obj = row.getValue(df.getFieldName());
			if(obj==null) continue;
			PropertyValue pv = new PropertyValue();
			pv.setId(obj.toString());
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < listDispField.size(); j++) {
				DataField field = listDispField.get(j);
				String value = "";
				obj = row.getValue(field.getFieldName());
				if(field.getDataType().equals("date")){
					value = obj!=null ? Utils.transformDate(field.getToStringformat(),(Date)obj):"";
				}else{
					value = obj!=null ? obj.toString():"";
				}
				if(j==0){
					sb.append(value);
				}else{
					sb.append("-"+value);
				}
			}
			pv.setText(sb.toString());
			al.add(pv);
		}
		return al;
	}
	/**
	 * 查询子节点
	 * @param nodeID 节点ID
	 * @param data 编码数据
	 * @param nest 是否级联
	 * @param treeKey tree节点key
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	private List<Map<String,Object>> getTreeDatas(String nodeID,DictData data,boolean nest, Map<String, String> treeKey){
		List<Map<String,Object>> list = getChildrenData(nodeID,data,treeKey);
		if(list==null) return null;
		for (Map<String, Object> mapChild : list) {
			Object obj = mapChild.get(treeKey.get("hasChildrenKey"));
			String state = obj==null ? "false":obj.toString();
			if(nest&&state.equals("true")){
				String id = mapChild.get(treeKey.get("nodeIdKey")).toString();
				List<Map<String,Object>> childList=getTreeDatas(id,data,nest,treeKey);
				mapChild.put(treeKey.get("childrenKey"), childList);
			}
		}
		return list;
	}
	/**
	 * 查询当前节点包括子节点
	 * @param nodeID 节点ID
	 * @param data 编码数据
	 * @param nest 是否级联
	 * @param treeKey tree节点key
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	private Map<String,Object> getTreeNodeData(String nodeID,DictData data,boolean nest, Map<String, String> treeKey){
		Map<String,Object> map = new HashMap<String,Object>();
		RecordData row = data.getRecord(nodeID);
		if(row==null){
			map.put(treeKey.get("nodeIdKey"), nodeID);
			map.put(treeKey.get("nodeTextKey"),"节点不存在");
			return map;
		}
		List<DataField> listDispField = data.getListDispField();
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < listDispField.size(); j++) {
			DataField field = listDispField.get(j);
			if(j==0){
				sb.append(row.getValue(field.getFieldName()));
			}else{
				sb.append("-"+row.getValue(field.getFieldName()));
			}
		}
		map.put(treeKey.get("nodeIdKey"), nodeID);
		map.put(treeKey.get("nodeTextKey"),sb.toString());
		List<Map<String,Object>> child = getTreeDatas(nodeID,data,nest,treeKey);
		if(child!=null){
			map.put(treeKey.get("hasChildrenKey"),"true");
			map.put(treeKey.get("childrenKey"),child);
		}else{
			map.put(treeKey.get("hasChildrenKey"),"false");
		}
		return map;
	}
	private List<Map<String,Object>> getChildrenData(String nodeID,DictData data,Map<String, String> treeKey){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DataField df = data.getMetaDict().getUniqueDataField();
		Object obj = nodeID;
		if(df.getDataType().equals("number") && nodeID!=null){
			obj = new Long(nodeID);
		}
		List<RecordData> listData = data.getListRecordDatas();
		String parentField = data.getMetaDict().getParentField();
		if(obj==null){
			for (RecordData recordData : listData) {
				if(recordData.getValue(parentField)==null){
					list.add(createTreeNode(data,df,recordData,treeKey));
				}
			}
		}else{
			for (RecordData recordData : listData) {
				if(obj.equals(recordData.getValue(parentField))){
					list.add(createTreeNode(data,df,recordData,treeKey));
				}
			}		
		}
		return list.size()==0 ? null:list;
	}
	private Map<String,Object> createTreeNode(DictData data,DataField df, RecordData recordData, Map<String, String> treeKey) {
		Map<String,Object> node = new HashMap<String,Object>(5);
		String id = recordData.getValue(df.getFieldName())+"";
		node.put(treeKey.get("nodeIdKey"), id);
		List<DataField> listDispField = data.getListDispField();
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < listDispField.size(); j++) {
			DataField field = listDispField.get(j);
			if(j==0){
				sb.append(recordData.getValue(field.getFieldName()));
			}else{
				sb.append("-"+recordData.getValue(field.getFieldName()));
			}
		}
		node.put(treeKey.get("nodeTextKey"),sb.toString());
		node.put(treeKey.get("hasChildrenKey"),String.valueOf(hasChildren(id,data)));
		return node;
	}
	private boolean hasChildren(String nodeID,DictData data){
		DataField df = data.getMetaDict().getUniqueDataField();
		Object obj = nodeID;
		if(df.getDataType().equals("number")){
			obj = new Long(nodeID);
		}
		List<RecordData> listData = data.getListRecordDatas();
		String parentField = data.getMetaDict().getParentField();
		for (RecordData recordData : listData) {
			Object pidObj = recordData.getValue(parentField);
			if(pidObj!=null && obj.equals(pidObj)) return true;
		}
		return false;
	}
	/**
	 * 获取节点本身和子节点数据，请求参数说明。
	 * bmName 树形结构编码表名
	 * nodeID 节点ID
	 * onlyChild 只获取子节点数据 true只获取子节点数据 false获取节点本身和子节点数据。
	 * <br>自定义treeNode key 通过请求参数自定义
	 * <br>nodeIdKey 节点id key
	 * <br>nodeTextKey 节点名称 key
	 * <br>childrenKey 子节点集合key
	 * <br>hasChildrenKey 是否子节点key
	 */
	@RequestMapping(value="/getAsynTreeJson.do",method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView getAsynTreeJson(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model = new ModelAndView("jackson");
		try {
			ApplicationContext context = SpringHelper.getContext();
			String bmName = request.getParameter("bmName");
			if(bmName==null) throw new Exception("数据字典表名不存在");
			String nodeID = request.getParameter("nodeID");
			String onlyChild = request.getParameter("onlyChild");
			onlyChild = onlyChild ==null ? "false":onlyChild;
			DataDictionary dict = context.getBean("dataDictionary", DataDictionary.class);
			StringBuilder sb = new StringBuilder();
			if(nodeID!=null && !nodeID.equals("")){
				MetaDict metaDict = dict.getMetaDict(bmName);
				String pid = metaDict.getParentField();
				DataField uniqueDataField= metaDict.getUniqueDataField();
				sb.append('(');
				sb.append(uniqueDataField.getFieldName());
				if(uniqueDataField.getDataType().equals("number")){
					sb.append('=').append(nodeID);
				}else{
					sb.append("='").append(nodeID).append('\'');
				}
				sb.append(" or ");
				sb.append(pid);
				if(uniqueDataField.getDataType().equals("number")){
					sb.append('=').append(nodeID);
				}else{
					sb.append("='").append(nodeID).append('\'');
				}
				sb.append(" or ");
				sb.append(pid).append(" in(");
				String tableName = metaDict.getTableName().length() == 0 ? metaDict
						.getDicName() : metaDict.getTableName();
				sb.append("select ").append(uniqueDataField.getFieldName());
				sb.append(' ').append(tableName);
				sb.append(' ').append(" where ").append(pid);
				if(uniqueDataField.getDataType().equals("number")){
					sb.append('=').append(nodeID);
				}else{
					sb.append("='").append(nodeID).append('\'');
				}
				sb.append("))");
			}
			DictData data = dict.getDictData(bmName,sb.toString());
			List<Map<String, Object>> obj = new ArrayList<Map<String, Object>>();
			Map<String,String> treeKey = initTreeKey(request);
			if(onlyChild.equals("true")){
				obj = getTreeDatas(nodeID, data,false,treeKey);
				model.addObject("ResponseMessage",VOUtil.createResponseMessage(true, "查询成功",obj));
			}else{
				if(nodeID==null) throw new Exception("节点标识不存在");
				obj.add(getTreeNodeData(nodeID, data,false,treeKey));
				model.addObject("ResponseMessage",VOUtil.createResponseMessage(true, "查询成功",obj));
			}
		}catch (Exception e) {
			e.printStackTrace();
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
		}
		return model; 
	}
	
}
