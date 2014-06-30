package com.zdawn.commons.sysmodel.metaservice;

public interface Property {
	/**
	 * true-启用 false-停用
	 */
	public boolean isUsing();
	/**
	 * 返回属性名
	 */
	public String getName();
	/**
	 * 返回字段名
	 */
	public String getColumn();
	/**
	 * 返回属性描述
	 */
	public String getDescription();
	/**
	 * 返回字段数据类型
	 */
	public String getType();
	/**
	 * 返回字段长度
	 */
	public int getLength();
	/**
	 * 返回字段精度
	 */
	public int getScale();
	/**
	 * 是否为空
	 * <br>false可为空
	 * <br>true不能为空
	 */
	public boolean isNotNull();
	/**
	 * 缺省值
	 */
	public String getDefaultValue();
	/**
	 * 转换String格式
	 */
	public String getToStringformat();
	/**
	 * 返回字段引用编码或者表信息
	 */
	public Reference getReference();
}
