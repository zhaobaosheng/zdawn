package com.zdawn.commons.dict;

import java.net.URL;

import com.zdawn.commons.dict.model.DictMetaXMLLoader;
import com.zdawn.commons.dict.model.MetaDictSet;

public class MetaDictSetFactory {
	private static MetaDictSet metaDictSet = null;
	private MetaDictSetFactory(){
	}
	public static synchronized void loadFileSystemDataDictionary(String filePath){
		DictMetaXMLLoader loader = new DictMetaXMLLoader();
		metaDictSet = loader.loadFromXML(filePath);
	}
	public static synchronized void loadClassPathDataDictionary(String fileName){
		URL url = MetaDictSetFactory.class.getClassLoader().getResource(fileName);
		loadFileSystemDataDictionary(url.getPath());
	}
	public static MetaDictSet getMetaDictSet(){
		return metaDictSet;
	}
}
