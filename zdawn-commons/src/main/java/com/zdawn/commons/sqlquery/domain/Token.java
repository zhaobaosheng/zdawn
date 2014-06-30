package com.zdawn.commons.sqlquery.domain;

import java.util.Map;

import com.zdawn.commons.sqlquery.model.ParameterMapper;

public interface Token {
	public static int CONSTANT_TOKEN = 1;
	public static int REPLACING_TOKEN = 2;
	public static int QUESTIONFIX_TOKEN = 3;
	public static int BLOCK_TOKEN = 4;
	/**
	 * 符号类型
	 * <br> CONSTANT_TOKEN
	 * <br> REPLACING_TOKEN
	 * <br> QUESTIONFIX_TOKEN
	 * <br> BLOCK_TOKEN
	 */
	public int getTokenType();
	/**
	 * 符号转换成文本
	 */
	public String transform(Map<String,Object> data,ParameterMapper mapper);
}
