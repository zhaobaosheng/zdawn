package com.zdawn.casclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

public class ExtAuthenticationFilter extends AbstractCasFilter {
    /**
     * The URL to the CAS Server login.
     */
    private String casServerLoginUrl;
    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;
    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;
    
    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();
    /**
     * except urls
     */
    private List<String> exceptURLs = null;
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
     * 2 替换 service、serverName、casServerLoginUrl参数
     */
    private int replaceType = 0;

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
            log.trace("Loaded CasServerLoginUrl parameter: " + this.casServerLoginUrl);
            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
            log.trace("Loaded renew parameter: " + this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
            log.trace("Loaded gateway parameter: " + this.gateway);

            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

            if (gatewayStorageClass != null) {
                try {
                    this.gatewayStorage = (GatewayResolver) Class.forName(gatewayStorageClass).newInstance();
                } catch (final Exception e) {
                    log.error(e,e);
                    throw new ServletException(e);
                }
            }
            String exceptURI = getPropertyFromInitParams(filterConfig, "exceptURLs", null);
            log.trace("Loaded exceptURLs parameter: " + exceptURI);
            if(exceptURI!=null){
            	exceptURLs = new ArrayList<String>();
            	String[] uri = exceptURI.split(",");
            	for (int i = 0; i < uri.length; i++) exceptURLs.add(uri[i]);
            }
            String repType = getPropertyFromInitParams(filterConfig, "replaceType", "0");
            this.replaceType = Integer.parseInt(repType);
            log.trace("replaceType parameter: " + this.replaceType);
            if(replaceType>0){
            	ipFrom = getPropertyFromInitParams(filterConfig, "ipFrom", "0");
            	log.trace("ipFrom parameter: " + this.ipFrom);
            }
        }
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        if(exceptURLs!=null && URLUtils.exceptURL(request.getRequestURI(), exceptURLs)){
        	filterChain.doFilter(request, response);
            return;
        }
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        //判断是否是ajax请求
 		String requestType = request.getHeader("X-Requested-With");
 		if ("XMLHttpRequest".equalsIgnoreCase(requestType)) {
 			response.setHeader("sessionstatus", "timeout");
        	response.setContentType("text/html;charset=UTF-8");
        	String tip = "{\"result\":\"false\",\"desc\":\"session time out\"}";
        	OutputStream out  = response.getOutputStream();
        	out.write(tip.getBytes("UTF-8"));
        	out.flush();
        	out.close();
        	return ;
 		}
        
        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = CommonUtils.safeGetParameter(request,getArtifactParameterName());
        final boolean wasGatewayed = this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            filterChain.doFilter(request, response);
            return;
        }

        final String modifiedServiceUrl;

        log.debug("no ticket and no assertion found");
        if (this.gateway) {
            log.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        if (log.isDebugEnabled()) {
            log.debug("Constructed service url: " + modifiedServiceUrl);
        }
        
        String urlToRedirectTo = "";
        if(replaceType>0){
        	String ipAddress = ipFrom.equals("0") ? request.getServerName():request.getHeader(ipFrom);
        	if(ipAddress==null) ipAddress = request.getServerName();
			int index = ipAddress.indexOf(':');
			if(index!=-1) ipAddress = ipAddress.substring(0,index);
			String tmpLoginUrl = this.casServerLoginUrl;
			String tmpServiceUrl = URLUtils.replaceIP(modifiedServiceUrl, ipAddress);
			if(replaceType==2) tmpLoginUrl = URLUtils.replaceIP(this.casServerLoginUrl,ipAddress);
			urlToRedirectTo=CommonUtils.constructRedirectUrl(tmpLoginUrl, getServiceParameterName(),tmpServiceUrl, this.renew, this.gateway);
        }else{
        	urlToRedirectTo=CommonUtils.constructRedirectUrl(this.casServerLoginUrl, getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);
        }

        if (log.isDebugEnabled()) {
            log.debug("redirecting to \"" + urlToRedirectTo + "\"");
        }

        response.sendRedirect(urlToRedirectTo);
    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }
    
    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
    	this.gatewayStorage = gatewayStorage;
    }

	public String getIpFrom() {
		return ipFrom;
	}

	public void setIpFrom(String ipFrom) {
		this.ipFrom = ipFrom;
	}

	public int getReplaceType() {
		return replaceType;
	}

	public void setReplaceType(int replaceType) {
		this.replaceType = replaceType;
	}
}
