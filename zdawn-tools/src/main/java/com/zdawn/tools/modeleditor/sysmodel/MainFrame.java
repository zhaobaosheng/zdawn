package com.zdawn.tools.modeleditor.sysmodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import com.zdawn.tools.modeleditor.UiLifeCycle;

public class MainFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 6640792272661945194L;
	
	private JPanel mainPanel = null;
	private JLabel statusBar= null;
	private HashMap<String,String[]> itemMap = new HashMap<String, String[]>();
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MainFrame() {
		setTitle("数据模型编辑工具");
//		setIconImage(new ImageIcon(getClass().getResource("XXX.jpg")).getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(1000, 700);
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeConfirm();
			}
		});

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT,5,0);
		JPanel statusPanel = new JPanel(layout);
		statusPanel.setBorder(new EtchedBorder());
		statusPanel.add(new JLabel("状态："));
		statusBar = new JLabel("就绪");
		statusPanel.add(statusBar);
		getContentPane().add(statusPanel,BorderLayout.SOUTH);
		initMenu();
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	public void setStatusTip(String tip){
		this.statusBar.setText(tip);
	}
	private ArrayList<String[]> getMenu() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		//id-pid-name-level-uiType(menu-panel-dialog-exit)-className
		String[] temp = new String[]{"10","0","模型维护","1","menu",""};
		list.add(temp);
		temp = new String[]{"11","10","新建模型","2","create.sysmodel.panel","com.zdawn.tools.modeleditor.sysmodel.ui.MainPanel"};
		list.add(temp);
		temp = new String[]{"12","10","打开模型","2","open.sysmodel.panel","com.zdawn.tools.modeleditor.sysmodel.ui.MainPanel"};
		list.add(temp);
		temp = new String[]{"13","10","保存模型","2","panel",""};
		list.add(temp);
		temp = new String[]{"14","10","另存模型","2","panel",""};
		list.add(temp);
		temp = new String[]{"15","10","退出","2","exit",""};
		list.add(temp);
		
		temp = new String[]{"90","0","帮助","1","menu",""};
		list.add(temp);
		temp = new String[]{"92","90","关于","2","about_dialog","about"};
		list.add(temp);
		return list;
	}
	private void initMenu(){
		JMenuBar m_Main = new JMenuBar();
		Map<String,JMenu> menuMap = new HashMap<String, JMenu>(5);
		ArrayList<String[]> menuList = getMenu();
		for (int i = 0; i < menuList.size(); i++) {
			String[]  item = menuList.get(i);
			if(item[3].equals("1")){//menu
				JMenu menu = new JMenu(item[2]);
				m_Main.add(menu);
				menuMap.put(item[0], menu);
			}else{//item
				JMenuItem menuItem = new JMenuItem(item[2]);
				menuItem.setActionCommand(item[0]);
				menuItem.addActionListener(this);
				JMenu parentMenu = menuMap.get(item[1]); 
				parentMenu.add(menuItem);
				itemMap.put(item[0], item);
			}
		}
		this.setJMenuBar(m_Main);
	}
	private void closeConfirm(){
		String tip = "确定退出? ";
		int option = JOptionPane.showConfirmDialog(this, tip, "提示 ",JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION){
			System.exit(0);
		}else{
			return;
		}
	}
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		String[] item = itemMap.get(cmd);
		if(item[4].endsWith("panel")){
			setStatusTip(item[2]);
			Component[] array = mainPanel.getComponents();
			if(array!=null && array.length>0){
				if(array[0] instanceof UiLifeCycle){
					UiLifeCycle uiLifeCycle = (UiLifeCycle)array[0];
					uiLifeCycle.destroyUI();
				}
				mainPanel.removeAll();
			}
			try {
				Class<?> funcPanel = this.getClass().getClassLoader().loadClass(item[5]);
				JPanel panel = (JPanel)funcPanel.newInstance();
				if(panel instanceof UiLifeCycle){
					UiLifeCycle uiLifeCycle = (UiLifeCycle)panel;
					Map<String,String> para = new HashMap<String, String>();
					para.put("uiType", item[4]);
					uiLifeCycle.initUI(para);
				}
				mainPanel.add(panel,BorderLayout.CENTER);
				mainPanel.updateUI();
			} catch (Exception e) {}
		}else if(item[4].equals("about_dialog")){
			StringBuffer sb = new StringBuffer();
			sb.append("数据模型编辑工具 版本v1.0\n");
			sb.append("版权所有 zdawn.com");
			JOptionPane.showConfirmDialog(this,sb.toString(), "关于",
					JOptionPane.CLOSED_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
		}else if(item[4].equals("exit")){
			closeConfirm();
		}
	}
}
