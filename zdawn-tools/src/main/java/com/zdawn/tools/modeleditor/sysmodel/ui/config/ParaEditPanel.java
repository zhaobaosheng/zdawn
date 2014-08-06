package com.zdawn.tools.modeleditor.sysmodel.ui.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.tools.modeleditor.UiEventListener;

public class ParaEditPanel extends JPanel {
	private static final long serialVersionUID = -4371361427836598335L;
	
	private ArrayList<UiEventListener> eventListenerList = new ArrayList<UiEventListener>();
	
	private JTextField keyText = null;
	
	private JTextField valueText =null;
	
	private SysModelImpl sysModel = null;
	
	private boolean edit = false;
	
	public ParaEditPanel(String title,SysModelImpl sysModel){
		this.sysModel = sysModel;
		setBorder(new EmptyBorder(0,5,0,5));
		setLayout(new BorderLayout(10,10));
		FlowLayout layoutTitle = new FlowLayout(FlowLayout.CENTER,0,10);
		JPanel titlePanel = new JPanel(layoutTitle);
		titlePanel.add(new JLabel(title));
		//edit panel
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		FlowLayout layoutRow = new FlowLayout(FlowLayout.CENTER,10,0);
		JPanel keyPanel = new JPanel(layoutRow);
		keyPanel.add(new JLabel("参数key"));
		keyText = new JTextField(40);
		keyPanel.add(keyText);
		editPanel.add(keyPanel);
		
		JPanel valuePanel = new JPanel(layoutRow);
		valuePanel.add(new JLabel("参数值"));
		valueText = new JTextField(40);
		valuePanel.add(valueText);
		editPanel.add(valuePanel);
		
		//buttons
		FlowLayout layoutButton = new FlowLayout(FlowLayout.CENTER,30,0);
		JPanel buttonPanel = new JPanel(layoutButton);
		JButton  saveButton = new JButton("保存");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveParaConfig();
			}
		});
		buttonPanel.add(saveButton);
		JButton  cancelButton = new JButton(" 取消");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireUiEvent("CLOSE");
			}
		});
		buttonPanel.add(cancelButton);
		editPanel.add(buttonPanel);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(editPanel,BorderLayout.CENTER);
		
		add(titlePanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}
	public void initEditData(String key,String value){
		edit = true;
		if(key==null){
			JOptionPane.showConfirmDialog(this,"参数key不能为空", "提示信息",
					JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			edit = false;
			return ;
		}
		keyText.setText(key);
		valueText.setText(value==null ? "":value);
	}
	public void registerUiEventListener(UiEventListener eventListener){
		this.eventListenerList.add(eventListener);
	}
	private void fireUiEvent(String eventID){
		for (UiEventListener temp : eventListenerList) {
			temp.handleEvent(eventID, null);
		}
	}
	private void saveParaConfig(){
		String key = keyText.getText().trim();
		String value = valueText.getText().trim();
		//key can not appear chinese char
		if(key.equals("")) {
			JOptionPane.showConfirmDialog(this,"参数key必填", "提示信息",
					JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			return ;
		}
		Pattern pattern  = Pattern.compile("[\\u4e00-\\u9fa5]");
		Matcher matcher = pattern.matcher(key);
		if(matcher.find()){
			JOptionPane.showConfirmDialog(this,"参数key不能含有中文字符", "提示信息",
					JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			return ;
		}
		if(!edit && sysModel.getConfig().containsKey(key)){
			JOptionPane.showConfirmDialog(this,"参数key已经存在", "提示信息",
					JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			return ;
		}
		sysModel.getConfig().put(key, value);
		fireUiEvent("CLOSE");
	}
}
