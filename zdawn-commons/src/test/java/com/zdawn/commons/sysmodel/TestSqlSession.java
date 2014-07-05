package com.zdawn.commons.sysmodel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zdawn.commons.sysmodel.model.EapOrgAdminRel;
import com.zdawn.commons.sysmodel.model.EapUser;
import com.zdawn.commons.sysmodel.model.EapUserGroupRel;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;
import com.zdawn.commons.sysmodel.persistence.support.DataConverter;

public class TestSqlSession extends TestCase {
	
	private ApplicationContext ctx = null;
	
	public void initApplicationContext(){
		ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
	}
	
	public static void main(String[] arg){
		TestSqlSession testSqlSession = new TestSqlSession();
		testSqlSession.initApplicationContext();
		//Map
		testSqlSession.testSaveMapUser();
		testSqlSession.testUpdateMapUser();
		testSqlSession.testDeleteUser();
		//JavaBean
//		testSqlSession.testSaveEapUser();
//		testSqlSession.testUpdateEapUser();
//		testSqlSession.testDeleteUser();
	}

	public void testSaveMapData() {
		Map<String,String> para = new HashMap<String, String>();
		SysUserMgr sysUserMgr= ctx.getBean("sysUserMgr",SysUserMgr.class);
		try {
			para.put("fdObjectid",UUID.randomUUID().toString());
			para.put("userId","zhaoxusheng");
			para.put("logonName","zhaoxusheng");
			para.put("isLock","1");
			para.put("creatorId","admin");
			para.put("password","111111");
			para.put("createTime","2014-06-28 14:35:30");
			para.put("failureCount","5");
			sysUserMgr.saveUser(para);
		} catch (PersistenceException e) {}
	}
	public void testSaveMapUser(){
		SysUserMgr sysUserMgr= ctx.getBean("sysUserMgr",SysUserMgr.class);
		try {
			Map<String,String> para = new HashMap<String, String>();
			para.put("fdObjectid","EapUser_1");
			para.put("userId","zhaonuannuan");
			para.put("logonName","zhaonuannuan");
			para.put("isLock","1");
			para.put("creatorId","admin");
			para.put("password","111111");
			para.put("createTime","2014-06-28 14:35:30");
			para.put("failureCount","5");
			Map<String,Object>  data = DataConverter.convertToObjects("EapUser", para);
			//EAP_USER_GROUP_REL
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			 para = new HashMap<String, String>();
			 para.put("userId","zhaonuannuan");
			 para.put("creatorId","admin");
			 para.put("createTime","2014-06-28 14:35:30");
			 para.put("groupId","GROP000067110503");
			 para.put("fdObjectid","EapUserGroupRel_1");
			 list.add(DataConverter.convertToObjects("EapUserGroupRel", para));
			 
			 para = new HashMap<String, String>();
			 para.put("userId","zhaonuannuan");
			 para.put("creatorId","admin");
			 para.put("createTime","2014-06-28 14:35:30");
			 para.put("groupId","GROP000065110422");
			 para.put("fdObjectid","EapUserGroupRel_2");
			 list.add(DataConverter.convertToObjects("EapUserGroupRel", para));
			 data.put("eapUserGroupRelList", list);
			 
			 //EapOrgAdminRel
			 para = new HashMap<String, String>();
			 para.put("userId","zhaonuannuan");
			 para.put("orgId","348");
			 para.put("idLevel","21");
			 para.put("fdObjectid","EapOrgAdminRel_1");
			 data.put("eapOrgAdminRel", DataConverter.convertToObjects("EapOrgAdminRel", para));
			sysUserMgr.saveMapUser(data);
		} catch (PersistenceException e) {}
	}
	
	public void testUpdateMapUser(){
		try {
			SysUserMgr sysUserMgr= ctx.getBean("sysUserMgr",SysUserMgr.class);
			Map<String, Object> data = sysUserMgr.getMapUser("EapUser_1");
			data.put("password", "222222");
			Map<String, Object> orgAmin =(Map<String, Object>) data.get("eapOrgAdminRel");
			orgAmin.put("idLevel", new Short("12"));
			
			 List<Map<String,Object>> list = ( List<Map<String,Object>>) data.get("eapUserGroupRelList");
			 HashMap<String, Object> temp = new HashMap<String, Object>();
			 temp.put("userId","zhaonuannuan");
			 temp.put("creatorId","admin");
			 temp.put("createTime",new Timestamp(System.currentTimeMillis()));
			 temp.put("groupId","GROP000065110455");
			 temp.put("fdObjectid","EapUserGroupRel_3");
			 list.add(temp);
			 
			 sysUserMgr.updateMapUser(data);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testSaveEapUser(){
		try {
			SysUserMgr sysUserMgr= ctx.getBean("sysUserMgr",SysUserMgr.class);
			EapUser user = new EapUser();
			user.setFdObjectid("EapUser_1");
			user.setUserId("zhaonuannuan");
			user.setLogonName("zhaonuannuan");
			user.setIsLock("1");
			user.setCreatorId("admin");
			user.setCreateTime(new Timestamp(System.currentTimeMillis()));
			user.setPassword("111111");
			user.setFailureCount(new Short("5"));
			//EAP_USER_GROUP_REL
			List<EapUserGroupRel> list = new ArrayList<EapUserGroupRel>();
			EapUserGroupRel tmp = new EapUserGroupRel();
			tmp.setUserId("zhaonuannuan");
			tmp.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tmp.setCreatorId("admin");
			tmp.setGroupId("GROP000067110503");
			tmp.setFdObjectid("EapUserGroupRel_1");
			 list.add(tmp);
			 
			tmp = new EapUserGroupRel();
			tmp.setUserId("zhaonuannuan");
			tmp.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tmp.setCreatorId("admin");
			tmp.setGroupId("GROP000065110422");
			tmp.setFdObjectid("EapUserGroupRel_2");
			 list.add(tmp);
			 
			 user.setEapUserGroupRelList(list);
			 
			 //EapOrgAdminRel
			 EapOrgAdminRel eapOrgAdminRel = new EapOrgAdminRel();
			 eapOrgAdminRel.setUserId("zhaonuannuan");
			 eapOrgAdminRel.setOrgId("348");
			 eapOrgAdminRel.setIdLevel(new Short("5"));
			 eapOrgAdminRel.setFdObjectid("EapOrgAdminRel_1");
			 
			 user.setEapOrgAdminRel(eapOrgAdminRel);
			
			
			sysUserMgr.saveEapUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testUpdateEapUser(){
		try {
			SysUserMgr sysUserMgr= ctx.getBean("sysUserMgr",SysUserMgr.class);
			EapUser user =  sysUserMgr.getEapUser(EapUser.class, "EapUser_1");
			user.setPassword("222222");
			user.getEapOrgAdminRel().setIdLevel(new Short("12"));
			
			EapUserGroupRel tmp = new EapUserGroupRel();
			tmp.setUserId("zhaonuannuan");
			tmp.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tmp.setCreatorId("admin");
			tmp.setGroupId("GROP000065110111");
			tmp.setFdObjectid("EapUserGroupRel_3");
			user.getEapUserGroupRelList().add(tmp);
			
			 sysUserMgr.updateEapUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testDeleteUser(){
		try {
			SysUserMgr sysUserMgr= ctx.getBean("sysUserMgr",SysUserMgr.class);
			 sysUserMgr.deleteData("EapUser_1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
