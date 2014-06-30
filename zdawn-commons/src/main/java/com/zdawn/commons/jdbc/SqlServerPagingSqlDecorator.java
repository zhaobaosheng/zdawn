package com.zdawn.commons.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlServerPagingSqlDecorator implements PagingSqlDecorator {
	private final static Logger log = LoggerFactory.getLogger(SqlServerPagingSqlDecorator.class);
	/**
	 * 分页例子
	 * select * from (
     *   select row_number()over(order by (select 0)) temprownumber,*
     *   from (select top 30 * from HMHP_Road_2010) t
     *  ) tt
     *  where temprownumber>20
	 */
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
			begin = (currentPage - 1) * pageSize + 1;
			end = begin + pageSize;
			String nsql = weaveTopInOriginSql(sql,end);
			sb.append("select * from (");
			sb.append("select *,row_number() over(order by (select 0)) temprownumber ");
			sb.append("from (");
			sb.append(nsql);
			sb.append(") t ");
			sb.append(") tt ");
			sb.append("where temprownumber >"+begin);
			log.debug(sb.toString());
		}
		return sb.toString();
	}
	private String weaveTopInOriginSql(String originalSql,int rows){
		int index = 0;
		for (int i = 0; i < originalSql.length(); i++) {
			if(originalSql.charAt(i)=='s' || originalSql.charAt(i)=='S'){
				index = i;
				break;
			}
		}
		if(originalSql.length()>index+6){
			String select = originalSql.substring(index, index+6);
			if(select.equalsIgnoreCase("select")){
				return select + " top "+rows + originalSql.substring(index+6);
			}
		}
		return originalSql;
	}
}
