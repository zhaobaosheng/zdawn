package com.zdawn.util.spring;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringHelper {
	private static ApplicationContext context = null;
	
	public static ApplicationContext getApplicationContext(ServletContext servletContext) throws Exception{
		if(context==null){
			context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			if(context==null) throw new Exception("获取spring上下文失败");
		}
		return context;
	}
	public static ApplicationContext getApplicationContext(HttpServletRequest request) throws Exception{
		return getApplicationContext(request.getSession().getServletContext());
	}

	public static void setContext(ApplicationContext context) {
		SpringHelper.context = context;
	}
	public static ApplicationContext getContext() {
		return context;
	}
	public static Object getBean(String beanName){
		return getContext().getBean(beanName);
	}
}
