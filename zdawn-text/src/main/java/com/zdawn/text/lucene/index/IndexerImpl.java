package com.zdawn.text.lucene.index;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.text.lucene.config.Config;
import com.zdawn.text.lucene.config.IndexDB;
import com.zdawn.text.lucene.config.MetaDocument;
import com.zdawn.text.lucene.config.MetaField;
import com.zdawn.text.lucene.factory.IndexWriterFactory;
import com.zdawn.text.lucene.factory.IndexWriterWrapper;

public class IndexerImpl implements Indexer {
	private static final Logger log = LoggerFactory.getLogger(IndexerImpl.class);
	
	private Config config;
	
	private IndexWriterFactory indexWriterFactory;
	
	@Override
	public void createIndex(String dbName, String docName,
			Map<String, Object> fieldData) throws Exception {
		//get index db
		IndexDB indexDB = config.getIndexDBMap().get(dbName);
		if(indexDB==null) throw new Exception("not found indexdb name="+dbName);
		//get document
		MetaDocument doc = indexDB.getDocument(docName);
		if(doc==null) throw new Exception("not found document name="+docName);
		//get file field
		List<MetaField> fileList = doc.getFileField();
		if(fileList.size()>1) throw new Exception("only support one file  field");
		IndexWriter indexWriter = indexWriterFactory.getWorkingIndexWriter(dbName);
		//not have file field 
		if(fileList.size()==0){
			addNoneFileFieldDoc(indexWriter,doc,fieldData);
		}
		MetaField file = fileList.get(0);
		//store file content
		if(file.getFieldType().stored()){
			addStoreFileContentDoc(indexWriter,doc,file,fieldData);
		}else{//not store content
			addNonStoreFileContentDoc(indexWriter,doc,file,fieldData);
		}
		indexWriter.commit();
	}
	
	private void addNonStoreFileContentDoc(IndexWriter indexWriter,
			MetaDocument metaDoc, MetaField file, Map<String, Object> fieldData)
			throws Exception {
		Reader reader = null;
		String tmpContentPath =null;
		try {
			Document document = DocBuilder.createDocumentExcludeFileField(metaDoc, fieldData);
			if(document.getFields().size()==0) throw new Exception("none field for data");
			//创建文件字段
			String filePath = (String)fieldData.get(file.getFieldName());
			if(filePath==null || filePath.equals("")) throw new Exception("field data not exist name="+file.getFieldName());
			//获得扩展名
			String ext = IndexDBUtil.getFileExtName(filePath);
			//获取手工指定文件编码
			String charsetName = (String)fieldData.get(file.getFieldName()+"_CharsetName");
			if(!config.containsExceptParseFileType(ext)){//parse document
				charsetName = config.getSaveContentCharsetName();
				FileContentParser parser = new FileContentParser();
				tmpContentPath = parser.parseContent(filePath,config.getParseCacheDir(),charsetName);
				filePath = tmpContentPath;
			}else{
				if(charsetName==null || charsetName.equals("")) charsetName = CharsetDetector.detectCharSet(filePath);
			}
			Field fileField = DocBuilder.createFileField(file,filePath,charsetName);
			document.add(fileField);
			indexWriter.addDocument(document);
			reader = fileField.readerValue();
		} catch (IOException e) {
			log.error("addNonStoreFileContentDoc", e);
			throw e;
		}finally{
			try {
				if(reader!=null) reader.close();
			} catch (Exception e) {}
			if(tmpContentPath!=null)  FileContentParser.deleteFile(tmpContentPath);
		}
	}

	private void addStoreFileContentDoc(IndexWriter indexWriter,
			MetaDocument metaDoc, MetaField file, Map<String, Object> fieldData)
			throws Exception {
		String tmpContentPath =null;
		ContentSpliter contentSpliter = null;
		try {
			List<Field> fieldList = DocBuilder.collectNoneFileField(metaDoc, fieldData);
			if(fieldList.size()==0) throw new Exception("none field for data");
			String filePath = (String)fieldData.get(file.getFieldName());
			if(filePath==null || filePath.equals("")) throw new Exception("field data not exist name="+file.getFieldName());
			//获取手工指定文件编码
			String charsetName = (String)fieldData.get(file.getFieldName()+"_CharsetName");
			//获得扩展名
			String ext = IndexDBUtil.getFileExtName(filePath);
			if(!config.containsExceptParseFileType(ext)){//parse document
				charsetName = config.getSaveContentCharsetName();
				FileContentParser parser = new FileContentParser();
				tmpContentPath = parser.parseContent(filePath,config.getParseCacheDir(),charsetName);
				filePath = tmpContentPath;
			}else{
				if(charsetName==null || charsetName.equals("")) charsetName = CharsetDetector.detectCharSet(filePath);
			}
			//读取文件内容分段建立索引
			contentSpliter = new ContentSpliter(filePath,
					charsetName, config.getFileContentBlockSize(),
					config.getSentenceSeperator(ext));
			String block = contentSpliter.getNextBlockContent();
			while(block!=null){
				Document document = DocBuilder.createDocument(fieldList);
				document.add(new Field(file.getFieldName(),block,file.getFieldType()));
				indexWriter.addDocument(document);
				//next
				block = contentSpliter.getNextBlockContent();
			}
		} catch (IOException e) {
			log.error("addStoreFileContentDoc", e);
			throw e;
		}finally{
			if(contentSpliter!=null) contentSpliter.releaseResource();
			if(tmpContentPath!=null)  FileContentParser.deleteFile(tmpContentPath);
		}
	}
	// add  have not file field document 
	private void addNoneFileFieldDoc(IndexWriter indexWriter,MetaDocument metaDoc,
						Map<String, Object> fieldData) throws Exception {
		try {
			Document document = DocBuilder.createDocumentExcludeFileField(metaDoc, fieldData);
			if(document.getFields().size()==0) throw new Exception("none field for data");
			indexWriter.addDocument(document);
		} catch (IOException e) {
			log.error("addNoneFileFieldDoc", e);
			throw e;
		}
	}

	@Override
	public void updateIndex(String dbName, String docName,
			Map<String, Object> fieldData) throws Exception {
		//get index db
		IndexDB indexDB = config.getIndexDBMap().get(dbName);
		if(indexDB==null) throw new Exception("not found indexdb name="+dbName);
		//get document
		MetaDocument doc = indexDB.getDocument(docName);
		if(doc==null) throw new Exception("not found document name="+docName);
		Object obj = fieldData.get(doc.getUniqueFieldName());
		//TermQuery
		TermQuery query = new TermQuery(new Term(doc.getUniqueFieldName(),obj.toString()));
		List<IndexWriter> indexWriterList = indexWriterFactory.getWorkingIndexWriters(dbName);
		IndexWriter target = null;
		for (IndexWriter indexWriter : indexWriterList) {
			if(hasDeleteDocument(indexWriter.getDirectory(), query)){
				indexWriter.deleteDocuments(query);
				target = indexWriter;
				break;
			}
		}
		if(target==null){
			indexWriterList = indexWriterFactory.getRetiredIndexWriter(dbName);
			for (IndexWriter indexWriter : indexWriterList) {
				if(hasDeleteDocument(indexWriter.getDirectory(), query)){
					indexWriter.deleteDocuments(query);
					target = indexWriter;
					break;
				}
			}
		}
		if(target==null) target = indexWriterFactory.getWorkingIndexWriter(dbName);
		List<MetaField> fileList = doc.getFileField();
		if(fileList.size()>1) throw new Exception("only support one file  field");
		//not have file field 
		if(fileList.size()==0){
			addNoneFileFieldDoc(target,doc,fieldData);
		}
		MetaField file = fileList.get(0);
		//store file content
		if(file.getFieldType().stored()){
			addStoreFileContentDoc(target,doc,file,fieldData);
		}else{//not store content
			addNonStoreFileContentDoc(target,doc,file,fieldData);
		}
		target.commit();
	}

	@Override
	public void deleteIndex(String dbName, String docName, String id)
			throws Exception {
		try {
			//get index db
			IndexDB indexDB = config.getIndexDBMap().get(dbName);
			if(indexDB==null) throw new Exception("not found indexdb name="+dbName);
			//get document
			MetaDocument doc = indexDB.getDocument(docName);
			if(doc==null) throw new Exception("not found document name="+docName);
			//TermQuery
			TermQuery query = new TermQuery(new Term(doc.getUniqueFieldName(),id));
			List<IndexWriter> indexWriterList = indexWriterFactory.getWorkingIndexWriters(dbName);
			indexWriterList.addAll(indexWriterFactory.getRetiredIndexWriter(dbName));
			for (IndexWriter indexWriter : indexWriterList) {
				if(hasDeleteDocument(indexWriter.getDirectory(), query)){
					indexWriter.deleteDocuments(query);
					indexWriter.commit();
					break;
				}
			}
		} catch (IOException e) {
			log.error("deleteIndex", e);
			throw e;
		}
	}
	private boolean hasDeleteDocument(Directory directory,TermQuery query) throws IOException {
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		TopDocs topDocs = indexSearcher.search(query,1);
		if(topDocs.totalHits>0) return true;
		return false;
	}

	@Override
	public void commitChange(String dbName) throws Exception {
		List<IndexWriter> list = indexWriterFactory.getWorkingIndexWriters(dbName);
		for (IndexWriter indexWriter : list) {
			IndexWriterWrapper wrapper = (IndexWriterWrapper)indexWriter;
			if(wrapper.isDirty()){
				 wrapper.commit();
				 wrapper.setDirty(false);
			}
		}
		list = indexWriterFactory.getRetiredIndexWriter(dbName);
		for (IndexWriter indexWriter : list) {
			IndexWriterWrapper wrapper = (IndexWriterWrapper)indexWriter;
			if(wrapper.isDirty()){
				 wrapper.commit();
				 wrapper.setDirty(false);
			}
		}
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setIndexWriterFactory(IndexWriterFactory indexWriterFactory) {
		this.indexWriterFactory = indexWriterFactory;
	}
}
