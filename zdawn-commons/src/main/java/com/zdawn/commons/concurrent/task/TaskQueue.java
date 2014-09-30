package com.zdawn.commons.concurrent.task;

import java.util.LinkedList;

/**
 * @author zhaobs
 * 2014-02-13
 */
public class TaskQueue {
	/**
	 * 队列最大深度
	 */
	private int maxSize = 1000;
	/**
	 * task 队列
	 */
	private LinkedList<Runnable> queueTask = new LinkedList<Runnable>();
	/**
	 * 从队列获取任务
	 */
	public synchronized Runnable pollTask(){
		try {
			while(queueTask.size()==0){
				wait();
			}
		} catch (InterruptedException e) {
			System.out.println("getRecTask error"+e.toString());
		}
		return queueTask.removeFirst();
	}
	/**
	 * 添加任务
	 */
	public synchronized boolean putTask(Runnable task){
		if(queueTask.size()>=maxSize) return false;
		queueTask.add(task);
		notifyAll();
		return true;
	}
	/**
	 * 能否添加任务
	 */
	public boolean canAddTask(){
		return queueTask.size() < maxSize;
	}
	/**
	 * 获取当前任务队列深度
	 */
	public int getCurrentQueueSize(){
		return queueTask.size();
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
