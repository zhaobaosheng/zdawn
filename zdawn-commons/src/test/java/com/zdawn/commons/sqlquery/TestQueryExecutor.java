package com.zdawn.commons.sqlquery;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zdawn.commons.sqlquery.domain.SqlQuery;

public class TestQueryExecutor{
	
	private ApplicationContext ctx = null;
	
	public void initApplicationContext(){
		ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
	}
	public void testQueryStringMapData() {
		SqlQuery sqlQuery=ctx.getBean("sqlQuery",SqlQuery.class);
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("orgId", "348");
		para.put("name", "娜");
		para.put("orgName","中科软");
		para.put("birthday",new Date(System.currentTimeMillis()));
		
		try {
			List<Map<String, String>> dataStringMap = sqlQuery.queryStringMapData("eap_org", para);
			System.out.println(dataStringMap.size());
			List<Map<String, Object>> dataObjectMap = sqlQuery.queryObjectMapData("eap_org", para);
			System.out.println(dataObjectMap.size());
			List<String[]> dataStringArray = sqlQuery.queryStringArrayData("eap_org", para);
			System.out.println(dataStringArray.size());
			List<Object[]> dataObjectArray = sqlQuery.queryObjectArrayData("eap_org", para);
			System.out.println(dataObjectArray.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testQueryPagingStringMapData() {
	}

	public void testQueryObjectMapData() {
	}

	public void testQueryPagingObjectMapData() {
	}

	public void testQueryStringArrayData() {
	}

	public void testQueryObjectArrayData() {
	}
	public static void main(String[] arg){
		TestQueryExecutor TestQueryExecutor = new TestQueryExecutor();
		TestQueryExecutor.initApplicationContext();
		TestQueryExecutor.testQueryStringMapData();
	}
}
