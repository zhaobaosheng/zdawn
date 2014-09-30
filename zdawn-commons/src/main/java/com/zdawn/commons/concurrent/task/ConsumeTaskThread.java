package com.zdawn.commons.concurrent.task;


/**
 * 消费任务线程
 * @author zhaobs
 * 2014-02-13
 */
public class ConsumeTaskThread extends Thread {
	private DisposeUnit disposeUnit = null;
	private TaskQueue taskQueue = null;
	private boolean startBroker = true;
	
	public ConsumeTaskThread(TaskQueue taskQueue,DisposeUnit disposeUnit){
		this.taskQueue = taskQueue;
		this.disposeUnit = disposeUnit;
	}
	
	public TaskQueue getTaskQueue() {
		return taskQueue;
	}

	public void stopThread(){
		startBroker = false;
	}
	
	public void setDisposeUnit(DisposeUnit disposeUnit) {
		this.disposeUnit = disposeUnit;
	}

	@Override
	public void run() {
		while(startBroker)
		{
			Runnable task = taskQueue.pollTask();
			if(task!=null){
				try {
					disposeUnit.handleTask(task);
				} catch (Exception e) {
					System.err.println("handle task exception = "+e);
				}
			}
		}
	}
}
