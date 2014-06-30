package com.zdawn.commons.sysmodel.persistence.spring;

import org.springframework.transaction.support.ResourceHolderSupport;

import com.zdawn.commons.sysmodel.persistence.SqlSession;

public class SqlSessionHolder extends ResourceHolderSupport{
	private SqlSession sqlSession = null;
	
	public SqlSessionHolder(SqlSession sqlSession){
		this.sqlSession = sqlSession;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}
	
}
