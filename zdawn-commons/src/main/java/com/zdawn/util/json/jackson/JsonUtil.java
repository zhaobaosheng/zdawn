package com.zdawn.util.json.jackson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.util.Utils;
import com.zdawn.util.vo.VOUtil;

public class JsonUtil {
	private static Logger log = LoggerFactory.getLogger(JsonUtil.class);
	
	public static String createErrorResponseMessage(String errorDesc){
		return createErrorResponseMessage(null,errorDesc);
	}
	public static String createErrorResponseMessage(ObjectMapper mapper,String errorDesc){
		return createErrorResponseMessage(mapper, errorDesc,"");
	}
	public static String createErrorResponseMessage(ObjectMapper mapper,String desc,Object errorData){
		try {
			if(mapper==null) mapper = new ObjectMapper();
			return mapper.writeValueAsString(VOUtil.createResponseMessage(false,desc,errorData));
		} catch (Exception e) {
			log.error("createErrorResponseMessage", e);
		}
		return null;
	}
	public static String convertObjectToJsonString(Object data){
		return convertObjectToJsonString(null,data);
	}
	public static String convertObjectToJsonString(ObjectMapper mapper,Object data){
		String json = "";
		if(data==null) return json;
		try {
			if(mapper==null) mapper = new ObjectMapper();
			json = mapper.writeValueAsString(data);
		} catch (Exception e) {
			log.error("convertObjectToJsonString", e);
		}
		return json;
	}
	/**
	 * 解析JSON格式数据，可以单一对象或是数组对象
	 * @param mapper ObjectMapper 可以为null
	 * @param params 请求参数Json字符串
	 * <br>[{\"a\":\"123\"},{...}] 数组
	 * <br>{\"a\":\"123\"} 单一对象
	 * @return 集合
	 */
	public static List<Map<String,String>> parseArrayOrSingleRequestData(ObjectMapper mapper,String params){
		if(mapper==null) mapper = new ObjectMapper();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			JsonNode rootNode = null;
			if(params.charAt(0)=='['){
				rootNode = mapper.readValue(params, ArrayNode.class);
			}else{
				rootNode = mapper.readValue(params, JsonNode.class);
			}
			if(rootNode.isArray()){
				ArrayNode arrayNode = (ArrayNode)rootNode;
				for (int i = 0; i < arrayNode.size(); i++){
					JsonNode childNode = arrayNode.get(i);
					if(!childNode.isArray()) readObjectChildData(list,childNode);
				}
			}else{
				readObjectChildData(list,rootNode);
			}
		} catch (Exception e) {
			log.error("parseArrayOrSingleRequestData", e);
		}
		return list;
	}

	private static void readObjectChildData(List<Map<String, String>> childDatas,
			JsonNode obj) {
		if(childDatas==null) return;
		Map<String, String> child = new HashMap<String,String>();
		Iterator<String> it = obj.getFieldNames();
		while (it.hasNext()) {
			String name = it.next();
			JsonNode temp = obj.path(name);
			if(temp.isValueNode()) child.put(name,temp.asText());
			else if(temp.isArray()){
				String data = readSimpleArray((ArrayNode)temp);
				if(data!=null) child.put(name,data);
				else child.put(name,temp.toString());
			}
		}
		if(child.size()>0) childDatas.add(child);
	}
	private static String readSimpleArray(ArrayNode arrayNode) {
		if(arrayNode.size()>0){
			JsonNode temp = arrayNode.get(0);
			if(temp.isValueNode()){
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < arrayNode.size(); i++) {
					JsonNode aNode = arrayNode.get(i);
					if(i==0) sb.append(aNode.asText());
					else sb.append(',').append(aNode.asText());
				}
				return sb.toString();
			}
		}
		return null;
	}
	/**
	 * 解析通用返回格式数据，result和desc作为键值对返回，非基本数据类型返回json字符串。
	 * 注意数据中键值不能是result和desc
	 * @param params json格式数据
	 * @return Map&lt;String,String&gt;
	 */
	public static Map<String,String> parseResponseOneData(String params){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> responseData = null;
		try {
			JsonNode rootNode = mapper.readValue(params, JsonNode.class);
			responseData = new HashMap<String, String>();
			String value = rootNode.path("result").getTextValue();
			responseData.put("result", value);
			value = rootNode.path("desc").getTextValue();
			responseData.put("desc", value);
			JsonNode customerNode = rootNode.path("data");
			if(customerNode==null) return responseData;
			Iterator<String> it = customerNode.getFieldNames();
			while (it.hasNext()) {
				String name = it.next();
				JsonNode temp = customerNode.path(name);
				if(temp.isValueNode()){
					responseData.put(name, temp.asText());
				}else if(temp.isContainerNode()){
					if(temp.isArray()){
						ArrayNode arrayNode = (ArrayNode)temp;
						String simleData = readSimpleArray(arrayNode);
						if(simleData!=null){
							responseData.put(name, simleData);
						}else{
							responseData.put(name, arrayNode.toString());
						}
					}else{
						responseData.put(name, temp.toString());
					}
				}
			}
		} catch (Exception e) {
			log.error("parseResponseOneData", e);
		}
		return responseData;
	}
	/**
	 * 解析通用分页返回数据
	 * @param params json格式数据
	 * @return Map&lt;String,Object&gt;
	 * <br>result 返回结果 true or false
	 * <br>desc 结果说明
	 * <br>total 总记录数
	 * <br>dataList 返回数据 类型为 ArrayList&lt;Map&lt;String,String&gt;&gt;
	 */
	public static Map<String,Object> parseResponsePageData(String params){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> responseData = null;
		try {
			JsonNode rootNode = mapper.readValue(params, JsonNode.class);
			responseData = new HashMap<String, Object>();
			String value = rootNode.path("result").getTextValue();
			responseData.put("result", value);
			value = rootNode.path("desc").getTextValue();
			responseData.put("desc", value);
			JsonNode customerNode = rootNode.path("data");
			if(customerNode==null) return responseData;
			Iterator<String> it = customerNode.getFieldNames();
			while (it.hasNext()) {
				String name = it.next();
				JsonNode temp = customerNode.path(name);
				if(temp.isValueNode()){
					responseData.put(name, temp.asText());
				}else if(temp.isContainerNode()){
					if(temp.isArray()){
						ArrayNode arrayNode = (ArrayNode)temp;
						String simleData = readSimpleArray(arrayNode);
						if(simleData!=null){
							responseData.put(name, simleData);
						}else{
							List<Map<String, String>> childDatas = new ArrayList<Map<String,String>>();
							for (int i = 0; i < arrayNode.size(); i++){
								JsonNode childNode = arrayNode.get(i);
								if(childNode.isArray()) responseData.put(name, childNode.toString());
								else readObjectChildData(childDatas,childNode);
							}
							if(childDatas.size()>0) responseData.put(name,childDatas);
						}
					}else{
						responseData.put(name, temp.toString());
					}
				}
			}
		} catch (Exception e) {
			log.error("parseResponsePageData", e);
		}
		return responseData;
	}
	/**
	 * 通用json解析--将json数据解析键值对
	 * <br>当前对象属性键值对
	 * <br>子对象集合,包括1对1，1对多。
	 * @param jsonString json格式数据
	 * @param childData 子对象存放容器，如果为null不解析子对象。
	 * @return 主实体属性键值对
	 */
	public static Map<String,String> parseCommonJsonData(String jsonString,Map<String,Object> childData,String... includeChilds){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> mainData = null;
		try {
			JsonNode rootNode = mapper.readValue(jsonString, JsonNode.class);
			mainData = new HashMap<String, String>();
			Iterator<String> it = rootNode.getFieldNames();
			while (it.hasNext()) {
				String name = it.next();
				JsonNode temp = rootNode.path(name);
				if(temp.isValueNode()){
					mainData.put(name, temp.asText());
				}else if(temp.isContainerNode()){
					if(childData==null) continue;
					if(includeChilds!=null && !Utils.contains(includeChilds, name)) continue;
					if(temp.isArray()){
						ArrayNode arrayNode = (ArrayNode)temp;
						List<Map<String, String>> tmpDataList = new ArrayList<Map<String,String>>();
						for (int i = 0; i < arrayNode.size(); i++){
							JsonNode childNode = arrayNode.get(i);
							readObjectChildData(tmpDataList, childNode);
						}
						childData.put(name, tmpDataList);
					}else{
						readObjectOneChildData(childData,temp,name);
					}
				}
			}
			
		} catch (Exception e) {
			log.error("parseCommonJsonData", e);
		}
		return mainData;
	}
	private static void readObjectOneChildData(Map<String,Object> childData,JsonNode obj,String nodeName) {
		if(childData==null) return;
		Map<String, String> child = new HashMap<String,String>();
		Iterator<String> it = obj.getFieldNames();
		while (it.hasNext()) {
			String name = it.next();
			JsonNode temp = obj.path(name);
			if(temp.isValueNode()) child.put(name,temp.asText());
			else if(temp.isArray()){
				String data = readSimpleArray((ArrayNode)temp);
				if(data!=null) child.put(name,data);
				else child.put(name,temp.toString());
			}
		}
		if(child.size()>0) childData.put(nodeName, child);
	}

}
