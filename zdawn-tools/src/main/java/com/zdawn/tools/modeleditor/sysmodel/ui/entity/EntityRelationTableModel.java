package com.zdawn.tools.modeleditor.sysmodel.ui.entity;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Relation;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;

public class EntityRelationTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7085449857370186994L;

	private SysModelImpl sysModel = null;
	
	private String tableName = null;

	private String[] columnNames = new String[]{"本实体表名","本实体字段名","关联实体表名","关联实体字段名","关系类型"};
	
	private List<Relation> listRelations = null;
	
	public EntityRelationTableModel(SysModelImpl sysModel,String tableName){
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
		if(listRelations==null) initData();
		return listRelations==null ? 0:listRelations.size();
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(listRelations==null) initData();
		if(listRelations==null) return "";
		if(rowIndex>=listRelations.size()) return "";
		Relation relation = listRelations.get(rowIndex);
		if(relation==null)	return "";
		if(columnIndex>=this.getColumnCount()) return "";
		//处理返回值
		Object value = null;
		switch (columnIndex) {
		case 0: //本实体表名
			value = tableName;
			break;
		case 1: //本实体字段名
			value = relation.getSelfColumn();
			break;
		case 2: //关联实体表名
			value = relation.getTableName();
			break;
		case 3: //关联实体字段名
			value = relation.getColumn();
			break;
		case 4: //关系类型
			value = relation.getType();
			break;
		default:
			break;
		}
		return value;
	}
	private void initData(){
		Entity entity = sysModel.findEntityByTableName(tableName);
		listRelations = entity.getRelations();
	}

	public void filterData(){
		fireTableDataChanged();
	}
}
