package com.zdawn.casclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class URLUtils {
	/**
	 * 判断当前的URL是否在排除列表中
	 * <br>只支持路径匹配
	 * @param currentURL 当前的URL
	 * @param exceptList 排除列表
	 * @return boolean
	 */
	public static boolean exceptURL(String currentURL,List<String> exceptList){
		for (String temp : exceptList) {
			int index = temp.indexOf("*");
			if(index>0){
				String sub = temp.substring(0,index);
				if(currentURL.startsWith(sub)&&currentURL.length()>sub.length()) return true;
			}else{
				if(currentURL.equals(temp)) return true;
			}
		}
		return false;
	}
	public static String replaceIP(String url,String ipAddress){
		try {
			URL u = new URL(url);
			StringBuilder sb  = new StringBuilder();
			sb.append(u.getProtocol()).append("://").append(ipAddress);
			sb.append(u.getPort()<0 ? "":":"+u.getPort()).append(u.getPath());
			if(u.getQuery()!=null) sb.append('?').append(u.getQuery());
			return sb.toString();
		} catch (MalformedURLException e) {
			System.out.println("replaceIP error url="+url);
		}
		return "";
	}
}
