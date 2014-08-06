package com.zdawn.tools.modeleditor.sysmodel.ui.entitylist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.tools.modeleditor.KeyValue;

public class EntityListPanel extends JPanel {
	private static final long serialVersionUID = 8217031242854865317L;

	private JTextField filterConditionText = null;
	
	private JComboBox filterTypeComBo = null;
	
	private EntityListTableModel  tableModel = null;
	
	public EntityListPanel(SysModelImpl sysModel,String type){
		setBorder(new EmptyBorder(0,5,0,5));
		setLayout(new BorderLayout(10,10));
		FlowLayout layoutTitle = new FlowLayout(FlowLayout.CENTER,0,10);
		JPanel titlePanel = new JPanel(layoutTitle);
		titlePanel.add(new JLabel("实体管理"));
		//filter panel
		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout());
		//title
		FlowLayout layoutQueryTitle = new FlowLayout(FlowLayout. LEFT,10,0);
		JPanel queryTitlePanel = new JPanel(layoutQueryTitle);
		queryTitlePanel.add(new JLabel("过滤条件"));
		queryPanel.add(queryTitlePanel, BorderLayout.NORTH);
		//condition panel
		FlowLayout layoutRow = new FlowLayout(FlowLayout.CENTER,10,0);
		JPanel conditionPanel = new JPanel(layoutRow);
		JPanel typePanel = new JPanel();
		typePanel.add(new JLabel("实体类型"));
		Vector<KeyValue> typeData = initTypeData(sysModel);
		filterTypeComBo  = new JComboBox(new DefaultComboBoxModel(typeData));
		typePanel.add(filterTypeComBo);
		conditionPanel.add(typePanel);
		//filter name 
		JPanel filterNamePanel = new JPanel();
		filterNamePanel.add(new JLabel("过滤名称(描述-实体名称-表名)"));
		filterConditionText = new JTextField(20);
		filterNamePanel.add(filterConditionText);
		conditionPanel.add(filterNamePanel);
		//button
		JPanel buttonPanel = new JPanel();
		JButton filterButton = new JButton("过滤");
		filterButton.setActionCommand("filter");
		filterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterData();
			}
		});
		buttonPanel.add(filterButton);
		conditionPanel.add(buttonPanel);
		
		queryPanel.add(conditionPanel, BorderLayout.CENTER);
		
		//entity list table
		JScrollPane scrollPane=new JScrollPane();
		tableModel = new EntityListTableModel(sysModel, type);
		JTable table = new JTable(tableModel);
		scrollPane.getViewport().add(table);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(queryPanel,BorderLayout.NORTH);
		contentPanel.add(scrollPane,BorderLayout.CENTER);
		add(titlePanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
		//init ui display
		filterTypeComBo.setSelectedItem(new KeyValue(type,""));
	}

	protected void filterData() {
		KeyValue keyValue = (KeyValue)filterTypeComBo.getSelectedItem();
		String type = keyValue.getKey();
		String condition = filterConditionText.getText();
		tableModel.filterData(type, condition);
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
}
