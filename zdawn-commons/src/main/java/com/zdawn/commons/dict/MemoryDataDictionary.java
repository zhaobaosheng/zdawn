package com.zdawn.commons.dict;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.zdawn.commons.dict.model.DataField;
import com.zdawn.commons.dict.model.DictData;
import com.zdawn.commons.dict.model.MetaDict;
import com.zdawn.commons.dict.model.MetaDictSet;
import com.zdawn.commons.dict.model.RecordData;
import com.zdawn.commons.jdbc.JdbcUtils;

/**
 * 将数据字典数据缓存在内存中 <br>
 * 根据com.sinosoft.ie.dhow.core.general.dict.model.MetaDict.cacheType属性判断是否缓存
 * 
 * @author zhaobs
 */
public class MemoryDataDictionary implements DataDictionary,InitializingBean {
	private Logger log = LoggerFactory.getLogger(MemoryDataDictionary.class);
	// 缓存
	private Map<String, DictData> ddCache = new HashMap<String, DictData>();
	/**
	 * 编码所在数据源
	 */
	private DataSource dataSource = null;
	/**
	 * 缓存元数据文件
	 */
	private String metaFileName = "DataDic.xml";
	/**
	 * 元数据对象
	 */
	private MetaDictSet metaDictSet = null;

	private static final String MEMORY = "memory";

	public void initDicData() {
		// 装载元数据
		initMetaDictSet();
		// 装载编码数据
		loadDictDataFromDB();
	}

	@Override
	public MetaDict getMetaDict(String dictName) {
		return metaDictSet.getMetaDict(dictName);
	}

	@Override
	public DictData getDictData(String dictName) {
		MetaDict dict = getMetaDict(dictName);
		if (!dict.getCacheType().equalsIgnoreCase(MEMORY)) {
			log.warn(dictName+ " cacheType attribute is not memory,so return null");
			return null;
		}
		// 从缓存中读
		DictData dictData = ddCache.get(dictName);
		if (dictData != null)
			return dictData;
		// 不存在,load from rdbms
		dictData = readFromDB(dict, null);
		// 放入缓存
		ddCache.put(dictName, dictData);
		return dictData;
	}

	@Override
	public DictData getDictData(String dictName, String condition) {
		MetaDict dict = getMetaDict(dictName);
		if (!dict.getCacheType().equalsIgnoreCase(MEMORY)) {
			return readFromDB(dict, condition);
		} else {
			log.warn("now cache in memory not implement");
		}
		return null;
	}

	private void initMetaDictSet() {
		if (metaDictSet == null) {
			MetaDictSetFactory.loadClassPathDataDictionary(metaFileName);
			metaDictSet = MetaDictSetFactory.getMetaDictSet();
		}
	}

	private void loadDictDataFromDB() {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			List<MetaDict> list = metaDictSet.getListMetaDicts();
			for (MetaDict metaDict : list) {
				if (!metaDict.getCacheType().equalsIgnoreCase(MEMORY))
					continue;
				log.info("begin load " + metaDict.getDicName());
				DictData dict = loadData(con, metaDict, null);
				log.info("load " + metaDict.getDicName() + " success!");
				if (dict != null)
					ddCache.put(metaDict.getDicName(), dict);
			}
		} catch (Exception e) {
			log.error("loadDictDataFromDB", e);
		} finally {
			JdbcUtils.closeConnection(con);
		}
	}

	private DictData loadData(Connection con, MetaDict metaDict,
			String condition) {
		Statement st = null;
		ResultSet rs = null;
		DictData dictData = new DictData();
		dictData.setMetaDict(metaDict);
		try {
			String sql = createDictSql(metaDict, condition);
			log.debug(sql);
			st = con.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				dictData.addRecordData(readRecordData(rs, metaDict));
			}
		} catch (Exception e) {
			log.error("load " + metaDict.getDicName() + " fail,reason:"
					+ e.toString());
			dictData = null;
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(st);
		}
		return dictData;
	}

	private DictData readFromDB(MetaDict metaDict, String condition) {
		DictData dictData = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			dictData = loadData(con, metaDict, condition);
		} catch (Exception e) {
			log.error("readFromDB", e);
		} finally {
			JdbcUtils.closeConnection(con);
		}
		return dictData;
	}

	private RecordData readRecordData(ResultSet rs, MetaDict metaDict)
			throws Exception {
		RecordData record = new RecordData();
		List<DataField> listDataFields = metaDict.getListDataFields();
		for (int i = 0; i < listDataFields.size(); i++) {
			DataField dataField = listDataFields.get(i);
			if (dataField.getDataType().equals("date")) {
				record.putKeyValue(dataField.getFieldName(),
						rs.getTimestamp(dataField.getFieldName()));
			} else if (dataField.getDataType().equals("number")) {
				record.putKeyValue(dataField.getFieldName(),
						rs.getLong(dataField.getFieldName()));
			} else {
				record.putKeyValue(dataField.getFieldName(),
						rs.getString(dataField.getFieldName()));
			}
		}
		return record;
	}

	private String createDictSql(MetaDict metaDict, String condition) {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		List<DataField> listDataFields = metaDict.getListDataFields();
		for (int i = 0; i < listDataFields.size(); i++) {
			DataField dataField = listDataFields.get(i);
			if (i == 0) {
				sb.append(dataField.getFieldName());
			} else {
				sb.append(',').append(dataField.getFieldName());
			}
		}
		sb.append(" from ").append(metaDict.getDicName());
		String sqlCondition = metaDict.getCondition();
		if(condition!=null && condition.length()>0){
			sqlCondition = sqlCondition.length()==0 ? condition:sqlCondition + " and "+condition;
		}
		if(sqlCondition.length()>0) sb.append(" where ").append(sqlCondition);
		if (metaDict.getOrderField().length() > 0)
			sb.append(" order by " + metaDict.getOrderField());
		return sb.toString();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setMetaFileName(String metaFileName) {
		this.metaFileName = metaFileName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initDicData();
	}
	
}
