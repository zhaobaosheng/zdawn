package com.zdawn.commons.sysmodel;

import com.zdawn.commons.sysmodel.model.EapOrgAdminRel;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;

public interface SysEapOrgMgr {
	public void insertEapOrgAdminRel(EapOrgAdminRel data) throws PersistenceException ;
}
