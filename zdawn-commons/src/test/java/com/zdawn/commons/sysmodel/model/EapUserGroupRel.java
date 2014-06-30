package com.zdawn.commons.sysmodel.model;

import java.sql.Timestamp;

public class EapUserGroupRel {
	private String fdObjectid;
	private String userId;
	private String groupId;
	private String creatorId;
	private Timestamp createTime;
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
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
