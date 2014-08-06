package com.zdawn.tools.modeleditor.sysmodel.ui.entitylist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;

public class EntityListTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7959822379400118713L;
	
	private SysModelImpl sysModel = null;
	
	private String type = null;

	private String[] columnNames = new String[]{"中文描述","实体名称","表名","实体类型","主键字段","实体对应java类全名"};
	
	private String[] entityNameArray = null;
	
	public EntityListTableModel(SysModelImpl sysModel,String type){
		this.sysModel = sysModel;
		this.type = type;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column >= columnNames.length) return "";
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		if(entityNameArray==null) initData(null);
		return entityNameArray.length;
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(entityNameArray==null) initData(null);
		if(rowIndex>=entityNameArray.length) return "";
		Entity entity = sysModel.findEntityByName(entityNameArray[rowIndex]);
		if(entity==null){
			System.out.println("entity not found entityName="+entityNameArray[rowIndex]);
			return "";
		}
		if(columnIndex>=this.getColumnCount()) return "";
		Object value = null;
		switch (columnIndex) {
		case 0: //中文描述
			value = entity.getDescription();
			break;
		case 1: //实体名称
			value = entity.getName();
			break;
		case 2: //表名
			value = entity.getTableName();
			break;
		case 3: //实体类型
			String type = entity.getType();
			value = sysModel.getConfig().get(type);
			if(value==null) value = type;
			break;
		case 4: //主键字段
			value = entity.getUniqueColumn();
			break;
		case 5: //实体对应java类全名
			value = entity.getClazz();
			break;
		default:
			break;
		}
		return value;
	}
	private void initData(String condition){
		List<String> entityNameList = new ArrayList<String>();
		if(type==null || type.equals("")){
			for (Map.Entry<String,Entity> entry  : sysModel.getEntities().entrySet()) {
				if(include(condition,entry.getValue())) entityNameList.add(entry.getKey());
			}
		}else{
			for (Map.Entry<String,Entity> entry  : sysModel.getEntities().entrySet()) {
				if(entry.getValue().getType().equals(type) && include(condition,entry.getValue())){
					entityNameList.add(entry.getKey());
				}
			}
		}
		entityNameArray = entityNameList.toArray(new String[0]);
		Arrays.sort(entityNameArray);
	}
	private boolean include(String condition,Entity entity){
		//无条件
		if(condition==null) return true;
		if(entity.getDescription().indexOf(condition)!=-1) return true;
		if(entity.getName().indexOf(condition)!=-1) return true;
		if(entity.getTableName().indexOf(condition)!=-1) return true;
		return false;
	}
	public void filterData(String type,String condition){
		this.type = type;
		if(condition!=null && condition.equals("")) condition = null;
		initData(condition);
		fireTableDataChanged();
	}
}
