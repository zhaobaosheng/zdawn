package com.zdawn.commons.sqlquery.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
		ArrayList<File> listFiles =  findAllFiles(filePath, regxFileName);
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
	/**
	 * 在给定路径中查找匹配正则表达式文件,不会有目录
	 * @param path 文件路径
	 * @param regxFileName 正则表达式
	 * @return ArrayList&lt;File&gt;
	 */
	public static ArrayList<File> findAllFiles(String path,String regxFileName){
		ArrayList<File> listFiles = new ArrayList<File>();
		Pattern pattern = Pattern.compile(regxFileName);
		ArrayList<File> subFoder = new ArrayList<File>();
		subFoder.add(new File(path));
		while(true){
			ArrayList<File> temp = new ArrayList<File>();
			for (File file : subFoder) {
				if(file.isDirectory()){//如果是文件夹,遍历子文件
					File[] subFile = file.listFiles();
					for (int i = 0; i < subFile.length; i++) {
						if(subFile[i].isDirectory()) temp.add(subFile[i]);
						else{
							Matcher matcher = pattern.matcher(subFile[i].getName());
							if(matcher.matches()) listFiles.add(subFile[i]);
						}
					}
				}else{
					Matcher matcher = pattern.matcher(file.getName());
					if(matcher.matches()) listFiles.add(file);
				}
			}
			//如果没有子目录退回循环，有赋值subFoder变量
			if(temp.size()==0) break;
			subFoder = null;
			subFoder = temp;
		}
		return listFiles;
	}
	public static void main(String[] arg){
		loadQueryConfigFromClassPathByRegexFileName("Sql-Query\\w*\\.xml");
	}
}
