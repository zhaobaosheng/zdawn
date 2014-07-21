package com.zdawn.tools.gencode.model;

import java.util.ArrayList;
import java.util.List;

public class CodeModel {
	private  Config config;
	private List<String> importEntities;
	private List<MetaClazz> metaClazzes;
	/***
	 * 根据一组类名查找类实例
	 * @param names 类名
	 * @return List&lt;MetaClazz&gt;
	 */
	public List<MetaClazz> getMetaClazzesByName(String[] name){
		List<MetaClazz> result = new ArrayList<MetaClazz>();
		if(metaClazzes!=null){
			for (int i = 0; i < metaClazzes.size(); i++) {
				MetaClazz m =  metaClazzes.get(i);
				for (int j = 0; j < name.length; j++) {
					if(m.getName().equalsIgnoreCase(name[j])){
						result.add(m);
					}
				}
			}
		}
		return result;
	}
	/***
	 * 根据类名找类对象
	 * @param name 类名
	 * @return MetaClazz
	 */
	public MetaClazz getMetaClazzesByName(String name){
		if(metaClazzes!=null){
			for (int i = 0; i < metaClazzes.size(); i++) {
				MetaClazz m =  metaClazzes.get(i);
				if(m.getName().equalsIgnoreCase(name)) return m;
			}
		}
		return null;
	}
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public List<MetaClazz> getMetaClazzes() {
		return metaClazzes;
	}
	public void addMetaClazz(MetaClazz metaClazz){
		if(metaClazzes==null) metaClazzes = new ArrayList<MetaClazz>();
		metaClazzes.add(metaClazz);
	}
	public void setMetaClazzes(List<MetaClazz> metaClazzes) {
		this.metaClazzes = metaClazzes;
	}
	public List<String> getImportEntities() {
		return importEntities;
	}
	public void setImportEntities(List<String> importEntities) {
		this.importEntities = importEntities;
	}
	public void addImportEntity(String value){
		if(importEntities==null) importEntities = new ArrayList<String>();
		importEntities.add(value);
	}
}
