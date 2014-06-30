package com.zdawn.commons.sysmodel.model;

import java.sql.Timestamp;
import java.util.List;

public class EapUser {
	private String fdObjectid;
	private String userId;
	private String logonName;
	private String isLock;
	private String creatorId;
	private String password;
	private Timestamp createTime;
	private Short failureCount;
	
	private EapOrgAdminRel eapOrgAdminRel;
	
	private List<EapUserGroupRel> eapUserGroupRelList;
	
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
	public String getLogonName() {
		return logonName;
	}
	public void setLogonName(String logonName) {
		this.logonName = logonName;
	}
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Short getFailureCount() {
		return failureCount;
	}
	public void setFailureCount(Short failureCount) {
		this.failureCount = failureCount;
	}
	public EapOrgAdminRel getEapOrgAdminRel() {
		return eapOrgAdminRel;
	}
	public void setEapOrgAdminRel(EapOrgAdminRel eapOrgAdminRel) {
		this.eapOrgAdminRel = eapOrgAdminRel;
	}
	public List<EapUserGroupRel> getEapUserGroupRelList() {
		return eapUserGroupRelList;
	}
	public void setEapUserGroupRelList(List<EapUserGroupRel> eapUserGroupRelList) {
		this.eapUserGroupRelList = eapUserGroupRelList;
	}
	
}
