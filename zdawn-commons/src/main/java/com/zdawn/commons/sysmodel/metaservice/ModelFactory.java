package com.zdawn.commons.sysmodel.metaservice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.util.ResourceUtil;

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
		ArrayList<File> listFiles =  ResourceUtil.findAllFiles(filePath, regxFileName);
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
	
	public static void main(String[] arg){
		loadQueryConfigFromClassPathByRegexFileName("DataModel\\w*\\.xml");
	}
}
