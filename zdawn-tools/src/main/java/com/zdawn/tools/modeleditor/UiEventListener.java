package com.zdawn.tools.modeleditor;

import java.util.Map;
/**
 * 界面间协作接口
 * @author zhaobs
 */
public interface UiEventListener {
	/**
	 * 处理事件
	 * @param eventID 事件ID
	 * @param args 参数参数
	 */
	public void handleEvent(String eventID,Map<String,Object> args);
}
