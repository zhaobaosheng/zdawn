package com.zdawn.tools.modeleditor.sysmodel.ui.entity;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.tools.modeleditor.KeyValue;
import com.zdawn.tools.modeleditor.UiLifeCycle;

public class EntityPanel extends JPanel implements UiLifeCycle {
	private static final long serialVersionUID = -8892666146610499213L;
	//master panel
	private JTabbedPane tabbedPane = new JTabbedPane();
	/*******entity begin**********/
	private JTextField entityName = null;
	private JTextField tableName = null;
	private JTextField desc = null;
	private JComboBox<KeyValue> typeComBo = null;
	private JTextField clazz = null;
	/********entity end*****/
	/********property begin*****/
	private PropertyTableModel propertyTableModel;
	private JTable propertyTable;
	/********property end*****/
	/********relation begin*****/
	private EntityRelationTableModel relationTableModel;
	private JTable relationTable;
	/********relation end*****/
	private SysModelImpl sysModel;
	
	public EntityPanel(SysModelImpl sysModel){
		this.sysModel = sysModel;
	}
	private JPanel createEntityPanel(SysModelImpl sysModel){
		JPanel entityPanel = new JPanel(new BorderLayout());
		entityPanel.setBorder(new EmptyBorder(0,5,0,5));
		entityPanel.setLayout(new BorderLayout(10,10));
		FlowLayout layoutTitle = new FlowLayout(FlowLayout.CENTER,0,10);
		//实体基本信息标题
		JPanel titlePanel = new JPanel(layoutTitle);
		titlePanel.add(new JLabel("实体基本信息"));
		//edit panel
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.PAGE_AXIS));
		FlowLayout layoutRow = new FlowLayout(FlowLayout.LEFT,10,0);
		//实体名称
		JPanel namePanel = new JPanel(layoutRow);
		namePanel.add(new JLabel("实体名称"));
		entityName = new JTextField(40);
		namePanel.add(entityName);
		editPanel.add(namePanel);
		//表名
		JPanel tableNamePanel = new JPanel(layoutRow);
		tableNamePanel.add(new JLabel("表    名"));
		tableName = new JTextField(40);
		tableNamePanel.add(tableName);
		editPanel.add(tableNamePanel);
		//中文描述
		JPanel descPanel = new JPanel(layoutRow);
		descPanel.add(new JLabel("中文描述"));
		desc = new JTextField(100);
		descPanel.add(desc);
		editPanel.add(descPanel);
		//实体分类
		JPanel typePanel = new JPanel(layoutRow);
		typePanel.add(new JLabel("分    类"));
		Vector<KeyValue> typeData = initTypeData(sysModel);
		typeComBo  = new JComboBox<KeyValue>(new DefaultComboBoxModel<KeyValue>(typeData));
		typeComBo.setEditable(true);
		typePanel.add(typeComBo);
		editPanel.add(typePanel);
		//实体类名
		JPanel clazzPanel = new JPanel(layoutRow);
		clazzPanel.add(new JLabel("实体类名"));
		clazz = new JTextField(100);
		clazzPanel.add(clazz);
		editPanel.add(clazzPanel);
		//buttons
		FlowLayout layoutButton = new FlowLayout(FlowLayout.CENTER,30,0);
		JPanel buttonPanel = new JPanel(layoutButton);
		JButton  saveButton = new JButton("保存");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveEntity();
			}
		});
		buttonPanel.add(saveButton);
		editPanel.add(buttonPanel);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(editPanel,BorderLayout.CENTER);
		
		entityPanel.add(titlePanel, BorderLayout.NORTH);
		entityPanel.add(contentPanel, BorderLayout.CENTER);
		return entityPanel;
	}
	//init entity data
	private void initEntityData(SysModelImpl sysModel, String tName){
		Entity entity = sysModel.findEntityByTableName(tName);
		if(entity==null) return ;
		this.entityName.setText(entity.getName());
		this.tableName.setText(entity.getTableName());
		this.desc.setText(entity.getDescription());
		this.clazz.setText(entity.getClazz());
		ComboBoxModel<KeyValue> typeModel =  typeComBo.getModel();
		int size = typeModel.getSize();
		for (int i = 0; i < size; i++) {
			KeyValue kv = typeModel.getElementAt(i);
			if(kv.getKey().equals(entity.getType())){
				this.typeComBo.setSelectedIndex(i);
				break;
			}
		}
	};
	private void saveEntity(){
		JOptionPane.showConfirmDialog(this,"Save Entity", "提示信息",
				JOptionPane.CLOSED_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
	}
	private Vector<KeyValue> initTypeData(SysModelImpl sysModel) {
		Vector<KeyValue> typeData = new Vector<KeyValue>();
		typeData.add(new KeyValue("","请选择"));
		Map<String,Entity> entityMap = sysModel.getEntities();
		List<String> typeList = new ArrayList<String>();
		for (Map.Entry<String,Entity> entry  : entityMap.entrySet()) {
			if(!typeList.contains(entry.getValue().getType())) typeList.add(entry.getValue().getType());
		}
		//sort
		String[] typeArray = typeList.toArray(new String[0]);
		Arrays.sort(typeArray);
		for (int i = 0; i < typeArray.length; i++) {
			String value = sysModel.getConfig().get(typeArray[i]);
			if(value==null) value = typeArray[i];
			typeData.add(new KeyValue(typeArray[i],value));
		}
		return typeData;
	}
	
	private JPanel createPropertyPanel(SysModelImpl sysModel,String tableName){
		JPanel propertyPanel = new JPanel(new BorderLayout());
		propertyPanel.setBorder(new EmptyBorder(0,5,0,5));
		propertyPanel.setLayout(new BorderLayout(10,10));
		//entity list table
		JScrollPane scrollPane=new JScrollPane();
		propertyTableModel = new PropertyTableModel(sysModel,tableName);
		propertyTable = new JTable(propertyTableModel);
		propertyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		propertyTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) handlePropertyOperation("modify");
			}
		});
		scrollPane.getViewport().add(propertyTable);
		//buttons
		PropertyOperationListener propertyOperation = this.new PropertyOperationListener();
		FlowLayout layoutButton = new FlowLayout(FlowLayout.CENTER,30,10);
		JPanel buttonPanel = new JPanel(layoutButton);
		JButton  addButton = new JButton("新增");
		addButton.setActionCommand("create");
		addButton.addActionListener(propertyOperation);
		buttonPanel.add(addButton);
		JButton  modifyButton = new JButton("修改");
		modifyButton.setActionCommand("modify");
		modifyButton.addActionListener(propertyOperation);
		buttonPanel.add(modifyButton);
		JButton  delButton = new JButton("删除");
		delButton.setActionCommand("delete");
		delButton.addActionListener(propertyOperation);
		buttonPanel.add(delButton);
		JButton  refreshButton = new JButton("刷新");
		refreshButton.setActionCommand("refresh");
		refreshButton.addActionListener(propertyOperation);
		buttonPanel.add(refreshButton);
		
		propertyPanel.add(scrollPane,BorderLayout.CENTER);
		propertyPanel.add(buttonPanel,BorderLayout.SOUTH);
		return propertyPanel;
	}
	//create entity relation
	private JPanel createRelationPanel(SysModelImpl sysModel,String tableName){
		JPanel relationPanel = new JPanel(new BorderLayout());
		relationPanel.setBorder(new EmptyBorder(0,5,0,5));
		relationPanel.setLayout(new BorderLayout(10,10));
		//relation list table
		JScrollPane scrollPane=new JScrollPane();
		relationTableModel = new EntityRelationTableModel(sysModel,tableName);
		relationTable = new JTable(relationTableModel);
		relationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		relationTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) handleRelationOperation("modify");
			}
		});
		scrollPane.getViewport().add(relationTable);
		//buttons
		RelationOperationListener relationOperation = this.new RelationOperationListener();
		FlowLayout layoutButton = new FlowLayout(FlowLayout.CENTER,30,10);
		JPanel buttonPanel = new JPanel(layoutButton);
		JButton  addButton = new JButton("新增");
		addButton.setActionCommand("create");
		addButton.addActionListener(relationOperation);
		buttonPanel.add(addButton);
		JButton  modifyButton = new JButton("修改");
		modifyButton.setActionCommand("modify");
		modifyButton.addActionListener(relationOperation);
		buttonPanel.add(modifyButton);
		JButton  delButton = new JButton("删除");
		delButton.setActionCommand("delete");
		delButton.addActionListener(relationOperation);
		buttonPanel.add(delButton);
		
		relationPanel.add(scrollPane,BorderLayout.CENTER);
		relationPanel.add(buttonPanel,BorderLayout.SOUTH);
		return relationPanel;
	}
	
	@Override
	public void initUI(Map<String, String> para) {
		String tableName = para.get("tableName");
		tableName = tableName==null ? "":tableName;
		//entity info
		JPanel entityPanel = createEntityPanel(sysModel);
		tabbedPane.addTab("实体",entityPanel);
		initEntityData(sysModel, tableName);
		//property info
		JPanel propertyPanel = createPropertyPanel(sysModel, tableName);
		tabbedPane.addTab("属性",propertyPanel);
		//realtion info
		JPanel relationPanel = createRelationPanel(sysModel, tableName);
		tabbedPane.addTab("关系",relationPanel);
		//add self
		setLayout(new BorderLayout());
		add(tabbedPane,BorderLayout.CENTER);
	}

	@Override
	public void destroyUI() {
	}
	
	private void handlePropertyOperation(String action){
		
	}
	private void handleRelationOperation(String string) {
		// TODO Auto-generated method stub
		
	}
	class PropertyOperationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if(action==null) return ;
			handlePropertyOperation(action);
		}
	}
	class RelationOperationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if(action==null) return ;
			handleRelationOperation(action);
		}
	}
}
