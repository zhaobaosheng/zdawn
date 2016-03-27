package com.zdawn.text.lucene.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchContextImpl implements SearchContext {
	//store search data
	private List<IndexSearcher> indexSearcherList= null;
	private TopDocs[]  topDocArray = null; 
	private CountDownLatch countLatch = null;
	private int totalHits;
	/**
	 * @param count 并行检索线程数
	 */
	public SearchContextImpl(List<IndexSearcher> indexSearcherList){
		this.indexSearcherList = indexSearcherList;
		countLatch = new CountDownLatch(indexSearcherList.size());
		topDocArray = new TopDocs[indexSearcherList.size()];
	}
	/**
	 * 标记完成一个线程检索
	 */
	public void finishSearch(){
		countLatch.countDown();
	}
	/**
	 * 当前线程等待并行检索线程全部完成
	 * @param millisTimeout 等待时间，如果超过这个时间会返回，如果线程全部完成可以提前返回。
	 * @throws InterruptedException
	 */
	public void  await(long millisTimeout)  throws InterruptedException{
		countLatch.await(millisTimeout,TimeUnit.MILLISECONDS);
	}
	/**
	 * 设置单个查询线程查询数据
	 * @param key 检索数据标识
	 * @param data 数据
	 */
	public void setThreadData(Object key,Object data){
		if(key instanceof IndexSearcher && data!=null){
			if(data instanceof TopDocs){
				for (int i = 0; i < indexSearcherList.size(); i++) {
					IndexSearcher indexSearcher = indexSearcherList.get(i);
					if(indexSearcher==key){
						topDocArray[i] = (TopDocs)data;
					}
				}
			}else{
				System.out.println("data not TopDocs class "+data.getClass().getName());
			}
		}else{
			System.out.println("index to large index="+key+" or data is null");
		}
	}
	//分页合并搜索结果
	public List<DocumentHolder> mergePageTopDocs(int currentPage,
			int pageSize, int topNum) {		
		List<DocumentHolder> topDocumentHolder = mergeTopDocs(pageSize);
		int start = (currentPage-1)*pageSize;
		int end = currentPage*pageSize > topDocumentHolder.size() ? topDocumentHolder.size()-1:currentPage*pageSize-1;
		int length = end-start +1;
		List<DocumentHolder> pageData = new ArrayList<DocumentHolder>();
		System.arraycopy(topDocumentHolder, start, pageData, 0, length);
		return pageData;
	}
	//合并搜索结果
	public List<DocumentHolder> mergeTopDocs(int maxNum){
		LinkedList<DocumentHolder> list = new LinkedList<DocumentHolder>();
		boolean initList = false;
		for (int i = 0; i < topDocArray.length; i++) {
			if(topDocArray[i]==null) continue;
			totalHits = totalHits +topDocArray[i].totalHits;
			ScoreDoc[] temp = topDocArray[i].scoreDocs;
			if(temp ==null || temp.length==0) continue;
			if(temp[0].score==Float.NEGATIVE_INFINITY) continue;
			if(!initList){//任意一个TopDocs初始化
				for (ScoreDoc scoreDoc : temp) {
					if(scoreDoc==null) continue;
					if(scoreDoc.score==Float.NEGATIVE_INFINITY) break;
					list.add(new DocumentHolder(i, scoreDoc.score, scoreDoc.doc));
				}
				initList = true;
			}else{//合并
				for (ScoreDoc scoreDoc : temp) {
					if(scoreDoc==null) continue; 
					if(scoreDoc.score==Float.NEGATIVE_INFINITY) break;
					if(maxNum==list.size()){
						//取最后一个元素,与当前比较,大于当前元素退出循环
						DocumentHolder holder = list.peekLast();
						if(holder.getScore()>=scoreDoc.score) break;
					}
					insertDocumentHolder(list,scoreDoc,i,maxNum);
				}
			}
		}
		return list;
	}
	/**
	 * 总记录数
	 */
	public int getTotalHits() {
		return totalHits;
	}
	//插入元素
	private void insertDocumentHolder(LinkedList<DocumentHolder> list,
			ScoreDoc scoreDoc,int index, int topNum) {
		if(topNum>list.size()){
			list.add(new DocumentHolder(index, scoreDoc.score, scoreDoc.doc));
			return;
		}
		for (int j = 0; j < list.size(); j++) {
			DocumentHolder holder = list.get(j);
			if(scoreDoc.score>holder.getScore()){
				list.add(j, new DocumentHolder(index, scoreDoc.score, scoreDoc.doc));
				if(list.size()>topNum) list.pollLast();
				return;
			}
		}
	}
}
