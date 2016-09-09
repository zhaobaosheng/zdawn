package com.zdawn.tools.modeleditor.sysmodel.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.commons.sysmodel.metaservice.SysModelXMLLoader;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.tools.modeleditor.KeyValue;
import com.zdawn.tools.modeleditor.UiLifeCycle;
import com.zdawn.tools.modeleditor.sysmodel.ui.config.ConfigPanel;
import com.zdawn.tools.modeleditor.sysmodel.ui.entity.EntityPanel;
import com.zdawn.tools.modeleditor.sysmodel.ui.entitylist.EntityListPanel;

public class MainPanel extends JPanel implements UiLifeCycle {
	private static final long serialVersionUID = 3480632323748000980L;
	
	private JTree  navigatorTree = null;
	
	private JPanel mainPanel = null;
	
	private JScrollPane leftScrollPane=new JScrollPane();
	
	private SysModelImpl sysModel = null;
	
	private String sysModelPath = null;
	
	public MainPanel(){
		setLayout(new BorderLayout());
		JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
		splitPane.setDividerLocation(200);
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(leftScrollPane);
		mainPanel = new JPanel(new BorderLayout());
		splitPane.setRightComponent(mainPanel);
		add(splitPane, BorderLayout.CENTER);
	}
	
	@Override
	public void initUI(Map<String, String> para) {
		String uiType = para.get("uiType");
		if("create.sysmodel.panel".equals(uiType)){
			sysModel = new SysModelImpl();
			sysModel.setVersion("1.0");
		}else if("open.sysmodel.panel".equals(uiType)){
			JFileChooser fileChooser = new JFileChooser();
			String userDir =  System.getProperty("user.dir");
			if(userDir!=null){
				fileChooser.setCurrentDirectory(new File(userDir));
			}
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("xml file", "xml", "XML"));
			fileChooser.setDialogTitle("请选择模型文件...");  
			fileChooser.setApproveButtonText("确定");  
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
			int result = fileChooser.showOpenDialog(this);  
			if (JFileChooser.APPROVE_OPTION == result) {  
				sysModelPath=fileChooser.getSelectedFile().getPath();
				SysModelXMLLoader loader = new SysModelXMLLoader();
				sysModel = (SysModelImpl)loader.loadFromXML(sysModelPath);
            }  
		}
		initTree(sysModel);
	}

	@Override
	public void destroyUI() {
	}

	public void initTree(SysModel sysModel){
		if(sysModel==null) return ;
		String temp = "数据模型 版本["+sysModel.getVersion()+"]";
		DefaultMutableTreeNode root  =   new  DefaultMutableTreeNode (new KeyValue("root",temp));
		//config node
		DefaultMutableTreeNode config = new  DefaultMutableTreeNode (new KeyValue("config","配置参数"));
		 root.add(config);
		//entity node
		DefaultMutableTreeNode allEntity = new  DefaultMutableTreeNode (new KeyValue("entity","实体集合"));
		root.add(allEntity);
		//type node
		Map<String,Entity> entityMap = sysModel.getEntities();
		List<String> typeList = new ArrayList<String>();
		List<String> entityNameList = new ArrayList<String>();
		for (Map.Entry<String,Entity> entry  : entityMap.entrySet()) {
			if(!typeList.contains(entry.getValue().getType())) typeList.add(entry.getValue().getType());
			if(!entityNameList.contains(entry.getKey())) entityNameList.add(entry.getKey());
		}
		//sort
		String[] typeArray = typeList.toArray(new String[0]);
		Arrays.sort(typeArray);
		String[] entityNameArray = entityNameList.toArray(new String[0]);
		Arrays.sort(entityNameArray);
		//type node
		for (String type : typeArray) {
			String desc = sysModel.getConfig().get(type);
			desc = desc==null?type:desc;
			DefaultMutableTreeNode typeNode = new  DefaultMutableTreeNode (new KeyValue(type,desc));
			allEntity.add(typeNode);
			//entity node
			for (String entityName : entityNameArray) {
				Entity entity =  entityMap.get(entityName);
				if(entity.getType().equals(type)){
					DefaultMutableTreeNode entityNode = new  DefaultMutableTreeNode (entity);
					typeNode.add(entityNode);
				}
			}
		}
		navigatorTree = new JTree(root);
		navigatorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		leftScrollPane.getViewport().add(navigatorTree);
		navigatorTree.addMouseListener(new MouseAdapter() {
		      public void mouseClicked(MouseEvent event) {
		    	  doMouseClicked(event);
		      }
		 });
	}
	private void doMouseClicked(MouseEvent event){
		TreePath tp = navigatorTree.getPathForLocation(event.getX(), event.getY());
		if(tp==null) return;
		navigatorTree.setSelectionPath(tp);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tp.getLastPathComponent();
		Object obj = node.getUserObject();
		if(obj instanceof KeyValue){
			KeyValue keyValue = (KeyValue)obj;
			if(keyValue.equalsKey("config")){
				ConfigPanel configPanel = new ConfigPanel(sysModel);
				mainPanel.removeAll();
				mainPanel.add(configPanel);
				mainPanel.updateUI();
			}else if(keyValue.equalsKey("root")){
				return;
			}else {
				String type = keyValue.getKey();
				if(type.equals("entity")) type = "";
				EntityListPanel listPanel = new EntityListPanel(sysModel,type);
				mainPanel.removeAll();
				mainPanel.add(listPanel);
				mainPanel.updateUI();
			}
		}else if(obj instanceof Entity){//entity node
			EntityPanel entityPanel = new EntityPanel(sysModel);
			Map<String,String> para = new HashMap<String,String>();
			para.put("tableName", ((Entity)obj).getTableName());
			entityPanel.initUI(para);
			mainPanel.removeAll();
			mainPanel.add(entityPanel);
			mainPanel.updateUI();
		}
	}
}
