package com.zdawn.tools.modeleditor;

import java.awt.Container;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class CommonDialog extends JDialog implements UiEventListener {
	
	public static final String CLOSE_DIALOG = "CLOSE";
	
	private static final long serialVersionUID = 6105108627954414632L;
	
	public CommonDialog(Container container,String title){
		super(UIUtil.getTopWindow(container),title, true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	public void addContentPanel(JPanel panel){
		getContentPane().add(panel);
	}
	@Override
	public void handleEvent(String eventID, Map<String, Object> args) {
		if(eventID.equals(CLOSE_DIALOG)){
			this.dispose();
		}
	}
}
