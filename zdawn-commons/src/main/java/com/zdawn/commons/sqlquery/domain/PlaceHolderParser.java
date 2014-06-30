package com.zdawn.commons.sqlquery.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 占位符解析器
 * @author zhaobs
 *
 */
public class PlaceHolderParser {
	/**
	 * 过滤没有字符包括 \r \n \t 多余空格
	 * @param origin 
	 * @return String
	 */
	public String filterWhiteSpace(String origin){
		StringBuilder sb = new StringBuilder();
		int length = origin.length();
		char prev = ' ';
		for (int i = 0; i < length; i++) {
			char c = origin.charAt(i);
			if(c=='\n' || c=='\r' || c=='\t') c=' ';
			if(c==' '){
				if(prev !=' '){
					sb.append(c);
					prev=' ';
				}
			}else{
				sb.append(c);
				prev = c;
			}
		}
		return sb.toString();
	}
	/**
	 * 将占位符字符串解析成Token
	 * @param data 字符串
	 * @return token集合
	 */
	public List<Token> parsePlaceHolderString(String data){
		if(data==null || data.equals("")) return null;
		List<Token> tokenList = new ArrayList<Token>();
		int length = data.length();
		int index = 0;
		StringBuilder sb = new StringBuilder();
		while(index<length){
			char c = data.charAt(index);
			if(c=='$'){
				if(index+1<length && data.charAt(index+1)=='{'){
					StringBuilder keyBuilder = new StringBuilder();
					int nextIndex = parseMarkToken(index+1,data,keyBuilder);
					if(nextIndex!=-1){
						if(sb.length()>0){
							tokenList.add(new ConstantToken(sb.toString()));
							sb = new StringBuilder();
						}
						tokenList.add(new ReplacingToken(keyBuilder.toString()));
						index = nextIndex +1;
						continue;
					}
				}else if(index+1<length && data.charAt(index+1)=='['){
					StringBuilder keyBuilder = new StringBuilder();
					int nextIndex = parseBlockToken(index+1,data,keyBuilder);
					if(nextIndex!=-1){
						if(sb.length()>0){
							tokenList.add(new ConstantToken(sb.toString()));
							sb = new StringBuilder();
						}
						tokenList.add(parseBlockToken(keyBuilder.toString()));
						index = nextIndex +1;
						continue;
					}
				}
			}else if(c=='#'){
				if(index+1<length && data.charAt(index+1)=='{'){
					StringBuilder keyBuilder = new StringBuilder();
					int nextIndex = parseMarkToken(index+1,data,keyBuilder);
					if(nextIndex!=-1){
						if(sb.length()>0){
							tokenList.add(new ConstantToken(sb.toString()));
							sb = new StringBuilder();
						}
						tokenList.add(new QuestionFixToken(keyBuilder.toString()));
						index = nextIndex +1;
						continue;
					}
				}
			}
			sb.append(c);
			index = index +1;
		}
		if(sb.length()>0) tokenList.add(new ConstantToken(sb.toString()));
		return tokenList;
	}
	private Token parseBlockToken(String data) {
		BlockToken groupToken = new BlockToken();
		int length = data.length();
		int index = 0;
		StringBuilder sb = new StringBuilder();
		while(index<length){
			char c = data.charAt(index);
			if(c=='$'){
				if(index+1<length && data.charAt(index+1)=='{'){
					StringBuilder keyBuilder = new StringBuilder();
					int nextIndex = parseMarkToken(index+1,data,keyBuilder);
					if(nextIndex!=-1){
						if(sb.length()>0){
							groupToken.addToken(new ConstantToken(sb.toString()));
							sb = new StringBuilder();
						}
						groupToken.addToken(new ReplacingToken(keyBuilder.toString()));
						index = nextIndex +1;
						continue;
					}
				}
			}else if(c=='#'){
				if(index+1<length && data.charAt(index+1)=='{'){
					StringBuilder keyBuilder = new StringBuilder();
					int nextIndex = parseMarkToken(index+1,data,keyBuilder);
					if(nextIndex!=-1){
						if(sb.length()>0){
							groupToken.addToken(new ConstantToken(sb.toString()));
							sb = new StringBuilder();
						}
						groupToken.addToken(new QuestionFixToken(keyBuilder.toString()));
						index = nextIndex +1;
						continue;
					}
				}
			}
			sb.append(c);
			index = index +1;
		}
		if(sb.length()>0) groupToken.addToken(new ConstantToken(sb.toString()));
		return groupToken;
	}
	//error return -1
	private int parseMarkToken(int start, String data,StringBuilder keyBuilder) {
		int length = data.length();
		int index = start+1;
		while(index<length){
			char c = data.charAt(index);
			if(c=='}') break;
			if(keyBuilder!=null) keyBuilder.append(c);
			index = index +1;
		}
		
		return index==length ? -1:index;
	}
	//error return -1
	private int parseBlockToken(int start, String data,StringBuilder blockBuilder) {
		int length = data.length();
		int index = start+1;
		while(index<length){
			char c = data.charAt(index);
			if(c==']') break;
			if(blockBuilder!=null) blockBuilder.append(c);
			index = index +1;
		}
		return index==length ? -1:index;
	}
}
