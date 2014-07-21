package com.zdawn.commons.dict;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.dict.model.DataField;
import com.zdawn.commons.dict.model.DictData;
import com.zdawn.commons.dict.model.MetaDict;
import com.zdawn.commons.dict.model.MetaDictSet;
import com.zdawn.commons.dict.model.RecordData;
import com.zdawn.commons.jdbc.JdbcUtils;

public class DataDictionaryImpl implements DataDictionary {
	private  Logger log = LoggerFactory.getLogger(DataDictionaryImpl.class);
	private DataSource dataSource = null;
	private String metaFileName = "DataDic.xml";
	private MetaDictSet metaDictSet = null;
	
	public DataDictionaryImpl(){
		initMetaDictSet();
	}
	@Override
	public DictData getDictData(String dictName) {
		return getDictData(dictName,null);
	}
	
	@Override
	public MetaDict getMetaDict(String dictName) {
		return metaDictSet.getMetaDict(dictName);
	}

	@Override
	public DictData getDictData(String dictName, String condition) {
		MetaDict metaDict = metaDictSet.getMetaDict(dictName);
		return readFromDB(metaDict,condition);
	}

	private void initMetaDictSet() {
		if(metaDictSet==null){
			MetaDictSetFactory.loadClassPathDataDictionary(metaFileName);
			metaDictSet = MetaDictSetFactory.getMetaDictSet();
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void setMetaFileName(String metaFileName) {
		this.metaFileName = metaFileName;
	}
	private DictData readFromDB(MetaDict metaDict,String condition){
		 DictData dictData = new DictData();
		 dictData.setMetaDict(metaDict);
		 String sql = createDictSql(metaDict,condition);
		 Connection con = null;
		 Statement st = null;
		 ResultSet rs = null;
		 try {
			log.debug(sql);
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				dictData.addRecordData(readRecordData(rs,metaDict));
			}
		} catch (Exception e) {
			log.error("readFromDB",e);
		}finally{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(st);
			JdbcUtils.closeConnection(con);
		}
		return dictData;
	}
	private RecordData readRecordData(ResultSet rs,MetaDict metaDict) throws Exception {
		RecordData record = new RecordData();
		List<DataField> listDataFields = metaDict.getListDataFields();
		for (int i = 0; i < listDataFields.size(); i++) {
			DataField dataField = listDataFields.get(i);
			if(dataField.getDataType().equals("date")){
				record.putKeyValue(dataField.getFieldName(),rs.getTimestamp(dataField.getFieldName()));
			}else if(dataField.getDataType().equals("number")){
				record.putKeyValue(dataField.getFieldName(),rs.getLong(dataField.getFieldName()));
			}else{
				record.putKeyValue(dataField.getFieldName(),rs.getString(dataField.getFieldName()));
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
			if(i==0){
				sb.append(dataField.getFieldName());
			}else{
				sb.append(',').append(dataField.getFieldName());
			}
		}
		String tableName = metaDict.getTableName().length() == 0 ? metaDict
				.getDicName() : metaDict.getTableName();
		sb.append(" from ").append(tableName);
		String sqlCondition = metaDict.getCondition();
		if(condition!=null && condition.length()>0){
			sqlCondition = sqlCondition.length()==0 ? condition:sqlCondition + " and "+condition;
		}
		if(sqlCondition.length()>0) sb.append(" where ").append(sqlCondition);
		if(metaDict.getOrderField().length()>0) sb.append(" order by "+metaDict.getOrderField());
		return sb.toString();
	}
	
}
