package com.zdawn.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zdawn.text.lucene.search.Seacher;

public class TestSearch {
	
	private ApplicationContext ctx = null;
	
	public void initApplicationContext(){
		ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml","classpath:applicationContext-text.xml"});
	}
	public void testSearch(){
		Seacher seacher  = ctx.getBean("seacher", Seacher.class);
		String dbName = "knowledge";
		Map<String, String> para = new HashMap<String,String>();
		para.put("queryString", "fileContent:\"传染病\"");
		para.put("sort", "fileSize");
		para.put("sortDataType", "number");
		para.put("defaultField", "fileContent");
//		para.put("accessFieldName", "fileType");
//		para.put("accessValues", "G:\\UDP文档\\B-工程管理\\B5-系统测试");
		try {
			System.out.println("---------------------searchTopDocument----------------------");
			long start = System.currentTimeMillis();
			List<Map<String,Object>> topList = seacher.searchTopDocument(dbName, para);
			for (Map<String, Object> map : topList) {
				Set<Entry<String, Object>> set = map.entrySet();
				for (Entry<String, Object> entry : set) {
					System.out.println("key= "+entry.getKey() +"  value="+entry.getValue());
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("searchTopDocument total="+(end-start)+"ms  size="+topList.size());
			
			System.out.println("---------------------searchPageDocument----------------------");
			start = System.currentTimeMillis();
			Map<String, Object> map = seacher.searchPageDocument(dbName, para);
			Set<Entry<String, Object>> set = map.entrySet();
			for (Entry<String, Object> entry : set) {
				if("rows".equals(entry.getKey())){
					System.out.println("row list :");
					List<Map<String,Object>> list = (List<Map<String,Object>>)entry.getValue();
					for (Map<String, Object> mapRow : list) {
						Set<Entry<String, Object>> setRow = mapRow.entrySet();
						for (Entry<String, Object> entryRow : setRow) {
							System.out.println("key= "+entryRow.getKey() +"  value="+entryRow.getValue());
						}
					}
				}else{
					System.out.println("key= "+entry.getKey() +"  value="+entry.getValue());	
				}
			}
			end = System.currentTimeMillis();
			System.out.println("searchPageDocument total="+(end-start)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		TestSearch testSearch = new TestSearch();
		testSearch.initApplicationContext();
		testSearch.testSearch();
		System.out.println("---------------end---------------");
	}

}
