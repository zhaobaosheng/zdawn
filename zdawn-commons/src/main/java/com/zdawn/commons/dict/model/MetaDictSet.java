package com.zdawn.commons.dict.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetaDictSet implements Serializable {
	private static final long serialVersionUID = -833483159546288823L;
	private List<MetaDict> listMetaDicts = new ArrayList<MetaDict>();
	public void addMetaDict(MetaDict metaDict){
		if(!listMetaDicts.contains(metaDict)){
			listMetaDicts.add(metaDict);
		}
	}
	public void removeMetaDict(MetaDict metaDict){
		listMetaDicts.remove(metaDict);
	}
	public void removeAll(){
		listMetaDicts.clear();
	}
	public MetaDict getMetaDict(String dictName){
		for (MetaDict metaDict : listMetaDicts) {
			if(metaDict.getDicName().equals(dictName)) return metaDict;
		}
		return null;
	}
	public int size(){
		return listMetaDicts.size();
	}
	public List<MetaDict> getListMetaDicts() {
		return listMetaDicts;
	}
}
