package com.zdawn.commons.sqlquery;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zdawn.commons.sqlquery.domain.QueryExecutor;

public class TestQueryExecutor{
	
	private ApplicationContext ctx = null;
	
	public void initApplicationContext(){
		ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
	}
	public void testQueryStringMapData() {
		QueryExecutor queryExecutor=ctx.getBean("queryExecutor",QueryExecutor.class);
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("orgId", "348");
		para.put("name", "娜");
		para.put("orgName","中科软");
		para.put("birthday",new Date(System.currentTimeMillis()));
		
		try {
			List<Map<String, String>> dataStringMap = queryExecutor.queryStringMapData("eap_org", para);
			System.out.println(dataStringMap.size());
			List<Map<String, Object>> dataObjectMap = queryExecutor.queryObjectMapData("eap_org", para);
			System.out.println(dataObjectMap.size());
			List<String[]> dataStringArray = queryExecutor.queryStringArrayData("eap_org", para);
			System.out.println(dataStringArray.size());
			List<Object[]> dataObjectArray = queryExecutor.queryObjectArrayData("eap_org", para);
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
