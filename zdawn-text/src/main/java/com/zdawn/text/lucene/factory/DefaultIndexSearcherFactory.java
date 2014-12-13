package com.zdawn.text.lucene.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.text.lucene.config.Config;
import com.zdawn.text.lucene.config.IndexDB;

public class DefaultIndexSearcherFactory implements IndexSearcherFactory {
	private static final Logger log = LoggerFactory.getLogger(DefaultIndexSearcherFactory.class);
	/**
	 * 检查索引变化时间间隔 单位分钟
	 */
	private int checkIndexChangeSpanTime = 30;
	
	private long beginTime = 0;
	
	private Map<String,List<IndexSearcherInfo>> dbMap = new HashMap<String, List<IndexSearcherInfo>>();
	
	private Map<String,List<IndexSearcher>> indexSearcherCacheMap = new HashMap<String, List<IndexSearcher>>();
	
	private Config config;
	
	@Override
	public List<IndexSearcher> getIndexSearchers(String dbName) {
		if(dbName==null || dbName.equals("")) throw new RuntimeException("index db name is empty");
		checkLoadIndex(dbName);
		List<IndexSearcher> list = indexSearcherCacheMap.get(dbName);
		if(list==null){
			List<IndexSearcherInfo> indexSearcherInfoList = dbMap.get(dbName);
			list = new ArrayList<IndexSearcher>();
			for (IndexSearcherInfo indexSearcherInfo : indexSearcherInfoList) {
				list.add(indexSearcherInfo.getIndexSearcher());
			}
			indexSearcherCacheMap.put(dbName, list);
		}
		return list;
	}
	private void checkLoadIndex(String dbName){
		long current = System.currentTimeMillis();
		if(current-beginTime > checkIndexChangeSpanTime*60*1000){
			loadIndexChange(dbName);
			beginTime = current;
		}
	}

	private IndexSearcherInfo getIndexSearcherInfoByPath(String path,
			List<IndexSearcherInfo> listInfo) {
		for (IndexSearcherInfo indexSearcherInfo : listInfo) {
			if(indexSearcherInfo.getPath().equals(path)) return indexSearcherInfo;
		}
		return null;
	}
	private void loadIndexChange(String dbName) {
		//check new index file
		IndexDB indexDB = config.getIndexDBMap().get(dbName);
		File dbFile = new File(indexDB.getIndexDBPath());
		if(dbFile.isFile()) throw new RuntimeException("index db path is file");
		List<IndexSearcherInfo> listInfo = dbMap.get(dbName);
		if(listInfo==null){
			listInfo = new ArrayList<IndexSearcherInfo>();
			dbMap.put(dbName, listInfo);
		}
		File[] list = dbFile.listFiles();
		boolean nochange = true;
		for (File file : list) {
			if(file.isFile()) continue;
			String path = file.getPath();
			IndexSearcherInfo info = getIndexSearcherInfoByPath(path,listInfo);
			if(info==null){//create
				try {
					info = new IndexSearcherInfo();
					Directory directory = FSDirectory.open(new File(path));
					IndexReader indexReader = DirectoryReader.open(directory);
					IndexSearcher indexSearcher = new IndexSearcher(indexReader);
					info.setPath(path);
					info.setDirectory(directory);
					info.setIndexSearcher(indexSearcher);
					listInfo.add(info);
					if(nochange) nochange = false;
				} catch (IOException e) {
					log.error("create indexSearcher",e);
				}
			}else{
				//load index changed
				try {
					DirectoryReader oldReader = (DirectoryReader)info.getIndexSearcher().getIndexReader();
					DirectoryReader newReader = DirectoryReader.openIfChanged(oldReader);
					if(newReader!=null){//index changed,must create new IndexSearcher
						IndexSearcher indexSearcher = new IndexSearcher(newReader);
						info.setIndexSearcher(indexSearcher);
						//close old Reader
						oldReader.close();
						if(nochange) nochange = false;
					}
				} catch (IOException e) {
					log.error("load index changed",e);
				}
			}
		}
		if(!nochange) indexSearcherCacheMap.remove(dbName);
	}
	
	public void setCheckIndexChangeSpanTime(int checkIndexChangeSpanTime) {
		this.checkIndexChangeSpanTime = checkIndexChangeSpanTime;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
}
