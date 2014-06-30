package com.zdawn.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {
	/**
	 * 按下划线分开字符串
	 * <br>子串首字母大写其他字母小写
	 * <br>再合并字符串
	 * @param value 待处理字符串
	 * @param firstLower 转换后首字母是否小写
	 * @return 处理后字符串
	 */
	public static String convertStandardName(String value,boolean firstLower){
		String[] sub = value.split("_");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sub.length; i++) {
			sub[i] = sub[i].toLowerCase(); 
			if(i==0 && firstLower){
				sb.append(sub[i]);
			}else{
				sb.append((sub[i].charAt(0)+"").toUpperCase()).append(sub[i].substring(1));
			}
		}
		return sb.toString();
	}
	/**
	 * 首字母大写
	 */
	public static String firstUpperCase(String value){
		if(value==null) return value;
		String first = String.valueOf(value.charAt(0)).toUpperCase();
		if(value.length()==1) return first;
		return first+value.substring(1);
	}
	/**
	 * 首字母小写
	 */
	public static String firstLowerCase(String value){
		if(value==null) return value;
		String first = String.valueOf(value.charAt(0)).toLowerCase();
		if(value.length()==1) return first;
		return first+value.substring(1);
	}
	/**
	 * @param str 字符串
	 * @param separator 分隔符
	 * @return 字符串List
	 */
	public static List<String> convertString(String str,String separator) {
	    List<String> list = new ArrayList<String>();
	    String[] subValue = str.split(separator);
        for (int i = 0; i < subValue.length; i++) {
            list.add(subValue[i]);
        }
        return list;
	}
	/**
	 * 字符串 null or 空串 返回true
	 */
	public static boolean isEmpty(String value){
		if(value==null) return true;
		return value.equals("");
	}
	/**
	  * 四舍五入
	  * @param double a
	  * @param int scale 保留小数点后几位
	  */
	 public static double round(double a,int scale){
	 	BigDecimal bValue = new BigDecimal(a);
	 	return bValue.divide(new BigDecimal(1),scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	 }
 	/**
	 * 将给定Map集合中的key截断前缀,生成新的Map集合,原始集合key不会受到影响
	 * <br>如果key不是以前缀开头不会放到返回集合中
	 * @param prefix 前缀
	 * @param originPara 原始集合
	 * @param clearOrigin 是否清除原始集合内容
	 * @return Map&lt;String,String&gt;
	 */
	public static Map<String,String> truncateKeyPrefix(String prefix,Map<String,String> originPara,boolean clearOrigin){
		Map<String,String> para = new HashMap<String,String>(originPara.size());
		Set<Map.Entry<String,String>> set =originPara.entrySet();
		Iterator<Map.Entry<String,String>> it=set.iterator();
		while(it.hasNext()){
			Map.Entry<String, String>  entry= it.next();
			int index = entry.getKey().indexOf(prefix);
			if(index==-1) continue;
			index = index + prefix.length();
			para.put(entry.getKey().substring(index), entry.getValue());
		}
		if(clearOrigin) originPara.clear();
		return para;
	}
}
