package com.zdawn.util.spring;

import java.util.Locale;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class JacksonViewResolver implements ViewResolver, Ordered {
	private String jacksonView = "jackson";
	private int order = Integer.MAX_VALUE;
	private JacksonView view = null;
	@Override
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		viewName = viewName==null ? "":viewName;
		if(viewName.length()==0) return null;
		if(!viewName.equals(jacksonView)) return null;
		return view;
	}

	public void setJacksonView(String jacksonView) {
		this.jacksonView = jacksonView;
	}

	public void setView(JacksonView view) {
		this.view = view;
	}
}
