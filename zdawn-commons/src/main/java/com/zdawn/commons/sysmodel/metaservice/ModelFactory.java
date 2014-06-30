package com.zdawn.commons.sysmodel.metaservice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;

public class ModelFactory {
	private static SysModel sysModel = null;
	
	private ModelFactory(){
	}
	/**
	 * 从文件系统加载实体模型
	 * @param filePath 路径
	 */
	public static synchronized void loadQueryConfigFromFileSystem(String filePath){
		SysModelXMLLoader loader = new SysModelXMLLoader();
		sysModel = loader.loadFromXML(filePath);
	}
	public static synchronized void loadQueryConfigFromFileSystem(String filePath,String regxFileName){
		SysModelXMLLoader loader = new SysModelXMLLoader();
		ArrayList<File> listFiles =  findAllFiles(filePath, regxFileName);
		if(sysModel==null) sysModel = new SysModelImpl();
		for (File file : listFiles) {
			SysModel tmp = loader.loadFromXML(file);
			sysModel.getConfig().putAll(tmp.getConfig());
			sysModel.getEntities().putAll(tmp.getEntities());
		}
	}
	/**
	 * 从classpath加载实体模型
	 * @param fileName 配置文件名
	 */
	public static synchronized void loadQueryConfigFromClassPath(String fileName){
		URL url = ModelFactory.class.getClassLoader().getResource(fileName);
		loadQueryConfigFromFileSystem(url.getPath());
	}
	/**
	 * 使用正则表达式从classpath装载实体模型
	 * @param regxFileName 文件名正则表达式
	 */
	public static synchronized void loadQueryConfigFromClassPathByRegexFileName(String regxFileName){
		try {
			Enumeration<URL> e = ModelFactory.class.getClassLoader().getResources("");
			while(e.hasMoreElements()){
				String path = e.nextElement().getPath();
				loadQueryConfigFromFileSystem(path,regxFileName);
			}
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	public static SysModel getSysModel() {
		return sysModel;
	}
	/**
	 * 在给定路径中查找匹配正则表达式文件
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
		loadQueryConfigFromClassPathByRegexFileName("DataModel\\w*\\.xml");
	}
}
