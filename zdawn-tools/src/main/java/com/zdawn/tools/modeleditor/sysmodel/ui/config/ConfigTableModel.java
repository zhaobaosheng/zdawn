package com.zdawn.tools.modeleditor.sysmodel.ui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;

public class ConfigTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -8928019734640185472L;

	private SysModelImpl sysModel = null;
	
	private String[] columnNames = new String[]{"参数key","参数值"};
	
	private String[] keyArray = null;
	
	public ConfigTableModel(SysModelImpl sysModel){
		this.sysModel = sysModel;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column >= columnNames.length) return "";
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		if(keyArray==null) initData(null);
		return keyArray.length;
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(keyArray==null) initData(null);
		if(rowIndex>=keyArray.length) return "";
		if(columnIndex>=this.getColumnCount()) return "";
		Object value = null;
		switch (columnIndex) {
		case 0: //key
			value =  keyArray[rowIndex];
			break;
		case 1: //value
			value =  sysModel.getConfig().get(keyArray[rowIndex]);
			break;
		default:
			break;
		}
		return value;
	}
	private void initData(String condition){
		List<String> keyList = new ArrayList<String>();
		for (Map.Entry<String,String> entry  : sysModel.getConfig().entrySet()) {
			if(include(condition,entry.getKey(),entry.getValue())) keyList.add(entry.getKey());
		}
		keyArray = keyList.toArray(new String[0]);
		Arrays.sort(keyArray);
	}
	private boolean include(String condition, String key, String value) {
		//无条件
		if(condition==null) return true;
		if(key.indexOf(condition)!=-1) return true;
		if(value.indexOf(condition)!=-1) return true;
		return false;
	}
	public void filterData(String condition){
		if(condition!=null && condition.equals("")) condition = null;
		initData(condition);
		fireTableDataChanged();
	}
	public SysModelImpl getSysModel() {
		return sysModel;
	}
}
