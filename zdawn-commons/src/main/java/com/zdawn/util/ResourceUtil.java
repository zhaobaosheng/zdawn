package com.zdawn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceUtil {
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
	/**
	 在给定路径中查找给定文件名的文件
	 * @param path 文件路径
	 * @param regxFileName 正则表达式
	 * @param findOnlyOne 是否找到一个就返回
	 * @return ArrayList&lt;File&gt;
	 */
	public static ArrayList<File> findFiles(String path,String fileName,boolean findOnlyOne){
		ArrayList<File> listFiles = new ArrayList<File>();
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
							if(subFile[i].getName().equals(fileName)) listFiles.add(subFile[i]);
						}
					}
				}else{
					if(file.getName().equals(fileName)) listFiles.add(file);
				}
			}
			if(findOnlyOne && listFiles.size()==1) return listFiles;
			//如果没有子目录退回循环，有赋值subFoder变量
			if(temp.size()==0) break;
			subFoder = null;
			subFoder = temp;
		}
		return listFiles;
	}
	/**
	 * 从classpath中读取属性文件 编码格式utf-8
	 * @param resourceName 属性文件名
	 * @return 如果文件内容为空返回null
	 */
	public static Map<String,String> readPropertyFileFromClassPath(String resourceName){
		URL url = ResourceUtil.class.getClassLoader().getResource("");
		ArrayList<File> list = ResourceUtil.findFiles(url.getPath(),resourceName,true);
		return readPropertyFile(list.get(0));
	}
	public static Map<String,String> readPropertyFile(String filePath){
		File file = new File(filePath);
		return readPropertyFile(file);
	}
	/**
	 * 读取属性文件 编码格式utf-8
	 * @param filePath 文件路径
	 * @return 如果文件内容为空返回null
	 */
	public static Map<String,String> readPropertyFile(File file){
		Map<String,String> mapValue = new HashMap<String, String>();
		InputStreamReader isRead = null;
		BufferedReader reader = null;
		try{
			if(file.isFile()&&file.exists()){
				isRead = new InputStreamReader(new FileInputStream(file),"UTF-8");
				reader=new BufferedReader(isRead);
				String line = null;
				while((line=reader.readLine())!=null){
					if(line.startsWith("#") || line.length()==0) continue;
					String[] temp = line.split("=");
					if(temp.length>1){
						mapValue.put(temp[0].trim(), temp[1].trim());
					}
				}
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}finally{
			try {
				isRead.close();
				reader.close();
			} catch (IOException e) {}
		}
		return mapValue.size()>0 ? mapValue:null;
	}
}
