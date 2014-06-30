package com.zdawn.commons.sysmodel.metaservice;

public interface Relation {
	/**
	 * 返回当前实体关联属性名
	 */
	public String getSelfPropertyName();
	/**
	 * 返回当前实体关联字段名
	 */
	public String getSelfColumn();
	/**
	 * 返回实体关系描述
	 */
	public String getDescription();
	/**
	 * 返回关联实体名称
	 */
	public String getEntityName();
	/**
	 * 返回关联表名
	 */
	public String getTableName();
	/**
	 * 返回关联属性名
	 */
	public String getPropertyName();
	/**
	 * 返回关联字段名
	 */
	public String getColumn();
	/**
	 * 返回关系类型
	 * <br>oneToMany 一对多
	 * <br>oneToOne 一对一
	 */
	public String getType();
}
