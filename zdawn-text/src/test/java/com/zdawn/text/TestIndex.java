package com.zdawn.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zdawn.text.lucene.index.Indexer;

public class TestIndex {
	private ApplicationContext ctx = null;
	private Indexer indexer = null;
	private String path = "G:/UDP文档/B-工程管理";
	
	public void initApplicationContext(){
		ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml","classpath:applicationContext-text.xml"});
	}
	private void createOneIndex(File file){
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String fileId = UUID.randomUUID().toString();
			String fileName = file.getName();
			String fileType = file.getParent();
			String fileModifyDate = df.format(new Timestamp(file.lastModified()));
			long fileSize = file.length();
			String fileContent = file.getPath();
			Map<String, Object> fieldData = new HashMap<String, Object>();
			fieldData.put("fileId", fileId);
			fieldData.put("fileName", fileName);
			fieldData.put("fileType", fileType);
			fieldData.put("fileModifyDate", fileModifyDate);
			fieldData.put("fileSize", fileSize);
			fieldData.put("fileContent", fileContent);
			indexer.createIndex("knowledge","attachmentDoc", fieldData );
		} catch (Exception e) {
			saveErrorDoc(file);
		}
	}
	public void createIndex(){
		try {
			indexer = ctx.getBean("indexer", Indexer.class);
			ArrayList<File> subFoder = new ArrayList<File>();
			subFoder.add(new File(path));
			while(true){
				ArrayList<File> temp = new ArrayList<File>();
				for (File file : subFoder) {
					if(file.isDirectory()){//如果是文件夹,遍历子文件
						File[] subFile = file.listFiles();
						for (int i = 0; i < subFile.length; i++) {
							if(subFile[i].isDirectory()) temp.add(subFile[i]);
							else{
								long begin = System.currentTimeMillis();
								createOneIndex(subFile[i]);
								long end = System.currentTimeMillis();
								System.out.println(subFile[i].getPath()+" create index time-consuming total "+(end-begin)+"ms");
							}
						}
					}else{
						long begin = System.currentTimeMillis();
						createOneIndex(file);
						long end = System.currentTimeMillis();
						System.out.println(file.getPath()+" create index time-consuming total "+(end-begin)+"ms");
					}
				}
				//如果没有子目录退回循环，有赋值subFoder变量
				if(temp.size()==0) break;
				subFoder = null;
				subFoder = temp;
			}
			indexer.commitChange("knowledge");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void saveErrorDoc(File file){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("C:/Users/zhaobs/Desktop/temp/error.txt");
			fos.write(file.getPath().getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {fos.close();} catch (IOException e) {}
		}
	}
	public static void main(String[] args) {
		TestIndex index = new TestIndex();
		index.initApplicationContext();
		index.createIndex();
	}
}
