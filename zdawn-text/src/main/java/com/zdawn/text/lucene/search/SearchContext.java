package com.zdawn.text.lucene.search;


public interface SearchContext {
	/**
	 * 标记完成一个线程检索
	 */
	public void finishSearch();
	/**
	 * 当前线程等待并行检索线程全部完成
	 * @param millisTimeout 等待时间，如果超过这个时间会返回，如果线程全部完成可以提前返回。
	 * @throws InterruptedException
	 */
	public void  await(long millisTimeout)  throws InterruptedException;
	/**
	 * 设置单个查询线程查询数据
	 * @param key 检索数据标识
	 * @param data 数据
	 */
	public void setThreadData(Object key,Object data);
}
