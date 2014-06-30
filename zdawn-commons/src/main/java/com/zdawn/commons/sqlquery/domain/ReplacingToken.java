package com.zdawn.commons.sqlquery.domain;

import java.util.Map;

import com.zdawn.commons.sqlquery.model.ParameterItem;
import com.zdawn.commons.sqlquery.model.ParameterMapper;
import com.zdawn.util.convert.ConvertUtil;

public class ReplacingToken implements Token {
	
	private String placeholderKey = null;
	
	public ReplacingToken(String placeholderKey){
		this.placeholderKey = placeholderKey;
	}
	@Override
	public int getTokenType() {
		return Token.REPLACING_TOKEN;
	}

	@Override
	public String transform(Map<String, Object> data, ParameterMapper mapper) {
		ParameterItem item = mapper.getParameterItem(placeholderKey);
		return ConvertUtil.convertToString(data.get(placeholderKey),item.getToStringformat());
	}
	public String getPlaceholderKey() {
		return placeholderKey;
	}

	public void setPlaceholderKey(String placeholderKey) {
		this.placeholderKey = placeholderKey;
	}
	
}
