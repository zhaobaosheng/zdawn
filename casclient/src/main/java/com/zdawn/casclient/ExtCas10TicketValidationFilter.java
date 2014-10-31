package com.zdawn.casclient;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.TicketValidator;

public class ExtCas10TicketValidationFilter extends
		ExtAbstractTicketValidationFilter {
	/**
     * except urls
     */
    private List<String> exceptURLs = null;
    /**
     * 业务应用初始化权限类名
     * <br>类必须有init方法
     * <br>第一个参数 HttpServletRequest.class
     * <br>第二个参数 String.class
     * <br>example public void init(HttpServletRequest request,String userName)
     */
    private String appAuthClassName = null;
    
    private Object appAuthObject = null;
    
    private Method initMethod = null;
    
    /**
     * 替换ip地址来源
     * <br>0 从HttpServletRequest.getServerName方法获取
     * <br>配置其他内容从HttpServletRequest.getHeader方法获取
     * <br>并且配置内容作为getHeader方法获取的参数 
     */
    private String ipFrom = "0";
    
    /**
     * 替换类型
     * <br>使用用户请求ip替换配置的url中的ip地址
     * 0 不替换使用配置地址
     * 1 替换 service和serverName参数
     */
    private int replaceType = 0;
    
	@Override
	protected void initInternal(FilterConfig filterConfig)
			throws ServletException {
		 String exceptURI = getPropertyFromInitParams(filterConfig, "exceptURLs", null);
         log.trace("Loaded exceptURLs parameter: " + exceptURI);
         if(exceptURI!=null){
         	exceptURLs = new ArrayList<String>();
         	String[] uri = exceptURI.split(",");
         	for (int i = 0; i < uri.length; i++) exceptURLs.add(uri[i]);
         }
         appAuthClassName = getPropertyFromInitParams(filterConfig, "appAuthClassName", null);
         
         String repType = getPropertyFromInitParams(filterConfig, "replaceType", "0");
         this.replaceType = Integer.parseInt(repType);
         log.trace("replaceType parameter: " + this.replaceType);
         if(replaceType>0){
         	ipFrom = getPropertyFromInitParams(filterConfig, "ipFrom", "0");
         	log.trace("ipFrom parameter: " + this.ipFrom);
         }
		super.initInternal(filterConfig);
	}

	protected final TicketValidator getTicketValidator(final FilterConfig filterConfig) {
        final String casServerUrlPrefix = getPropertyFromInitParams(filterConfig, "casServerUrlPrefix", null);
        final Cas10TicketValidator validator = new Cas10TicketValidator(casServerUrlPrefix);
        validator.setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
        validator.setHostnameVerifier(getHostnameVerifier(filterConfig));
        validator.setEncoding(getPropertyFromInitParams(filterConfig, "encoding", null));

        return validator;
    }

	@Override
	protected boolean preFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String requestUri = request.getRequestURI();
        if(exceptURLs!=null && URLUtils.exceptURL(requestUri, exceptURLs)){
        	filterChain.doFilter(request, response);
            return false;
        }
        
		return true;
	}

	@Override
	protected void onSuccessfulValidation(HttpServletRequest request,
			HttpServletResponse response, Assertion assertion) {
		if(appAuthClassName==null) return ;
		if(initMethod == null){
			 try {
				 Class<?> appAuthClass = getClass().getClassLoader().loadClass(appAuthClassName);
				 appAuthObject = appAuthClass.newInstance();
				 initMethod = appAuthClass.getMethod("init",HttpServletRequest.class,String.class);
			} catch (Exception e) {
				log.warn("load class error ClassName="+appAuthClassName,e);
			}
		}
		try {
			initMethod.invoke(appAuthObject,request,assertion.getPrincipal().getName());
		} catch (Exception e) {
			log.warn("execute init error",e);
		}
	}

	@Override
	protected void onFailedValidation(HttpServletRequest request,
			HttpServletResponse response) {
		log.warn("Validate error to see log4j log!");
	}

	@Override
	protected String constructServiceUrl(HttpServletRequest request,
			HttpServletResponse response) {
		String tmpService = this.service;
		String tmpServerName = this.serverName;
		 if(replaceType>0){
			 String ipAddress = ipFrom.equals("0") ? request.getServerName():request.getHeader(ipFrom);
	         if(ipAddress==null) ipAddress = request.getServerName();
			 int index = ipAddress.indexOf(':');
			 if(index!=-1) ipAddress = ipAddress.substring(0,index);
			 if (CommonUtils.isNotBlank(service)) {
				 tmpService = URLUtils.replaceIP(tmpService, ipAddress);
			 }else{
				 tmpServerName = URLUtils.replaceIP(tmpServerName, ipAddress);
			 }
		 }
		return CommonUtils.constructServiceUrl(request, response, tmpService, tmpServerName, this.artifactParameterName, this.encodeServiceUrl);
	}
	
}
