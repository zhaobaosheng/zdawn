package com.zdawn.tools.gencode.domain;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import com.zdawn.commons.sysmodel.metaservice.ModelFactory;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.tools.gencode.model.CodeModel;

/**
 *  根据code model 读取数据模型
 * @author zhaobs
 * 2014-07-09
 */
public class SysModelReader {
	public SysModel  readSysModel(CodeModel codeModel){
		List<String> list = codeModel.getImportEntities();
		for (String resource : list) {
			//classpath:bb/cc/DataModel.xml
			String[]  path = parseResource(resource);
			try {
				Enumeration<URL> e = SysModelReader.class.getClassLoader().getResources(path[0]);
				while(e.hasMoreElements()){
					String fullPath = e.nextElement().getPath();
					File test = new File(fullPath+File.separator+path[1]);
					if(test.exists()){
						ModelFactory.loadQueryConfigFromFileSystem(test.getPath());
					}else{
						ModelFactory.loadQueryConfigFromFileSystem(fullPath, path[1]);
					}
				}
			} catch (IOException e) {
				System.out.println("error:"+e.toString());
			}
		}
		return ModelFactory.getSysModel();
	}
	private String[] parseResource(String resource){
		if(resource==null || resource.length()==0) return null;
		if(resource.startsWith("classpath:") || resource.startsWith("CLASSPATH:")){
			String temp = resource.substring(10);
			int index = temp.lastIndexOf('/');
			if(index==-1) return new String[]{"",temp};
			return new String[]{temp.substring(0,index),temp.substring(index+1)};
		}
		return null;
	}
}
