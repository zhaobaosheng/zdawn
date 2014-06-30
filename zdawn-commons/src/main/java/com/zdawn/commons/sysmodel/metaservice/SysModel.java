package com.zdawn.commons.sysmodel.metaservice;

import java.util.Map;

public interface SysModel {
	/**
	 * 返回版本
	 */
	public String getVersion();
	/**
	 * 配置信息
	 */
	public Map<String, String> getConfig();
	/**
	 * 全部实体信息
	 */
	public Map<String,Entity> getEntities();
	/**
	 * 通过表名查找实体
	 * @param tableName 表名
	 * @return Entity
	 */
	public Entity findEntityByTableName(String tableName);
	/**
	 * 通过实体名查找实体
	 * @param name 实体名
	 * @return Entity
	 */
	public Entity findEntityByName(String name);
}
