package com.zdawn.tools.modeleditor.sysmodel.ui.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.tools.modeleditor.CommonDialog;

public class ConfigPanel extends JPanel {
	private static final long serialVersionUID = 4327665231299619152L;
	
	private ConfigTableModel  tableModel = null;
	
	private JTable table = null;
	
	public ConfigPanel(SysModelImpl sysModel){
		setBorder(new EmptyBorder(0,5,0,5));
		setLayout(new BorderLayout(10,10));
		FlowLayout layoutTitle = new FlowLayout(FlowLayout.CENTER,0,10);
		JPanel titlePanel = new JPanel(layoutTitle);
		titlePanel.add(new JLabel("配置参数管理"));
		//filter panel
		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout());
		//entity list table
		JScrollPane scrollPane=new JScrollPane();
		tableModel = new ConfigTableModel(sysModel);
		table = new JTable(tableModel);
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) handleParaOperation("modify");
			}
		});
		scrollPane.getViewport().add(table);
		//buttons
		ParaOperationListener paraOperation = this.new ParaOperationListener();
		FlowLayout layoutButton = new FlowLayout(FlowLayout.CENTER,30,10);
		JPanel buttonPanel = new JPanel(layoutButton);
		JButton  addButton = new JButton("新增");
		addButton.setActionCommand("create");
		addButton.addActionListener(paraOperation);
		buttonPanel.add(addButton);
		JButton  modifyButton = new JButton("修改");
		modifyButton.setActionCommand("modify");
		modifyButton.addActionListener(paraOperation);
		buttonPanel.add(modifyButton);
		JButton  delButton = new JButton("删除");
		delButton.setActionCommand("delete");
		delButton.addActionListener(paraOperation);
		buttonPanel.add(delButton);
		JButton  refreshButton = new JButton("刷新");
		refreshButton.setActionCommand("refresh");
		refreshButton.addActionListener(paraOperation);
		buttonPanel.add(refreshButton);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(queryPanel,BorderLayout.NORTH);
		contentPanel.add(scrollPane,BorderLayout.CENTER);
		contentPanel.add(buttonPanel,BorderLayout.SOUTH);
		add(titlePanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}
	protected void filterData(String condition) {
		tableModel.filterData(condition);
	}
	private void handleParaOperation(String action){
		if(action.equals("create")){
			ParaEditPanel panel = new ParaEditPanel("新增配置参数",tableModel.getSysModel());
			CommonDialog dialog = new CommonDialog(this,"新增配置参数");
			panel.registerUiEventListener(dialog);
			dialog.addContentPanel(panel);
			dialog.setSize(400,250);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
			//关闭模式对话框刷新数据
			filterData(null);
		}else if(action.equals("modify")){
			ParaEditPanel panel = new ParaEditPanel("修改配置参数",tableModel.getSysModel());
			int count = table.getSelectedColumnCount();
			if(count>1 || count==0) {
				JOptionPane.showConfirmDialog(this,"请选择一行编辑", "提示信息",
						JOptionPane.CLOSED_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				return ;
			}
			int rowIndex = table.getSelectedColumn();
			Object key = tableModel.getValueAt(rowIndex,0);
			Object value = tableModel.getValueAt(rowIndex,1);
			panel.initEditData(key==null ? null:key.toString(), value==null ? null:value.toString());
			CommonDialog dialog = new CommonDialog(this,"修改配置参数");
			panel.registerUiEventListener(dialog);
			dialog.addContentPanel(panel);
			dialog.setSize(400,250);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
			//关闭模式对话框刷新数据
			filterData(null);
		}else if(action.equals("delete")){
			int[] row = table.getSelectedRows();
			if(row.length==0){
				JOptionPane.showConfirmDialog(this,"请选择要删除行", "提示信息",
						JOptionPane.CLOSED_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				return ;
			}
			int option = JOptionPane.showConfirmDialog(this,"是否删除", "提示 ",JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION){
				for (int i = 0; i < row.length; i++) {
					Object key = tableModel.getValueAt(row[i],0);
					tableModel.getSysModel().getConfig().remove(key.toString());
				}
				filterData(null);
			}
		}else if(action.equals("refresh")){
			filterData(null);
		}
	}
	class ParaOperationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if(action==null) return ;
			handleParaOperation(action);
		}
	}
}
