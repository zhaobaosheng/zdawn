package com.zdawn.commons.sysmodel;

import com.zdawn.commons.sysmodel.model.EapOrgAdminRel;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;
import com.zdawn.commons.sysmodel.persistence.spring.SqlSessionDaoSupport;

public class SysEapOrgMgrImpl extends SqlSessionDaoSupport implements SysEapOrgMgr {

	@Override
	public void insertEapOrgAdminRel(EapOrgAdminRel data) throws PersistenceException {
		getSqlSession().save("EapOrgAdminRel", data);
	}
	
}
