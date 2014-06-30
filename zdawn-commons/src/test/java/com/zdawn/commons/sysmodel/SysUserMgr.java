package com.zdawn.commons.sysmodel;

import java.util.Map;

import com.zdawn.commons.sysmodel.model.EapUser;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;

public interface SysUserMgr {
	public void saveUser(Map<String,String> para) throws PersistenceException;
	public void saveMapUser(Map<String,Object> para) throws PersistenceException;
	public void saveEapUser(EapUser user) throws PersistenceException;
	public void updateMapUser(Map<String,Object> para) throws PersistenceException;
	public void updateEapUser(EapUser user) throws PersistenceException;
	public EapUser getEapUser(Class<EapUser> clazz,String id) throws PersistenceException;
	public Map<String,Object> getMapUser(String id) throws PersistenceException;
	public void deleteData(String id) throws PersistenceException;
}
