package com.zdawn.commons.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OraclePagingSqlDecorator implements PagingSqlDecorator {
	private final static Logger log = LoggerFactory.getLogger(OraclePagingSqlDecorator.class);
	@Override
	public String decoratePagingSql(String sql, int pageSize, int total,
			int currentPage) {
		int begin = 0;
		int end = 0;
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
			begin = (currentPage - 1) * pageSize;
			end = begin + pageSize;
			sb.append("select * from (select b.*,rownum r from (");
			sb.append(sql);
			sb.append(") b ) where r between ").append(begin+1);
			sb.append(" and ").append(end);
			log.debug(sb.toString());
		}
		return sb.toString();
	}
}
