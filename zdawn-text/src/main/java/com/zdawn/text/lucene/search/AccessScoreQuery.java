package com.zdawn.text.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;
/**
 * 权限查询
 * @author zhaobs
 */
public class AccessScoreQuery extends CustomScoreQuery {
	/**
	 * 存储权限字段名
	 */
	private String accessFieldName = null;
	/**
	 * 授权的字段值,使用逗号分隔
	 * <br>每个权限值是prefix匹配
	 */
	private String accessValues = null;
	
	public AccessScoreQuery(Query subQuery,String accessFieldName,String accessValues){
		super(subQuery);
		this.accessFieldName = accessFieldName;
		this.accessValues = accessValues;
	}
	@Override
	protected CustomScoreProvider getCustomScoreProvider(
			AtomicReaderContext context) throws IOException {
		AccessQueryScoreProvider provide = new AccessQueryScoreProvider(context);
		provide.setAccessFieldName(accessFieldName);
		provide.setAccessValue(accessValues.split(","));
		provide.initDocValue();
		return provide;
	}
}
