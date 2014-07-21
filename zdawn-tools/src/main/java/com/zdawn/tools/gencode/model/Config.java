package com.zdawn.tools.gencode.model;

public class Config {
	private String version;
	private String generatePath;
	private String packagePrefix;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getGeneratePath() {
		return generatePath;
	}
	public void setGeneratePath(String generatePath) {
		this.generatePath = generatePath;
	}
	public String getPackagePrefix() {
		return packagePrefix;
	}
	public void setPackagePrefix(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}
}
