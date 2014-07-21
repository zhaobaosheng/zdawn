package com.zdawn.commons.dict.model;

import java.util.ArrayList;
import java.util.List;

public class DictData {
	private MetaDict metaDict = null;
	private List<RecordData> listRecordDatas = new ArrayList<RecordData>();
	private List<DataField> listDispField = null;

	public String getCodeDisplayString(String uniqueCode){
		return getDisplayString(uniqueCode);
	}
	public String getCodeDisplayString(Long uniqueCode){
		return getDisplayString(uniqueCode);
	}
	private String getDisplayString(Object uniqueCode){
		initDisp();
		RecordData data = getRecord(uniqueCode);
		if(data==null) return uniqueCode==null ? "":uniqueCode.toString();
		String disp = "";
		for (int i = 0; i < listDispField.size(); i++) {
			DataField field = listDispField.get(i);
			disp = i==0 ? data.getValue(field.getFieldName())+"":"-"+data.getValue(field.getFieldName());
		}
		return disp;
	}
	public RecordData getRecordData(String keyCode){
		return getRecord(keyCode);
	}
	public RecordData getRecordData(Long keyCode){
		return getRecord(keyCode);
	}
	public RecordData getRecord(Object keyCode){
		if(keyCode==null) return null;
		DataField field = metaDict.getUniqueDataField();
		if(field.getDataType().equals("string")){
			keyCode = keyCode.toString();
		}else if(field.getDataType().equals("number")){
			if(keyCode instanceof String) keyCode = new Long(keyCode.toString());
		}
		for (int i = 0; i < listRecordDatas.size(); i++) {
			RecordData data = listRecordDatas.get(i);
			Object obj = data.getValue(field.getFieldName());
			if(keyCode.equals(obj)) return data;
		}
		return null;
	}
	private void initDisp(){
		 if(listDispField==null){
			 listDispField = new ArrayList<DataField>();
			 List<DataField> listDataFields = metaDict.getListDataFields();
			 for (int i = 0; i < listDataFields.size(); i++) {
				 DataField field = listDataFields.get(i);
				 if(field.isDisplay()) listDispField.add(field);
			}
		 }
	}
	public MetaDict getMetaDict() {
		return metaDict;
	}
	public void setMetaDict(MetaDict metaDict) {
		this.metaDict = metaDict;
	}
	public List<RecordData> getListRecordDatas() {
		return listRecordDatas;
	}
	public void addRecordData(RecordData record){
		listRecordDatas.add(record);
	}
	public void removeAll(){
		listRecordDatas.clear();
	}
	public List<DataField> getListDispField() {
		initDisp();
		return listDispField;
	}
}
