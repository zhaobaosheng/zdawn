package com.zdawn.commons.sysmodel;

import java.util.Map;

import com.zdawn.commons.sysmodel.model.EapOrgAdminRel;
import com.zdawn.commons.sysmodel.model.EapUser;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;
import com.zdawn.commons.sysmodel.persistence.spring.SqlSessionDaoSupport;
import com.zdawn.commons.sysmodel.persistence.support.DataConverter;

public class SysUserMgrImpl extends SqlSessionDaoSupport implements SysUserMgr{
	private SysEapOrgMgr orgMgr = null;
	public void saveUser(Map<String,String> para) throws PersistenceException{
		try {
			Map<String,Object> data = DataConverter.convertToObjects("EapUser", para);
			getSqlSession().save("EapUser", data);
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void saveMapUser(Map<String, Object> para)
			throws PersistenceException {
		getSqlSession().save("EapUser", para);
	}

	@Override
	public void saveEapUser(EapUser user) throws PersistenceException {
		Object id = getSqlSession().save("EapUser", user);
		 getSqlSession().get(EapUser.class, "EapUser", id);
		try {
			EapOrgAdminRel eapOrgAdminRel = new EapOrgAdminRel();
			 eapOrgAdminRel.setUserId("zhaonuannuan_test");
			 eapOrgAdminRel.setOrgId("348");
			 eapOrgAdminRel.setIdLevel(new Short("11"));
			 eapOrgAdminRel.setFdObjectid("EapOrgAdminRel_2");
			 orgMgr.insertEapOrgAdminRel(eapOrgAdminRel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateMapUser(Map<String, Object> para)
			throws PersistenceException {
		getSqlSession().update("EapUser", para);
	}

	@Override
	public void updateEapUser(EapUser user) throws PersistenceException {
		getSqlSession().update("EapUser", user);
	}

	@Override
	public EapUser getEapUser(Class<EapUser> clazz, String id)
			throws PersistenceException {
		return getSqlSession().get(clazz, "EapUser", id);
	}

	@Override
	public Map<String, Object> getMapUser(String id)
			throws PersistenceException {
		return getSqlSession().getData("EapUser", id);
	}

	@Override
	public void deleteData(String id) throws PersistenceException {
		getSqlSession().delete("EapUser", id);
	}

	public void setOrgMgr(SysEapOrgMgr orgMgr) {
		this.orgMgr = orgMgr;
	}
	
}
