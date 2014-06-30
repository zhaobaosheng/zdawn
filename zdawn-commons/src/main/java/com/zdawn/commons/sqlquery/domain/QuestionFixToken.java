package com.zdawn.commons.sqlquery.domain;

import java.util.Map;

import com.zdawn.commons.sqlquery.model.ParameterMapper;

public class QuestionFixToken implements Token {
	private String placeholderKey = null;
	
	public QuestionFixToken(String placeholderKey){
		this.placeholderKey = placeholderKey;
	}
	@Override
	public int getTokenType() {
		return Token.QUESTIONFIX_TOKEN;
	}

	@Override
	public String transform(Map<String, Object> data, ParameterMapper mapper) {
		return "?";
	}


	public String getPlaceholderKey() {
		return placeholderKey;
	}

	public void setPlaceholderKey(String placeholderKey) {
		this.placeholderKey = placeholderKey;
	}
	
}
