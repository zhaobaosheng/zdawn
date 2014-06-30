package com.zdawn.commons.sysmodel.metaservice.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.Relation;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.util.Utils;

public class SysModelImpl implements SysModel {
	private final Logger log = LoggerFactory.getLogger(SysModelImpl.class);
	/**
     * 版本
     */
    private String version;
    /**
     * 配置信息
     */
    private Map<String,String> config = new HashMap<String, String>();
    /**
     * 实体信息集合
     */
    private Map<String,Entity> entities = new HashMap<String, Entity>();
    /**
     * 添加 实体，会覆盖实体名称相同实体
     * @param entity 实体
     */
    public void addEntity(Entity entity){
    	if(entities.containsKey(entity.getName())){
    		log.warn("实体已经存在且被覆盖 名称="+entity.getName());
    	}
    	entities.put(entity.getName().toUpperCase(),entity);
    }
    /**
     * 增加配置信息,如果key相同覆盖已经存在
     * @param key
     * @param value
     */
    public void addConfig(String key,String value){
    	config.put(key, value);
    }
    /**
     * 初始化实体配置信息
     */
    public void initAndValidateEntityInfo(){
    	for (Entry<String,Entity> entry: entities.entrySet()) {
    		Entity currentEntity = entry.getValue();
    		//初始化关系对象
    		List<Relation> list = currentEntity.getRelations();
    		for (Relation relation : list) {
    			RelationImpl relationImpl = (RelationImpl)relation;
    			Property temp = currentEntity.findPropertyByColumn(relation.getSelfColumn());
    			if(temp==null) throw new RuntimeException("实体"+currentEntity.getName()+"属性"+relation.getSelfColumn()+"不存在");
    			relationImpl.setSelfPropertyName(temp.getName());
    			//设置关联实体和属性
    			Entity relEntity = null;
    			if(!Utils.isEmpty(relation.getEntityName())){
    				relEntity = findEntityByName(relation.getEntityName());
    				if(relEntity==null) throw new RuntimeException("实体"+relation.getEntityName()+"不存在");
    				if(Utils.isEmpty(relation.getTableName())) relationImpl.setTableName(relEntity.getTableName());
    			}else{
    				relEntity = findEntityByTableName(relation.getTableName());
    				if(relEntity==null) throw new RuntimeException("表名"+relation.getTableName()+"不存在");
        			if(Utils.isEmpty(relation.getEntityName())) relationImpl.setEntityName(relEntity.getName());
    			}
    			if(!Utils.isEmpty(relation.getPropertyName())){
    				Property relProperty = relEntity.findPropertyByName(relation.getPropertyName());
    				if(relProperty==null) throw new RuntimeException("实体"+relation.getEntityName()+"属性"+relation.getPropertyName()+"不存在");
        			if(Utils.isEmpty(relation.getColumn())) relationImpl.setColumn(relProperty.getColumn());
    			}else{
    				Property relProperty = relEntity.findPropertyByColumn(relation.getColumn());
    				if(relProperty==null) throw new RuntimeException("表名"+relation.getTableName()+"字段"+relation.getColumn()+"不存在");
    				if(Utils.isEmpty(relation.getPropertyName())) relationImpl.setPropertyName(relProperty.getName());
    			}
    		}
    		//初始化引用对象
    		List<Property> listProperty = currentEntity.getProperties();
    		for (Property property : listProperty) {
				if(property.getReference()==null) continue;
				ReferenceImpl referenceImpl= (ReferenceImpl)property.getReference();
				Entity refEntity = null;
				if(!Utils.isEmpty(referenceImpl.getEntityName())){
					refEntity = findEntityByName(referenceImpl.getEntityName());
					if(refEntity==null) throw new RuntimeException("实体"+referenceImpl.getEntityName()+"不存在");
					if(Utils.isEmpty(referenceImpl.getTableName())) referenceImpl.setTableName(refEntity.getTableName());
				}else{
					refEntity = findEntityByTableName(referenceImpl.getTableName());
					if(refEntity==null) throw new RuntimeException("表名"+referenceImpl.getTableName()+"不存在");
					if(Utils.isEmpty(referenceImpl.getEntityName())) referenceImpl.setEntityName(refEntity.getName());
				}
				//引用字段
				if(!Utils.isEmpty(referenceImpl.getPropertyName())){
					Property refProperty = refEntity.findPropertyByName(referenceImpl.getPropertyName());
					if(refProperty==null) throw new RuntimeException("实体"+referenceImpl.getEntityName()+"属性"+referenceImpl.getPropertyName()+"不存在");
					if(Utils.isEmpty(referenceImpl.getColumn())) referenceImpl.setColumn(refProperty.getColumn());
				}else{
					Property refProperty = refEntity.findPropertyByColumn(referenceImpl.getColumn());
					if(refProperty==null) throw new RuntimeException("表名"+referenceImpl.getTableName()+"字段"+referenceImpl.getColumn()+"不存在");
					if(Utils.isEmpty(referenceImpl.getPropertyName())) referenceImpl.setPropertyName(refProperty.getName());
				}
				//引用显示字段
				if(!Utils.isEmpty(referenceImpl.getDisplayPropertyName())){
					Property refProperty = refEntity.findPropertyByName(referenceImpl.getDisplayPropertyName());
					if(refProperty==null) throw new RuntimeException("实体"+referenceImpl.getEntityName()+"属性"+referenceImpl.getDisplayPropertyName()+"不存在");
					if(Utils.isEmpty(referenceImpl.getDisplayColumn())) referenceImpl.setDisplayColumn(refProperty.getColumn());
				}else{
					Property refProperty = refEntity.findPropertyByColumn(referenceImpl.getDisplayColumn());
					if(refProperty==null) throw new RuntimeException("表名"+referenceImpl.getTableName()+"字段"+referenceImpl.getDisplayColumn()+"不存在");
					if(Utils.isEmpty(referenceImpl.getDisplayPropertyName())) referenceImpl.setDisplayPropertyName(refProperty.getName());
				}
			}
    	}
    }
    public Entity findEntityByName(String name){
    	for (Entry<String,Entity> entry: entities.entrySet()) {
    		Entity temp = entry.getValue();
    		if(name.equalsIgnoreCase(temp.getName())) return temp;
    	}
    	return null;
    }
    public Entity findEntityByTableName(String tableName){
    	for (Entry<String,Entity> entry: entities.entrySet()) {
    		Entity temp = entry.getValue();
    		if(tableName.equalsIgnoreCase(temp.getTableName())) return temp;
    	}
    	return null;
    }
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Map<String, String> getConfig() {
		return config;
	}
	public void setConfig(Map<String, String> config) {
		this.config = config;
	}
	public Map<String,Entity> getEntities() {
		return entities;
	}
}
