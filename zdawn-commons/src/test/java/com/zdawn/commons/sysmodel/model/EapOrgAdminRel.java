package com.zdawn.commons.sysmodel.model;


public class EapOrgAdminRel {
	private String fdObjectid;
	private String userId;
	private String orgId;
	private Short idLevel;
	public String getFdObjectid() {
		return fdObjectid;
	}
	public void setFdObjectid(String fdObjectid) {
		this.fdObjectid = fdObjectid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public Short getIdLevel() {
		return idLevel;
	}
	public void setIdLevel(Short idLevel) {
		this.idLevel = idLevel;
	}
}
