package com.zdawn.tools.gencode.model;

public class RegxRule extends ValidateRule {

	private String expression;
	
	public RegxRule(){
		super();
		setType("regxRule");
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
}
