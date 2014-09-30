package com.zdawn.commons.concurrent.task;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * support add task
 * support use thread pool process task
 * @author zhaobs
 * @date 2014-02-13
 */
public class DisposeUnit{
	/**
	 * 任务队列
	 */
	private TaskQueue taskQueue = null;
	/**
	 * if threads idle keep threads size
	 */
	private int corePoolSize = 1;
	/**
	 * 处理任务线程最大数
	 */
	private int maxPoolSize = 1 ;
	/**
	 * 任务处理队列深度
	 */
	private int maxQueueSize = 1000 ;
	/**
	 * 线程池
	 */
	private ThreadPoolExecutor threadPool = null;
	/**
	 * 等待再次执行任务周期，单位秒
	 */
	private int waitExecutePeriod = 3;
	
	private ConsumeTaskThread consumeThread = null;
	
	private ThreadPoolExecutor createThreadPool(){
		if(threadPool!=null) return threadPool;
		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>());
		return threadPool;
	}
	/**
	 * 添加处理任务，添加不成功返回false
	 */
	public boolean addTask(Runnable task){
		boolean success = false;
		if(taskQueue.canAddTask()) success = taskQueue.putTask(task);
		return success;
	}
	/**
	 * 当前处理单元队列深度
	 */
	public int getCurrentTaskQueueSize(){
		return taskQueue.getCurrentQueueSize();
	}
	/**
	 * 可利用队列深度,数值越大可放任务越多
	 */
	public int availableTaskQueueSize(){
		return taskQueue.getMaxSize()-taskQueue.getCurrentQueueSize();
	}
	
	public float getQueueUsingPercent(){
		return taskQueue.getCurrentQueueSize()/taskQueue.getMaxSize();
	}
	/**
	 * 使用线程池执行任务,如果线程池没有空闲线程等待waitExecutePeriod时间间隔，再次执行。
	 */
	public void handleTask(Runnable task) {
		//如果不能放到线程池执行,阻塞当前线程，直到能执行为止
		while(true){
			if(executeTask(task,threadPool)) break;
			//等待一个周期
			try {
				Thread.sleep(waitExecutePeriod*1000);
			} catch (InterruptedException e) {}
		}
	}
	
	public void init(){
		if(taskQueue==null){
			taskQueue = new TaskQueue();
			taskQueue.setMaxSize(maxQueueSize);
		}
		if(threadPool==null) createThreadPool();
		if(consumeThread==null){
			consumeThread = new ConsumeTaskThread(taskQueue,this);
			consumeThread.start();
		}
	}
	
	private boolean executeTask(Runnable eworker,ThreadPoolExecutor threadPool){
		boolean success = true;
		try {
			threadPool.execute(eworker);
		} catch (RejectedExecutionException e) {
			success = false;
		}
		return success;
	}
	
	public TaskQueue getTaskQueue() {
		return taskQueue;
	}
	
	public void setTaskQueue(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}
	
	public int getCorePoolSize() {
		return corePoolSize;
	}
	
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}
	
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public int getWaitExecutePeriod() {
		return waitExecutePeriod;
	}
	
	public void setWaitExecutePeriod(int waitExecutePeriod) {
		this.waitExecutePeriod = waitExecutePeriod;
	}
	
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
		if(taskQueue!=null){
			if(taskQueue.getMaxSize()!=maxQueueSize) 
				taskQueue.setMaxSize(maxQueueSize);
		}
	}
	
}
