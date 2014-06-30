package com.zdawn.commons.jdbc;

/**
 * 分页sql装饰接口
 * @author zhaobs
 */
public interface PagingSqlDecorator {
	/**
	 * 装饰分页sql
	 * @param sql 原sql
	 * @param pageSize 每页数据数
	 * @param total 总记录数
	 * @param currentPage 当前页数
	 * @return 与数据库相关分页sql
	 */
	public String decoratePagingSql(String sql, int pageSize, int total, int currentPage);
}
