package com.zdawn.tools.gencode.model;

public class ExceptRule extends ValidateRule {
	
	private String codeDicName;
	private String content;
	
	public ExceptRule(){
		super();
		setType("exceptRule");
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCodeDicName() {
		return codeDicName;
	}
	public void setCodeDicName(String codeDicName) {
		this.codeDicName = codeDicName;
	}
}
