package com.zdawn.commons.sysmodel.persistence.spring;

import static org.springframework.util.Assert.notNull;

import org.springframework.dao.support.DaoSupport;

import com.zdawn.commons.sysmodel.persistence.SqlSession;
import com.zdawn.commons.sysmodel.persistence.SqlSessionFactory;

public class SqlSessionDaoSupport extends DaoSupport {
	
	private SqlSessionTemplate sqlSession;
	
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
	    if (this.sqlSession==null && sqlSessionFactory!=null) {
	      this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
	    }
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
	    this.sqlSession = sqlSessionTemplate;
	}
	
	public SqlSession getSqlSession() {
	   return this.sqlSession;
	}
	
	public SqlSessionTemplate getSqlSessionTemplate(){
		return this.sqlSession;
	}
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		notNull(this.sqlSession, "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
	}

}
