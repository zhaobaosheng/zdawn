package com.zdawn.tools.modeleditor;

import java.util.Map;

public interface UiLifeCycle {
	
	public void initUI(Map<String,String> para);
	
	public void destroyUI();
	
}
