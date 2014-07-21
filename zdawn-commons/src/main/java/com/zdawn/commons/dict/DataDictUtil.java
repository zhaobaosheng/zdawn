package com.zdawn.commons.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.zdawn.commons.dict.model.DataField;
import com.zdawn.commons.dict.model.DictData;
import com.zdawn.commons.dict.model.PropertyValue;
import com.zdawn.commons.dict.model.RecordData;
import com.zdawn.util.json.jackson.JsonUtil;
import com.zdawn.util.spring.SpringHelper;
/**
 * 数据字典工具类
 * <br>为下拉框提供数据
 * <br>将数据转换成JSON串
 * @author zhaobs
 */
public class DataDictUtil {
	private static Logger log = LoggerFactory.getLogger(DataDictUtil.class);
	/**
	 * 获取一个指定数据字典数据
	 * @param bmName 数据字典表名
	 * @return ArrayList&lt;PropertyValue&gt;
	 */
	public static ArrayList<PropertyValue> getComboboxData(String bmName){
		Map<String,ArrayList<PropertyValue>> map = getComboboxDataBybmNames(bmName);
		return map.get(bmName);
	}
	//数据字段数据Json串
	public static String getOneComboboxDataJsonString(String bmName){
		Map<String,ArrayList<PropertyValue>> map = getComboboxDataBybmNames(bmName);
		return JsonUtil.convertObjectToJsonString(map.get(bmName));
	}
	//数据字段数据Json串
	public static String getComboboxDataJsonString(String... bmNames){
		Map<String,ArrayList<PropertyValue>> map = getComboboxDataBybmNames(bmNames);
		return JsonUtil.convertObjectToJsonString(map);
	}
	/**
	 * 编码数据字典枚举验证
	 * @param value 被验证对象
	 * @param require 
	 * <br>true 必填验证 验证数据不能为空
	 * <br>false 验证数据可为空,如果为空验证通过
	 * @param dicName 编码数据字典名称
	 * @param desc 被验证对象描述 
	 * @param errorMsg 验证不通过提示信息
	 * @return 
	 * <br>index 0  验证是否通过  true or false
	 * <br>index 1 提示信息
	 * <br>如果desc为 null 返回 errorMsg
	 * <br>否则 返回 desc+范围不正确
	 */
	public static String[] validateExcept(Object value,boolean require,String dicName,String desc,String errorMsg){
		if(require){
			if(value==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
		}
		ApplicationContext context = SpringHelper.getContext();
		DataDictionary dict = context.getBean("dataDictionary", DataDictionary.class);
		DictData dicData  = dict.getDictData(dicName);
		if(dicData==null)  return new String[]{"false",dicName+"编码字典不存在！"};
		String temp = value.toString();
		if(dicData.getRecord(temp)==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("范围不正确")};
		return new String[]{"true",null};
	}
	/**
	 * 获取一组数据字典数据
	 * @param bmNames 数据字典表名
	 * @return Map&lt;String,ArrayList&lt;PropertyValue&gt;&gt;
	 */
	public static Map<String,ArrayList<PropertyValue>> getComboboxDataBybmNames(String... bmNames){
		Map<String,ArrayList<PropertyValue>> map = new HashMap<String,ArrayList<PropertyValue>>();
		if(bmNames==null) return map;
		try {
			ApplicationContext context = SpringHelper.getContext();
			DataDictionary dict = context.getBean("dataDictionary", DataDictionary.class);
			for (int i = 0; i < bmNames.length; i++) {
				String bmTableName = bmNames[i];
				DictData data = dict.getDictData(bmTableName);
				DataField df = data.getMetaDict().getUniqueDataField();
				map.put(bmTableName, getBMJsonData(df,data));
			}
		}catch (Exception e) {
			log.error("getComboboxData",e);
		}
		return map;
	}
	private static ArrayList<PropertyValue> getBMJsonData(DataField df,
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
			StringBuilder disp = new StringBuilder();
			for (int j = 0; j < listDispField.size(); j++) {
				DataField field = listDispField.get(j);
				disp = j==0 ? disp.append(row.getValue(field.getFieldName())):
					disp.append("-"+row.getValue(field.getFieldName()));
			}
			pv.setText(disp.toString());
			al.add(pv);
		}
		return al;
	}
}
