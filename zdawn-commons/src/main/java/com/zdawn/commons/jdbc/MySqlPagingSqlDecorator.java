package com.zdawn.commons.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlPagingSqlDecorator implements PagingSqlDecorator {
	private final static Logger log = LoggerFactory.getLogger(MySqlPagingSqlDecorator.class);
	/**
	 * select * from (select * from xx_config) t limit m,n
	 * m:从多少条开始，从零开始
	 * n:取n条数据
	 */
	@Override
	public String decoratePagingSql(String sql, int pageSize, int total,
			int currentPage) {
		int begin = 0;
		int pageCount = 0;
		StringBuilder sb = new StringBuilder();
		if (pageSize > 0 && total > pageSize) {
			if (total % pageSize == 0)
				pageCount = total / pageSize;
			else
				pageCount = total / pageSize + 1;

			if (currentPage < 1)
				currentPage = 1;
			if (currentPage > pageCount) {
				currentPage = pageCount;
			}
			begin = (currentPage - 1) * pageSize + 1;
			sb.append("select * from (");
			sb.append(sql);
			sb.append(") t limit ");
			sb.append(begin).append(',');
			sb.append(pageSize);
			log.debug(sb.toString());
		}
		return sb.toString();
	}

}
