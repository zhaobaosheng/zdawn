package com.zdawn.commons.sqlquery.domain;

import java.util.Map;

import com.zdawn.commons.sqlquery.model.ParameterMapper;

public class ConstantToken implements Token {
	
	public ConstantToken(String value){
		this.value = value;
	}
	public ConstantToken(){
	}
	private String value = null;
	
	@Override
	public String transform(Map<String, Object> data, ParameterMapper mapper) {
		return value;
	}

	@Override
	public int getTokenType() {
		return Token.CONSTANT_TOKEN;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
