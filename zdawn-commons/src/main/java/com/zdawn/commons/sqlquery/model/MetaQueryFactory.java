package com.zdawn.commons.sqlquery.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import com.zdawn.util.ResourceUtil;
/**
 * 读取查询配置模型
 * @author zhaobs
 */
public class MetaQueryFactory {
	private static MetaQuery metaQuery = null;
	
	private MetaQueryFactory(){
	}
	/**
	 * 从文件系统加载查询定义
	 * @param filePath 路径
	 */
	public static synchronized void loadQueryConfigFromFileSystem(String filePath){
		QueryConfigXMLLoader loader = new QueryConfigXMLLoader();
		metaQuery = loader.loadFromXML(filePath);
	}
	public static synchronized void loadQueryConfigFromFileSystem(String filePath,String regxFileName){
		QueryConfigXMLLoader loader = new QueryConfigXMLLoader();
		ArrayList<File> listFiles =  ResourceUtil.findAllFiles(filePath, regxFileName);
		if(metaQuery==null) metaQuery = new MetaQuery();
		for (File file : listFiles) {
			MetaQuery tmp = loader.loadFromXML(file);
			metaQuery.getResultMappers().putAll(tmp.getResultMappers());
			metaQuery.getQueryConfigs().putAll(tmp.getQueryConfigs());
		}
	}
	/**
	 * 从classpath加载查询定义
	 * @param fileName 验证文件名
	 */
	public static synchronized void loadQueryConfigFromClassPath(String fileName){
		URL url = MetaQueryFactory.class.getClassLoader().getResource(fileName);
		loadQueryConfigFromFileSystem(url.getPath());
	}
	/**
	 * 使用正则表达式从classpath装载查询定义
	 * @param regexFileName 文件名正则表达式
	 */
	public static synchronized void loadQueryConfigFromClassPathByRegexFileName(String regexFileName){
		try {
			Enumeration<URL> e = MetaQueryFactory.class.getClassLoader().getResources("");
			while(e.hasMoreElements()){
				String path = e.nextElement().getPath();
				loadQueryConfigFromFileSystem(path,regexFileName);
			}
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	public static MetaQuery getMetaQuery() {
		return metaQuery;
	}
	
	public static void main(String[] arg){
		loadQueryConfigFromClassPathByRegexFileName("Sql-Query\\w*\\.xml");
	}
}
