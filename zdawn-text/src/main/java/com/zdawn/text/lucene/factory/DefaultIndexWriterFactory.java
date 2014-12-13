package com.zdawn.text.lucene.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.text.lucene.config.Config;
import com.zdawn.text.lucene.config.IndexDB;
import com.zdawn.text.lucene.index.DocumentTrigger;
import com.zdawn.text.lucene.index.IndexDBUtil;

public class DefaultIndexWriterFactory implements IndexWriterFactory,DocumentTrigger {
	private static final Logger log = LoggerFactory.getLogger(DefaultIndexWriterFactory.class);
	
	private Config config;
	
	private Map<String,List<IndexWriterInfo>> workingIndexWriterMap = new HashMap<String, List<IndexWriterInfo>>();
	
	private Map<String,List<IndexWriterInfo>> retiredIndexWriterMap = new HashMap<String, List<IndexWriterInfo>>();
	//index-document count check point to calculate index size
	private int createIndexCheckPoint = -1;
	
	private long currentCreateIndexCount = 0;
	
	@Override
	public IndexWriter getWorkingIndexWriter(String dbName) {
		return getWorkingIndexWriters(dbName).get(0);
	}

	private IndexWriterInfo createNewIndexWriterInfo(IndexDB indexDB){
		IndexWriterInfo info =  new IndexWriterInfo();
		try {
			String subPath = IndexDBUtil.createNewFolder(indexDB.getIndexDBPath());
			File first = new File(indexDB.getIndexDBPath()+File.separator +subPath);
			first.mkdir();
			Directory directory = FSDirectory.open(first);
			if(IndexWriter.isLocked(directory)) IndexWriter.unlock(directory);
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_4_9 ,config.getAnalyzer());
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriterWrapper indexWriter = new IndexWriterWrapper(directory , iwConfig);
			indexWriter.setDbName(indexDB.getDbName());
			indexWriter.setTrigger(this);
			info.setIndexWriter(indexWriter);
			info.setPath(first.getPath());
			info.setIndexFileSize(0);
		} catch (IOException e) {
			log.error("createNewIndexWriterInfo",e);
			throw new RuntimeException("create IndexWriter error");
		}
		return info;
	}
	private void createIndexWriterForDB(IndexDB indexDB) {
		String path = indexDB.getIndexDBPath(); 
		File dbFile = new File(path);
		if(dbFile.isFile()) throw new RuntimeException("index db path is file");
		File[] list = dbFile.listFiles();
		try {
			List<IndexWriterInfo> workingList = new ArrayList<IndexWriterInfo>();
			List<IndexWriterInfo> retiredList = new ArrayList<IndexWriterInfo>();
			if(list.length==0){//create new index
				workingList.add(createNewIndexWriterInfo(indexDB));
			}else{
				for (File file : list) {
					if(file.isFile()) continue;
					long size = IndexDBUtil.sizeOfDirectory(file);
					Directory directory = FSDirectory.open(file);	 
					//配置IndexWriterConfig
					IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_4_9 ,config.getAnalyzer());
					iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
					IndexWriterWrapper indexWriter = new IndexWriterWrapper(directory , iwConfig);
					indexWriter.setDbName(indexDB.getDbName());
					indexWriter.setTrigger(this);
					IndexWriterInfo info = new IndexWriterInfo();
					info.setIndexWriter(indexWriter);
					info.setPath(file.getPath());
					info.setIndexFileSize(size);
					if(indexDB.getDbSize()==-1 || size/1024/1024 < indexDB.getDbSize()){//working
						workingList.add(info);
					}else{//retired
						retiredList.add(info);
					}
				}
			}
			workingIndexWriterMap.put(indexDB.getDbName(), workingList);
			retiredIndexWriterMap.put(indexDB.getDbName(), retiredList);
		} catch (IOException e) {
			log.error("createIndexWriterForDB",e);
			throw new RuntimeException("create IndexWriter error");
		}
	}

	@Override
	public List<IndexWriter> getRetiredIndexWriter(String dbName) {
		if(dbName==null || dbName.equals("")) throw new RuntimeException("index db name is empty");
		List<IndexWriterInfo> list = retiredIndexWriterMap.get(dbName);
		if(list==null){
			IndexDB indexDB = config.getIndexDBMap().get(dbName);
			//create IndexWriter for dbName
			createIndexWriterForDB(indexDB);
			list =  retiredIndexWriterMap.get(dbName);
		}
		List<IndexWriter> writerList = new ArrayList<IndexWriter>();
		for (IndexWriterInfo indexWriterInfo : list) {
			writerList.add(indexWriterInfo.getIndexWriter());
		}
		return writerList;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setCreateIndexCheckPoint(int createIndexCheckPoint) {
		this.createIndexCheckPoint = createIndexCheckPoint;
	}

	@Override
	public List<IndexWriter> getWorkingIndexWriters(String dbName) {
		if(dbName==null || dbName.equals("")) throw new RuntimeException("index db name is empty");
		List<IndexWriterInfo> list = workingIndexWriterMap.get(dbName);
		if(list==null){
			IndexDB indexDB = config.getIndexDBMap().get(dbName);
			//create IndexWriter for dbName
			createIndexWriterForDB(indexDB);
			list =  workingIndexWriterMap.get(dbName);
		}
		List<IndexWriter> writerList = new ArrayList<IndexWriter>();
		for (IndexWriterInfo indexWriterInfo : list) {
			writerList.add(indexWriterInfo.getIndexWriter());
		}
		return writerList;
	}

	@Override
	public void createDocument(String dbName,IndexWriter indexWriter,
			Iterable<? extends IndexableField> doc) {
		currentCreateIndexCount= currentCreateIndexCount +1;
		if(createIndexCheckPoint ==-1 || 
				currentCreateIndexCount< createIndexCheckPoint) return ;
		synchronized (workingIndexWriterMap) {
			List<IndexWriterInfo> list = workingIndexWriterMap.get(dbName);
			//find writer
			IndexWriterInfo move = null;
			for (IndexWriterInfo indexWriterInfo : list) {
				if(indexWriterInfo.getIndexWriter()==indexWriter){
					move = indexWriterInfo;
					break;
				}
			}
			//check size
			IndexDB indexDB = config.getIndexDBMap().get(dbName);
			long size = IndexDBUtil.sizeOfDirectory(move.getPath());
			if(indexDB.getDbSize()==-1 || size/1024/1024 < indexDB.getDbSize()) return ;
			currentCreateIndexCount = 0;
			//move from working to retired
			list.remove(move);
			list = retiredIndexWriterMap.get(dbName);
			if(list==null){
				list = new ArrayList<IndexWriterInfo>();
				retiredIndexWriterMap.put(dbName, list);
			}
			list.add(move);
			workingIndexWriterMap.get(dbName).add(createNewIndexWriterInfo(indexDB));
		}
	}

	@Override
	public void updateDocument(String dbName, IndexWriter indexWriter,
			Term term, Iterable<? extends IndexableField> doc) {
	}

	@Override
	public void deleteDocument(String dbName, IndexWriter indexWriter,
			Query... queries) {
		currentCreateIndexCount= currentCreateIndexCount -1;
	}
}
