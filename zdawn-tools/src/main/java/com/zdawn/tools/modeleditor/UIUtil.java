package com.zdawn.tools.modeleditor;

import java.awt.Container;
import java.awt.Desktop;
import java.net.URI;

import javax.swing.JFrame;

public class UIUtil {
	
	public static JFrame getTopWindow(Container container){
		if(container==null) return null;
		JFrame top = null;
		Container temp = container.getParent();
		while(temp!=null){
			if(temp instanceof JFrame){
				top = (JFrame)temp;
				break;
			}
			temp = temp.getParent();
		}
		return top;
	}
	public static void openURL(String url) throws Exception{
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(url));
		} catch (Exception e) {
			throw e;
		}
	}
}
