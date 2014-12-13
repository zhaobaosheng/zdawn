package com.zdawn.text.lucene.config;

import java.util.ArrayList;
import java.util.List;

public class IndexDB {
	/**
	 * 索引库英文名字 唯一
	 */
	private String dbName;
	/**
	 * 索引库描述
	 */
	private String description;
	/**
	 * 索引库路径
	 */
	private String indexDBPath;
	/**
	 * 索引库大小 单位M 大小超过创建新的索引库
	 */
	private int dbSize = 500;
	/**
	 * 索引库文档集合
	 */
	private List<MetaDocument> docList = new ArrayList<MetaDocument>();
	/**
	 * 查找文档
	 * @param docName
	 * @return 如果不存在返回 null
	 */
	public MetaDocument getDocument(String docName){
		for (MetaDocument doc : docList) {
			if(doc.getDocName().equals(docName)) return doc;
		}
		return null;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIndexDBPath() {
		return indexDBPath;
	}
	public void setIndexDBPath(String indexDBPath) {
		this.indexDBPath = indexDBPath;
	}
	public int getDbSize() {
		return dbSize;
	}
	public void setDbSize(int dbSize) {
		this.dbSize = dbSize;
	}
	public List<MetaDocument> getDocList() {
		return docList;
	}
	public void setDocList(List<MetaDocument> docList) {
		this.docList = docList;
	}
}
