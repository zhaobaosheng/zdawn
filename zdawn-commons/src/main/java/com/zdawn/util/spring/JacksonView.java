package com.zdawn.util.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.View;

import com.zdawn.util.vo.VOUtil;

public class JacksonView implements View {
	
	public static final String DEFAULT_CONTENT_TYPE = "application/json";

	private ObjectMapper objectMapper = new ObjectMapper();

	private JsonEncoding encoding = JsonEncoding.UTF8;

	private boolean disableCaching = false;
	
	@Override
	public String getContentType() {
		return "UTF-8";
	}
	private void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(getContentType());
		response.setCharacterEncoding(encoding.getJavaName());
		if (disableCaching ) {
			response.addHeader("Pragma", "no-cache");
			response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
			response.addDateHeader("Expires", 1L);
		}
	}
	@Override
	/**
	 * 序列化模型中key=ResponseMessage对象
	 */
	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		prepareResponse(request,response);
		JsonGenerator generator = objectMapper.getJsonFactory().createJsonGenerator(
				response.getOutputStream(), encoding);
		Object value = model.get("ResponseMessage");
		if(value==null) value = VOUtil.createResponseMessage("false", "data not found", "");
		objectMapper.writeValue(generator, value);
	}

}
