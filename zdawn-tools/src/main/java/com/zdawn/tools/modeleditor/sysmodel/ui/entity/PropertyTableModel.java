package com.zdawn.tools.modeleditor.sysmodel.ui.entity;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.Reference;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;

public class PropertyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 6585397820721273732L;

	private SysModelImpl sysModel = null;
	
	private String tableName = null;

	private String[] columnNames = new String[]{"中文描述","属性名","字段名","启用","字段类型","长度","精度","空值","缺省值","转换字符格式","引用描述"};
	
	private List<Property> listProperties = null;
	
	public PropertyTableModel(SysModelImpl sysModel,String tableName){
		this.sysModel = sysModel;
		this.tableName = tableName;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column >= columnNames.length) return "";
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		if(listProperties==null) initData();
		return listProperties==null ? 0:listProperties.size();
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(listProperties==null) initData();
		if(listProperties==null) return "";
		if(rowIndex>=listProperties.size()) return "";
		Property property = listProperties.get(rowIndex);
		if(property==null)	return "";
		if(columnIndex>=this.getColumnCount()) return "";
		//处理返回值
		Object value = null;
		switch (columnIndex) {
		case 0: //中文描述
			value = property.getDescription();
			break;
		case 1: //属性名
			value = property.getName();
			break;
		case 2: //字段名
			value = property.getColumn();
			break;
		case 3: //启用
			value = property.isUsing();
			break;
		case 4: //字段类型
			value = property.getType();
			break;
		case 5: //长度
			value = property.getLength();
			break;
		case 6: //精度
			value = property.getScale();
			break;
		case 7: //空值
			value = property.isNotNull();
			break;
		case 8: //缺省值
			value = property.getDefaultValue();
			break;
		case 9: //转换字符格式
			value = property.getToStringformat();
			break;
		case 10: //引用描述
			Reference reference=property.getReference();
			if(reference!=null){
				value = reference.getTableName()==null ? "":reference.getTableName();
				value = value + reference.getColumn()==null ? "":"."+reference.getColumn();		
			}
			break;
		default:
			break;
		}
		return value;
	}
	private void initData(){
		Entity entity = sysModel.findEntityByTableName(tableName);
		listProperties = entity.getProperties();
	}

	public void filterData(String type,String condition){
		fireTableDataChanged();
	}
}
