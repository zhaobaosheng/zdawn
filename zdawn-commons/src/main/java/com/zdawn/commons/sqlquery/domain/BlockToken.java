package com.zdawn.commons.sqlquery.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zdawn.commons.sqlquery.model.ParameterMapper;

public class BlockToken implements Token {
	
	private List<Token> childTokenList = new ArrayList<Token>();
	
	@Override
	public int getTokenType() {
		return Token.BLOCK_TOKEN;
	}
	
	@Override
	public String transform(Map<String, Object> data, ParameterMapper mapper) {
		if(skip(data)) return "";
		StringBuilder sb = new StringBuilder();
		for (Token token : childTokenList) {
			if(token.getTokenType()==Token.BLOCK_TOKEN){
				System.out.println("warning should not have block token");
				continue;
			}
			sb.append(token.transform(data,mapper));
		}
		return sb.toString();
	}

	private boolean skip(Map<String, Object> data) {
		boolean skip = false;
		for (Token token : childTokenList) {
			if(token.getTokenType()==Token.QUESTIONFIX_TOKEN){
				QuestionFixToken question=(QuestionFixToken)token;
				Object obj = data.get(question.getPlaceholderKey());
				if(obj==null || obj.equals("")){
					skip=true;
					break;
				}
			}else if(token.getTokenType()==Token.REPLACING_TOKEN){
				ReplacingToken replacing=(ReplacingToken)token;
				Object obj = data.get(replacing.getPlaceholderKey());
				if(obj==null || obj.equals("")){
					skip=true;
					break;
				}
			}
		}
		return skip;
	}

	public void addToken(Token token){
		childTokenList.add(token);
	}
	
	public List<QuestionFixToken> getQuestionFixTokens(Map<String, Object> para){
		List<QuestionFixToken> list = new ArrayList<QuestionFixToken>();
		if(skip(para)) return list;
		for (Token token : childTokenList) {
			if(token.getTokenType()==Token.QUESTIONFIX_TOKEN){
				QuestionFixToken question=(QuestionFixToken)token;
				list.add(question);
			}
		}
		return list;
	}
	
}
