package com.zdawn.util.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
	/**
	 * 正表达式缓存
	 */
	private static Map<Integer,Pattern> regxCache = new HashMap<Integer, Pattern>();
	
	private static int regxCacheSize = 100;
	/**
	 * 必填验证
	 * @param value 被验证对象
	 * @param desc 被验证对象描述 
	 * @param errorMsg 验证不通过提示信息
	 * @return 
	 * <br/>index 0  验证是否通过  true or false
	 * <br/>index 1 提示信息
	 * <br/>如果desc为 null 返回 errorMsg
	 * <br/>否则 返回 desc+必须填写
	 */
	public static String[] validateRequire(Object value,String desc,String errorMsg){
		if(value==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
		if(value.equals("")) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
		return new String[]{"true",null};
	}
	
	/**
	 * 长度验证
	 * @param value 被验证对象
	 * @param require 
	 * <br>true 必填验证 验证数据不能为空
	 * <br>false 验证数据可为空,如果为空验证通过
	 * @param minLength 最小长度  -1 不验证
	 * @param maxLength 最大长度  -1 不验证
	 * @param desc 被验证对象描述 
	 * @param errorMsg 验证不通过提示信息
	 * @return 
	 * <br>index 0  验证是否通过  true or false
	 * <br>index 1 提示信息
	 * <br>如果desc为 null 返回 errorMsg
	 * <br>否则 返回 desc+长度不正确
	 */
	public static String[] validateLength(Object value,boolean require,int minLength,int maxLength,String desc,String errorMsg){
		if(minLength==-1 && maxLength==-1) return new String[]{"true",null};
		if(require){
			if(value==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
			String temp = value.toString();
			if(minLength >= 0 && temp.length() < minLength) return new String[]{"false",desc==null ? errorMsg:desc.concat("长度不正确，>"+minLength)};
			if(minLength >= 0 && temp.length() > maxLength) return new String[]{"false",desc==null ? errorMsg:desc.concat("长度不正确，<"+minLength)};
		}
		return new String[]{"true",null};
	}
	
	/**
	 * 正则表达式验证
	 * @param value 被验证对象
	 * @param require 
	 * <br>true 必填验证 验证数据不能为空
	 * <br>false 验证数据可为空,如果为空验证通过
	 * @param expression 正则表达式
	 * @param desc 被验证对象描述 
	 * @param errorMsg 验证不通过提示信息
	 * @return 
	 * <br>index 0  验证是否通过  true or false
	 * <br>index 1 提示信息
	 * <br>如果desc为 null 返回 errorMsg
	 * <br>否则 返回 desc+填写格式不正确
	 */
	public static String[] validateRegx(Object value,boolean require,String expression,String desc,String errorMsg){
		if(expression==null || expression.length()==0) return new String[]{"true",null};
		if(require){
			if(value==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
			String temp = value.toString();
			Pattern pattern = getRegxPatternByExpression(expression);
			Matcher matcher = pattern.matcher(temp);
			if(!matcher.matches()) return new String[]{"false",desc==null ? errorMsg:desc.concat("填写格式不正确")};
		}
		return new String[]{"true",null};
	}
	/**
	 * 获取正则Pattern
	 * @param expression 正则表达式
	 * @return Pattern
	 */
	public static Pattern getRegxPatternByExpression(String expression){
		Integer key = new Integer(expression.hashCode());
		Pattern pattern = regxCache.get(key);
		if(pattern!=null) return pattern;
		pattern = Pattern.compile(expression);
		if(regxCache.size()<=regxCacheSize){
			regxCache.put(key, pattern);
		}else{
			System.out.println("tip ......  regxCache size is overflow !");
		}
		return pattern;
	}
	/**
	 * 简单枚举验证
	 * @param value 被验证对象
	 * @param require 
	 * <br>true 必填验证 验证数据不能为空
	 * <br>false 验证数据可为空,如果为空验证通过
	 * @param content 枚举值域 用逗号分隔
	 * @param desc 被验证对象描述 
	 * @param errorMsg 验证不通过提示信息
	 * @return 
	 * <br>index 0  验证是否通过  true or false
	 * <br>index 1 提示信息
	 * <br>如果desc为 null 返回 errorMsg
	 * <br>否则 返回 desc+范围不正确
	 */
	public static String[] validateExcept(Object value,boolean require,String content,String desc,String errorMsg){
		if(require){
			if(value==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
		}
		String temp = value.toString()+',';
		if(content.indexOf(temp)==-1) return new String[]{"false",desc==null ? errorMsg:desc.concat("范围不正确")};
		return new String[]{"true",null};
	}
	/**
	 * 匹配 中文字符（包含中文字符）
	 * @param value 被验证对象
	 * @param require 
	 * <br>true 必填验证，验证数据不能为空
	 * <br>false 验证数据可为空,如果为空验证通过
	 * @param desc 被验证对象描述
	 * @param errorMsg 验证不通过提示信息
	 * @return
	 * <br>index 0  验证是否通过  true or false
	 * <br>index 1 提示信息
	 * <br>如果desc为 null 返回 errorMsg
	 * <br>否则 返回 desc+填写格式不正确
	 * @author sunde
	 */
	public static String[] validateChStr(Object value,boolean require,String desc,String errorMsg){
		String eps = "[\\u4e00-\\u9fa5]";
		if(require){
			if(value==null) return new String[]{"false",desc==null ? errorMsg:desc.concat("必须填写")};
			String temp = value.toString();
			Pattern pattern = getRegxPatternByExpression(eps);
			Matcher matcher = pattern.matcher(temp);
			if(!matcher.find()) return new String[]{"false",desc==null ? errorMsg:desc.concat("填写格式不正确")};
		}
		return new String[]{"true",null};
	} 
}
