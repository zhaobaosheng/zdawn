package com.zdawn.util.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取Spring Context
 */
public class InitSpringContextServlet extends HttpServlet {
	private  Logger log = LoggerFactory.getLogger(InitSpringContextServlet.class);
	private static final long serialVersionUID = 1L;
    
    public InitSpringContextServlet() {
        super();
    }
    
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			SpringHelper.getApplicationContext(this.getServletContext());
		} catch (Exception e) {
			log.error("初始化Spring 上下文失败");
		}
	}
    
}
