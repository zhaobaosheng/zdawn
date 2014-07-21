package com.zdawn.tools.gencode.model;

import java.util.ArrayList;
import java.util.List;

public class MetaClazz {
	private String pkg;
	private String nameCn;
	private String name;
	private List<Method> methods;
	
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public String getNameCn() {
		return nameCn;
	}
	public void setNameCn(String nameCn) {
		if(nameCn != null)
		{		
			this.nameCn = nameCn;
		}
		else
		{
			this.nameCn = "";
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Method> getMethods() {
		return methods;
	}
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
	public void addMethod(Method method){
		if(methods==null) methods = new ArrayList<Method>();
		methods.add(method);
	}
}
