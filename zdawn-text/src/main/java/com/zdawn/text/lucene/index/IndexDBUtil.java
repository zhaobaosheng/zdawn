package com.zdawn.text.lucene.index;

import java.io.File;
import java.util.ArrayList;

public class IndexDBUtil {
	public static long sizeOfDirectory(File dirFile){
		long foldersize = 0;
		ArrayList<File> subFoder = new ArrayList<File>();
		subFoder.add(dirFile);
		while(true){
			ArrayList<File> temp = new ArrayList<File>();
			for (File file : subFoder) {
				if(file.isDirectory()){//文件夹
					File[] subFile = file.listFiles();
					for (int i = 0; i < subFile.length; i++) {
						if(subFile[i].isDirectory()) temp.add(subFile[i]);
						else{
							foldersize += subFile[i].length();
						}
					}
				}else{//文件
					foldersize += file.length();
				}
			}
			//如果没有子目录退回循环
			if(temp.size()==0) break;
			subFoder = null;
			subFoder = temp;
		}
		return foldersize;
	}
	/**
	 * 计算文件夹大小 忽略文件夹大小
	 * @param path 路径
	 * @return 文件夹字节数
	 */
	public static long sizeOfDirectory(String path){
		return sizeOfDirectory(new File(path));
	}
	
	public static String createNewFolder(String path) {
		File parentFile = new File(path);
		File[] list = parentFile.listFiles();
		int max = 1000;
		for (File file : list) {
			if(file.isFile()) continue;
			int current = Integer.parseInt(file.getName());
			if(current >max) max = current;
		}
		return String.valueOf(max+1);
	}
	public static String getFileName(String filePath){
		File file = new File(filePath);
		return file.getName();
	}
	public static String getFileExtName(String filePath){
		File file = new File(filePath);
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if(index==-1) return "";
		return name.substring(index+1);
	}
}
