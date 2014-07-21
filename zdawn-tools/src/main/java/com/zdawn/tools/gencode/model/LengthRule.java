package com.zdawn.tools.gencode.model;

public class LengthRule extends ValidateRule {
	
	private String maxLength;
	private String minLength;
	
	public LengthRule(){
		super();
		setType("lengthRule");
	}
	public String getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	public String getMinLength() {
		return minLength;
	}
	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}
}
