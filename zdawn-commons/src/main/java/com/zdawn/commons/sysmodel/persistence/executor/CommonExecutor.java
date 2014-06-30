package com.zdawn.commons.sysmodel.persistence.executor;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.JdbcUtils;
import com.zdawn.commons.jdbc.support.AbstractType;
import com.zdawn.commons.jdbc.support.TypeUtil;
import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.Relation;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;
import com.zdawn.util.Utils;
import com.zdawn.util.beans.BeanUtil;

/**
 * 存储数据标准实现
 * @author zhaobs
 * 2014-06-10
 */
public class CommonExecutor implements Executor {
	private static final Logger log = LoggerFactory.getLogger(CommonExecutor.class);
	/**
	 * 新增操作
	 * @param entityName 实体名
	 * @param data 数据
	 * @param sysModel 数据字典
	 * @param con 数据库连接
	 * @return 实体ID
	 * @throws PersistenceException
	 */
	public Serializable insert(String entityName, Map<String, Object> data,
			SysModel sysModel, Connection con) throws PersistenceException {
		Serializable id = null;
		Entity mainEntity = sysModel.findEntityByName(entityName);
		List<Property> propertyList = new ArrayList<Property>();
		Map<Relation,Object> relationMap = new HashMap<Relation,Object>(4);
		for (Map.Entry<String,Object> entry : data.entrySet()) {
			//collect insert property
			Property temp = mainEntity.findPropertyByName(entry.getKey());
			if(temp!=null){
				propertyList.add(temp);
				continue;
			}
			//check insert relative entity
			Relation relation = findEntityRelation(sysModel,
					mainEntity.getRelations(), entry.getKey(), entry.getValue());
			if(relation==null) continue;
			relationMap.put(relation,entry.getValue());
		}
		//insert main entity
		propertyList = ExecutorHelper.sortEntityProperty(mainEntity, propertyList);
		Property unique = mainEntity.findUniqueColumnProperty();
		if(unique.getDefaultValue().equals("auto_increment")){
			propertyList = SqlBuilder.filterProperty(propertyList,unique.getName());
			id = insertEntityForAutoIncrement(mainEntity, propertyList, data, con);
		}else{
			id = insertEntity(mainEntity, propertyList, data, con);
		}
		//insert other entity
		for (Map.Entry<Relation,Object> entry : relationMap.entrySet()) {
			Relation relation = entry.getKey();
			Entity entity = sysModel.findEntityByName(relation.getEntityName());
			Object fk = id;
			if (!relation.getSelfColumn().equals(unique.getColumn())){
				fk = data.get(relation.getSelfPropertyName());					
			}
			if(relation.getType().equals("oneToOne")){
				insertRelatedEntity(fk,relation,entity,entry.getValue(),con);
			}else if(relation.getType().equals("oneToMany")){
				insertChildEntity(fk,relation,entity,entry.getValue(),con);
			}else{
				log.warn(relation.getDescription()+" not support relation type "+relation.getType());
			}
		}
		return id;
	}

	public <T> Serializable insert(String entityName, T object,
			SysModel sysModel, Connection con) throws PersistenceException {
		Map<String, Object> data = BeanUtil.transformBeanToMap(object);
		Serializable id = insert(entityName, data, sysModel, con);
		Entity mainEntity = sysModel.findEntityByName(entityName);
		Property unique = mainEntity.findUniqueColumnProperty();
		boolean autoIncrement = unique.getDefaultValue().equals("auto_increment");
		if(autoIncrement){
			BeanUtil.bindBeanOneProperty(object,unique.getName(),id);
		}
		return id;
	}
	//find save relative entity
	@SuppressWarnings("unchecked")
	private Relation findEntityRelation(SysModel sysModel,List<Relation> relations, String key,
			Object value) {
		if(value==null) return null;
		for (Relation relation : relations) {
			if(relation.getType().equals("oneToOne")){
				boolean isMap = value instanceof Map;
				if(isMap){
					if(key.equalsIgnoreCase(relation.getEntityName())) return relation;
				}else{
					Entity entity = sysModel.findEntityByName(relation.getEntityName());
					if(value.getClass().getName().equals(entity.getClazz())) return relation;
				}
			}else if(relation.getType().equals("oneToMany") && value instanceof List){
				List<Object> dataList = (List<Object>)value;
				if(dataList.size()==0) continue;
				Object tmp = dataList.get(0);
				boolean isMap = tmp instanceof Map;
				if(isMap){
					if(key.equalsIgnoreCase(relation.getEntityName()+"List")) return relation;
				}else{
					Entity entity = sysModel.findEntityByName(relation.getEntityName());
					if(tmp.getClass().getName().equals(entity.getClazz())) return relation;
				}
			}
		}
		 if (log.isDebugEnabled()) {
	          log.debug("can not dispose data [ key=" +key + " value="+value+"]");
	     }
		return null;
	}
	@SuppressWarnings("unchecked")
	private void insertChildEntity(Object pid, Relation relation,
			Entity entity, Object data,Connection con) throws PersistenceException{
		try {
			List<Object> dataList = (List<Object>)data;
			if(dataList.size()==0) return;
			Property unique = entity.findUniqueColumnProperty();
			boolean autoIncrement = unique.getDefaultValue().equals("auto_increment");
			for (Object oneData : dataList) {
				//judge map or bean
				boolean isMap = oneData instanceof Map;
				Map<String, Object> childData = null;
				if(isMap){
					childData = (Map<String, Object>)oneData;
					childData.put(relation.getPropertyName(),pid);
				}else{//bean set relation property and get Map value
					BeanUtil.bindBeanOneProperty(oneData,relation.getPropertyName(),pid);
					childData = BeanUtil.transformBeanToMap(oneData);
				}
				//collect insert property
				List<Property> propertyList = new ArrayList<Property>();
				for (Map.Entry<String,Object> entry : childData.entrySet()) {
					Property temp = entity.findPropertyByName(entry.getKey());
					if(temp!=null){
						propertyList.add(temp);
						continue;
					}
				}
				propertyList = ExecutorHelper.sortEntityProperty(entity, propertyList);
				if(autoIncrement){
					propertyList = SqlBuilder.filterProperty(propertyList,unique.getName());
					Serializable relatedEntityId = insertEntityForAutoIncrement(entity, propertyList, childData,con);
					if(!isMap){
						//set unique property
						BeanUtil.bindBeanOneProperty(oneData,unique.getName(),relatedEntityId);
					}
				}else{
					insertEntity(entity, propertyList, childData, con);
				}
			}
		} catch (PersistenceException e) {
			throw e;
		} catch (Exception e) {
			log.error("insertChildEntity",e);
			throw new PersistenceException("",e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	private void insertRelatedEntity(Object pid, Relation relation,
			Entity entity, Object data,Connection con) throws PersistenceException{
		try {
			//judge map or bean
			boolean isMap = data instanceof Map;
			Map<String, Object> relatedData = null;
			if(isMap){
				relatedData = (Map<String, Object>)data;
				relatedData.put(relation.getPropertyName(),pid);
			}else{//bean set relation property and get Map value
				BeanUtil.bindBeanOneProperty(data,relation.getPropertyName(),pid);
				relatedData = BeanUtil.transformBeanToMap(data);
			}
			//collect insert property
			List<Property> propertyList = new ArrayList<Property>();
			for (Map.Entry<String,Object> entry : relatedData.entrySet()) {
				Property temp = entity.findPropertyByName(entry.getKey());
				if(temp!=null){
					propertyList.add(temp);
					continue;
				}
			}
			//insert entity
			propertyList = ExecutorHelper.sortEntityProperty(entity, propertyList);
			Property unique = entity.findUniqueColumnProperty();
			if(unique.getDefaultValue().equals("auto_increment")){
				propertyList = SqlBuilder.filterProperty(propertyList,unique.getName());
				Serializable relatedEntityId = insertEntityForAutoIncrement(entity, propertyList, relatedData,con);
				if(!isMap){
					//set unique property
					BeanUtil.bindBeanOneProperty(data,unique.getName(),relatedEntityId);
				}
			}else{
				insertEntity(entity, propertyList, relatedData, con);
			}
		} catch (PersistenceException e) {
			throw e;
		} catch (Exception e) {
			log.error("insertRelatedEntity",e);
			throw new PersistenceException("",e.getMessage());
		}
	}

	private Serializable insertEntityForAutoIncrement(Entity entity,
			List<Property> propertyList, Map<String, Object> data,
			Connection con) throws PersistenceException {
		Serializable id = null;
		PreparedStatement ps = null;
		ResultSet resultset = null;
		try {
			String sql = SqlBuilder.createInsertSql(entity.getTableName(),propertyList);
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < propertyList.size(); i++){
				Property property = propertyList.get(i);
				AbstractType type = TypeUtil.getDataType(property.getType());
				type.set(ps, data.get(property.getName()), i+1);
			}
			ps.executeUpdate();
			resultset = ps.getGeneratedKeys();
			if(resultset.next()){
				Property unique = entity.findUniqueColumnProperty();
				AbstractType type = TypeUtil.getDataType(unique.getType());
				id = (Serializable)type.get(resultset, 1);
				data.put(unique.getName(),id);
			}
		} catch (SQLException e) {
			log.error("insertEntityForAutoIncrement",e);
		    throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return id;
	}
	private Serializable insertEntity(Entity entity,
			List<Property> propertyList, Map<String, Object> data,
			Connection con) throws PersistenceException {
		Serializable id = null;
		PreparedStatement ps = null;
		try {
			String sql = SqlBuilder.createInsertSql(entity.getTableName(),propertyList);
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = con.prepareStatement(sql);
			for (int i = 0; i < propertyList.size(); i++){
				Property property = propertyList.get(i);
				AbstractType type = TypeUtil.getDataType(property.getType());
				type.set(ps, data.get(property.getName()), i+1);
			}
			ps.executeUpdate();
			id = (Serializable)data.get(entity.findUniqueColumnProperty().getName());
		} catch (SQLException e) {
			log.error("insertEntity",e);
		    throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeStatement(ps);
		}
		return id;
	}
	/**
	 * 更新操作
	 * @param entityName 实体名
	 * @param data 数据
	 * @param sysModel 数据字典
	 * @param con 数据连接
	 * @throws PersistenceException
	 */
	public void update(String entityName, Map<String, Object> data,
			SysModel sysModel, Connection con) throws PersistenceException {
		Entity mainEntity = sysModel.findEntityByName(entityName);
		Property unique = mainEntity.findUniqueColumnProperty();
		Serializable id = (Serializable)data.get(unique.getName());
		if(id==null) throw new PersistenceException("","not found primary key");
		List<Property> propertyList = new ArrayList<Property>();
		Map<Relation,Object> relationMap = new HashMap<Relation,Object>(4);
		for (Map.Entry<String,Object> entry : data.entrySet()) {
			//collect update property
			Property temp = mainEntity.findPropertyByName(entry.getKey());
			if(temp!=null){
				if(temp!=unique) propertyList.add(temp);
				continue;
			}
			//check update relative entity
			Relation relation = findEntityRelation(sysModel,
					mainEntity.getRelations(), entry.getKey(), entry.getValue());
			if(relation==null) continue;
			relationMap.put(relation,entry.getValue());
		}
		//update main entity
		propertyList = ExecutorHelper.sortEntityProperty(mainEntity, propertyList);
		updateEntity(mainEntity,unique,propertyList,data,con);
		//update other entity
		for (Map.Entry<Relation,Object> entry : relationMap.entrySet()) {
			Relation relation = entry.getKey();
			Entity entity = sysModel.findEntityByName(relation.getEntityName());
			Object fk = id;
			if (!relation.getSelfColumn().equals(unique.getColumn())){
				fk = data.get(relation.getSelfPropertyName());					
			}
			if(relation.getType().equals("oneToOne")){
				updateRelatedEntity(fk,relation,entity,entry.getValue(),con);
			}else if(relation.getType().equals("oneToMany")){
				updateChildEntity(fk,relation,entity,entry.getValue(),con);
			}else{
				log.warn(relation.getDescription()+" not support relation type "+relation.getType());
			}
		}
	}

	public <T> void update(String entityName, T object, SysModel sysModel,
			Connection con) throws PersistenceException {
		Map<String, Object> data = BeanUtil.transformBeanToMap(object);
		update(entityName, data, sysModel, con);
	}
	private void updateEntity(Entity entity,Property unique,List<Property> propertyList,
			Map<String, Object> data, Connection con) throws PersistenceException{
		PreparedStatement ps = null;
		try {
			if(unique==null) entity.findUniqueColumnProperty();
			String sql = SqlBuilder.createUpdateSql(entity.getTableName(),unique,propertyList);
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = con.prepareStatement(sql);
			for (int i = 0; i < propertyList.size(); i++){
				Property property = propertyList.get(i);
				AbstractType type = TypeUtil.getDataType(property.getType());
				type.set(ps, data.get(property.getName()), i+1);
			}
			AbstractType type = TypeUtil.getDataType(unique.getType());
			type.set(ps, data.get(unique.getName()),propertyList.size()+1);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.error("updateEntity",e);
		    throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeStatement(ps);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateRelatedEntity(Object pid,Relation relation, Entity entity,
			Object data, Connection con) throws PersistenceException{
		try {
			//judge map or bean
			boolean isMap = data instanceof Map;
			Map<String, Object> relatedData = null;
			if(isMap){
				relatedData = (Map<String, Object>)data;
			}else{//bean get Map value
				relatedData = BeanUtil.transformBeanToMap(data);
			}
			//judge insert or update
			Property unique = entity.findUniqueColumnProperty();
			Object id = relatedData.get(unique.getName());
			boolean exist = existEntity(entity.getTableName(),unique,id,con);
			if(exist){
				//collect update property
				List<Property> propertyList = new ArrayList<Property>();
				for (Map.Entry<String,Object> entry : relatedData.entrySet()) {
					Property temp = entity.findPropertyByName(entry.getKey());
					if(temp!=null){
						if(temp!=unique) propertyList.add(temp);
						continue;
					}
				}
				propertyList = ExecutorHelper.sortEntityProperty(entity, propertyList);
				updateEntity(entity, unique, propertyList, relatedData, con);
			}else{//insert
				relatedData.put(relation.getPropertyName(),pid);
				if(!isMap){
					BeanUtil.bindBeanOneProperty(data,relation.getPropertyName(),pid);
				}
				//collect insert property
				List<Property> propertyList = new ArrayList<Property>();
				for (Map.Entry<String,Object> entry : relatedData.entrySet()) {
					Property temp = entity.findPropertyByName(entry.getKey());
					if(temp!=null){
						propertyList.add(temp);
						continue;
					}
				}
				propertyList = ExecutorHelper.sortEntityProperty(entity, propertyList);
				if(unique.getDefaultValue().equals("auto_increment")){
					propertyList = SqlBuilder.filterProperty(propertyList,unique.getName());
					Serializable relatedEntityId = insertEntityForAutoIncrement(entity, propertyList,relatedData,con);
					if(!isMap){
						//set unique property
						BeanUtil.bindBeanOneProperty(data,unique.getName(),relatedEntityId);
					}
				}else{
					insertEntity(entity, propertyList, relatedData, con);
				}
			}
		} catch (PersistenceException e) {
			throw e;
		} catch (Exception e) {
			log.error("updateRelatedEntity",e);
			throw new PersistenceException("",e.getMessage());
		}
	}

	private boolean existEntity(String tableName,Property unique, Object id, Connection con)
			throws PersistenceException {
		if(id==null) return false;
		boolean result = false;
		try {
			boolean number = TypeUtil.isNumber(unique.getType());
			StringBuilder sb = new StringBuilder();
			sb.append("select count(*) from ").append(tableName);
			sb.append(" where ").append(unique.getColumn()).append('=');
			if(number){
				sb.append(id);
			}else{
				sb.append('\'').append(id).append('\'');
			}
			int count = JdbcUtils.getIntFunctionNumber(con, sb.toString());
			result = count==1;
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private void updateChildEntity(Object pid, Relation relation,
			Entity entity, Object data, Connection con) throws PersistenceException{
		try {
			List<Object> dataList = (List<Object>)data;
			if(dataList.size()==0) return;
			Property unique = entity.findUniqueColumnProperty();
			List<Object> primaryKeyList = queryExistPrimaryKey(entity.getTableName(),unique,dataList,con);
			for (Object oneData : dataList) {
				//judge map or bean
				boolean isMap = oneData instanceof Map;
				Map<String, Object> childData = null;
				if(isMap){
					childData = (Map<String, Object>)oneData;
				}else{//bean set relation property and get Map value
					childData = BeanUtil.transformBeanToMap(oneData);
				}
				Object id = childData.get(unique.getName());
				boolean exist = id!=null && primaryKeyList.contains(id);
				if(exist){
					//collect update property
					List<Property> propertyList = new ArrayList<Property>();
					for (Map.Entry<String,Object> entry : childData.entrySet()) {
						Property temp = entity.findPropertyByName(entry.getKey());
						if(temp!=null){
							if(temp!=unique) propertyList.add(temp);
							continue;
						}
					}
					propertyList = ExecutorHelper.sortEntityProperty(entity, propertyList);
					updateEntity(entity, unique, propertyList, childData, con);
				}else{//insert
					childData.put(relation.getPropertyName(),pid);
					if(!isMap){
						BeanUtil.bindBeanOneProperty(oneData,relation.getPropertyName(),pid);
					}
					//collect insert property
					List<Property> propertyList = new ArrayList<Property>();
					for (Map.Entry<String,Object> entry : childData.entrySet()) {
						Property temp = entity.findPropertyByName(entry.getKey());
						if(temp!=null){
							propertyList.add(temp);
							continue;
						}
					}
					propertyList = ExecutorHelper.sortEntityProperty(entity, propertyList);
					if(unique.getDefaultValue().equals("auto_increment")){
						propertyList = SqlBuilder.filterProperty(propertyList,unique.getName());
						Serializable relatedEntityId = insertEntityForAutoIncrement(entity, propertyList,childData,con);
						if(!isMap){
							//set unique property
							BeanUtil.bindBeanOneProperty(oneData,unique.getName(),relatedEntityId);
						}
					}else{
						insertEntity(entity, propertyList, childData, con);
					}
				}
			}
		} catch (PersistenceException e) {
			throw e;
		} catch (Exception e) {
			log.error("updateChildEntity",e);
			throw new PersistenceException("",e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object> queryExistPrimaryKey(String tableName,Property unique,
			List<Object> dataList, Connection con) throws PersistenceException{
		List<Object> primaryKeys = new ArrayList<Object>();
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			PropertyDescriptor uniqueDescriptor = null;
			for (Object object : dataList) {
				if(object instanceof Map){
					Map<String, Object> childData = (Map<String, Object>)object;
					Object id = childData.get(unique.getName());
					if(id!=null) primaryKeys.add(id);
				}else{
					if(uniqueDescriptor==null){
					   uniqueDescriptor = BeanUtil.getBeanPropertyDescriptor(object.getClass(),unique.getName());
					}
					Object id = BeanUtil.getBeanOnePropertyValue(object, uniqueDescriptor);
					if(id!=null) primaryKeys.add(id);
				}
			}
			if(primaryKeys.size()==0) return primaryKeys;
			boolean number = TypeUtil.isNumber(unique.getType());
			StringBuilder sb = new StringBuilder();
			sb.append("select ").append(unique.getColumn()).append(" from ").append(tableName);
			sb.append(" where ").append(unique.getColumn()).append(" in(");
			if(number){
				for (int i = 0; i < primaryKeys.size(); i++) {
					Object key = primaryKeys.get(i);
					if(i==0){
						sb.append(key);
					}else{
						sb.append(',').append(key);
					}
				}
			}else{
				for (int i = 0; i < primaryKeys.size(); i++) {
					Object key = primaryKeys.get(i);
					if(i==0){
						sb.append('\'').append(key).append('\'');
					}else{
						sb.append(',').append('\'').append(key).append('\'');
					}
				}
			}
			sb.append(')');
			primaryKeys = null;
			primaryKeys = new ArrayList<Object>();
			ps = con.prepareStatement(sb.toString());
			resultset = ps.executeQuery();
			AbstractType type = TypeUtil.getDataType(unique.getType());
			while(resultset.next()){
				primaryKeys.add(type.get(resultset,1));
			}
		} catch (SQLException e) {
			log.error("queryExistPrimaryKey",e);
			throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return primaryKeys;
	}

	public void delete(String entityName, Object id, SysModel sysModel,
			Connection con) throws PersistenceException {
		List<String> sqls = new ArrayList<String>();
		Entity mainEntity = sysModel.findEntityByName(entityName);
		Property unique = mainEntity.findUniqueColumnProperty();
		boolean number = TypeUtil.isNumber(unique.getType());
		String sql = SqlBuilder.createDeleteSql(mainEntity.getTableName(),unique.getColumn(),id,number);
		sqls.add(sql);
		List<Relation> relationList = mainEntity.getRelations();
		Object relationID = null;
		for (Relation relation : relationList) {
			Entity entity = sysModel.findEntityByName(relation.getEntityName());
			Property property = entity.findPropertyByName(relation.getPropertyName());
			number = TypeUtil.isNumber(property.getType());
			if (relation.getSelfColumn().equals(unique.getColumn())){
				sql = SqlBuilder.createDeleteSql(relation.getTableName(),property.getColumn(),id,number);
				sqls.add(sql);
			}else{
				if(relationID==null) relationID = getRelatedColumnValue(mainEntity,relation.getSelfColumn(),unique,id,con);
				sql = SqlBuilder.createDeleteSql(relation.getTableName(),property.getColumn(),relationID,number);
				sqls.add(sql);
			}
		}
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			for (int i = 0; i < sqls.size(); i++) {
				if (log.isDebugEnabled()) {
					log.debug(sql);
				}
				stmt.executeUpdate(sqls.get(i));
			}
		} catch (SQLException e) {
			log.error("delete",e);
			throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeStatement(stmt);
		}
	}

	private Object getRelatedColumnValue(Entity mainEntity, String relColumn,
			Property mainUnique, Object id, Connection con) throws PersistenceException{
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(relColumn).append(" from ").append(mainEntity.getTableName());
		sb.append(" where ").append(mainUnique.getColumn()).append('=');
		boolean number = TypeUtil.isNumber(mainUnique.getType());
		if(number){
			sb.append(id);
		}else{
			sb.append('\'').append(id).append('\'');
		}
		Object relValue = null;
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			ps = con.prepareStatement(sb.toString());
			resultset = ps.executeQuery();
			if(resultset.next()){
				Property property = mainEntity.findPropertyByColumn(relColumn);
				AbstractType type = TypeUtil.getDataType(property.getType());
				relValue = type.get(resultset,property.getColumn());
			}
		} catch (SQLException e) {
			log.error("getRelatedColumnValue",e);
			throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return relValue;
	}

	public Map<String, Object> getData(String entityName, Object id,
			SysModel sysModel, Connection con) throws PersistenceException {
		Entity mainEntity = sysModel.findEntityByName(entityName);
		Property unique = mainEntity.findUniqueColumnProperty();
		Map<String, Object> data = getEntity(mainEntity,id,con);
		List<Relation> relationList = mainEntity.getRelations();
		for (Relation relation : relationList) {
			Entity entity = sysModel.findEntityByName(relation.getEntityName());
			Object fk = id;
			if (!relation.getSelfColumn().equals(unique.getColumn())){
				fk = data.get(relation.getSelfPropertyName());
			}
			if(relation.getType().equals("oneToOne")){
				Map<String, Object> mapData = getRelatedEntity(relation,entity,fk,con);
				data.put(Utils.firstLowerCase(entity.getName()), mapData);
			}else if(relation.getType().equals("oneToMany")){
				List<Map<String, Object>> mapDataList = getChildEntity(relation,entity,fk,con);
				data.put(Utils.firstLowerCase(entity.getName())+"List", mapDataList);
			}else{
				log.warn(relation.getDescription()+" not support relation type "+relation.getType());
			}
		}
		return data;
	}
	
	public <T> T get(Class<T> clazz,String entityName, Object id, SysModel sysModel,
			Connection con) throws PersistenceException {
		Entity mainEntity = sysModel.findEntityByName(entityName);
		Property unique = mainEntity.findUniqueColumnProperty();
		Map<String, Object> data = getEntity(mainEntity,id,con);
		List<Relation> relationList = mainEntity.getRelations();
		for (Relation relation : relationList) {
			Entity entity = sysModel.findEntityByName(relation.getEntityName());
			Object fk = id;
			if (!relation.getSelfColumn().equals(unique.getColumn())){
				fk = data.get(relation.getSelfPropertyName());
			}
			if(relation.getType().equals("oneToOne")){
				Map<String, Object> mapData = getRelatedEntity(relation,entity,fk,con);
				if(mapData.size()>0){
					if(entity.getClazz().equals("")) throw new PersistenceException("","entity class not define");
					data.put(Utils.firstLowerCase(entity.getName()),BeanUtil.bindBean(entity.getClazz(),mapData));
				}
			}else if(relation.getType().equals("oneToMany")){
				List<Map<String, Object>> mapDataList = getChildEntity(relation,entity,fk,con);
				if(mapDataList.size()>0){
					if(entity.getClazz().equals("")) throw new PersistenceException("","entity class not define");
					List<Object> childList = new ArrayList<Object>();
					for (Map<String, Object> one : mapDataList) {
						childList.add(BeanUtil.bindBean(entity.getClazz(),one));
					}
					data.put(Utils.firstLowerCase(entity.getName())+"List",childList);
				}
			}else{
				log.warn(relation.getDescription()+" not support relation type "+relation.getType());
			}
		}
		return BeanUtil.bindBean(clazz,data);
	}
	
	private List<Map<String, Object>> getChildEntity(Relation relation,
			Entity entity, Object pid, Connection con) throws PersistenceException{
		Property property = entity.findPropertyByName(relation.getPropertyName());
		boolean number = TypeUtil.isNumber(property.getType());
		String sql = SqlBuilder.createSelectEntitySql(entity,property,pid,number);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = con.prepareStatement(sql);
			resultset = ps.executeQuery();
			List<Property> all = entity.getProperties();
			while(resultset.next()){
				Map<String, Object> data = new HashMap<String, Object>();
				for (Property temp : all) {
					AbstractType type = TypeUtil.getDataType(temp.getType());
					data.put(temp.getName(),type.get(resultset,temp.getColumn()));
				}
				list.add(data);
			}
		} catch (SQLException e) {
			log.error("getChildEntity",e);
			throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return list;
	}

	private Map<String, Object> getRelatedEntity(Relation relation,
			Entity entity, Object pid, Connection con) throws PersistenceException{
		Property property = entity.findPropertyByName(relation.getPropertyName());
		boolean number = TypeUtil.isNumber(property.getType());
		String sql = SqlBuilder.createSelectEntitySql(entity,property,pid,number);
		PreparedStatement ps = null;
	    ResultSet resultset = null;
	    Map<String, Object> data = new HashMap<String, Object>();
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = con.prepareStatement(sql);
			resultset = ps.executeQuery();
			if(resultset.next()){
				List<Property> all = entity.getProperties();
				for (Property temp : all) {
					AbstractType type = TypeUtil.getDataType(temp.getType());
					data.put(temp.getName(),type.get(resultset,temp.getColumn()));
				}
			}
		} catch (SQLException e) {
			log.error("getRelatedEntity",e);
			throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return data;
	}

	private Map<String, Object> getEntity(Entity entity, Object id,
			Connection con) throws PersistenceException{
		Map<String, Object> data = new HashMap<String, Object>();
		Property unique = entity.findUniqueColumnProperty();
		boolean number = TypeUtil.isNumber(unique.getType());
		String sql = SqlBuilder.createSelectEntitySql(entity,unique,id,number);
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = con.prepareStatement(sql);
			resultset = ps.executeQuery();
			if(resultset.next()){
				List<Property> all = entity.getProperties();
				for (Property property : all) {
					AbstractType type = TypeUtil.getDataType(property.getType());
					data.put(property.getName(),type.get(resultset,property.getColumn()));
				}
			}
		} catch (SQLException e) {
			log.error("getEntity",e);
			throw new PersistenceException("",e.getMessage());
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return data;
	}
}
