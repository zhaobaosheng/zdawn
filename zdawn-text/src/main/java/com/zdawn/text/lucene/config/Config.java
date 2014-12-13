package com.zdawn.text.lucene.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.zdawn.text.lucene.index.IndexDBUtil;
/**
 * Lucene全文索引配置信息类
 * @author zhaobs
 */
public class Config {
	private static final String seperatorFileName = "sentenceSeperator.txt";
	/**
	 * 配置参数
	 */
	private Map<String,String> para = null;
	
	private Analyzer analyzer = null;
	/**
	 * 索引库配置集合
	 */
	private Map<String,IndexDB> indexDBMap =new HashMap<String,IndexDB>();
	
	private String paraFileName = "indexConfigPara.properties";
	/**
	 * 分片存储文档内容字符大小
	 */
	private int fileContentBlockSize = -1;
	/**
	 * 不解析文档的类型集合
	 */
	private HashSet<String> exceptParseFileTypeSet = null;
	/**
	 * 不使用句子分隔符拆分文档类型集合
	 */
	private HashSet<String> exceptSentenceSeperatorFileTypeSet = null;
	//句子分隔符
	private  char[] seperator = null;
	//解析文档缓存路径
	private String parseCacheDir = null;
	//解析文档内容存入文本文件编码
	private String saveContentCharsetName = null;
	
	private static Map<String,String> readFile(String filePath){
		Map<String,String> mapValue = new HashMap<String, String>();
		try{
			File file = new File(filePath);
			if(file.isFile()&&file.exists()){
				InputStreamReader isRead = new InputStreamReader(new FileInputStream(file),"UTF-8");
				BufferedReader reader=new BufferedReader(isRead);
				String line = null;
				while((line=reader.readLine())!=null){
					if(line.startsWith("#")) continue;
					String[] temp = line.split("=");
					if(temp.length>1){
						mapValue.put(temp[0].trim(), temp[1].trim());
					}
				}
				isRead.close();
				reader.close();
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return mapValue.size()>0 ? mapValue:null;
	}
	public String getConfigPara(String key){
		if(para==null){
			URL url = Config.class.getClassLoader().getResource(paraFileName);
			para = readFile(url.getPath());
		}
		return para.get(key);
	}
	public int getFileContentBlockSize(){
		if(fileContentBlockSize==-1){
			String para = getConfigPara("file.store.blockSize");
			fileContentBlockSize = 204800;
			if(para!=null && !para.equals("")) fileContentBlockSize = Integer.parseInt(para);
		}
		return 204800;
	}
	/**
	 * 判断是否包含不解析文档类型
	 * @param ext 文件扩展名
	 */
	public boolean containsExceptParseFileType(String ext){
		if(exceptParseFileTypeSet==null){
			String para = getConfigPara("except_parse_fileType");
			String[] fileType = para.split(",");
			exceptParseFileTypeSet = new HashSet<String>();
			for (String tmp : fileType) exceptParseFileTypeSet.add(tmp);
		}
		return exceptParseFileTypeSet.contains(ext.toLowerCase());
	}
	/**
	 * 判断是否使用句子分隔符拆分内容文档类型
	 * @param ext 文件扩展名
	 * @return true 可使用句子分隔符拆分内容
	 */
	public boolean containsSentenceSeperatorFileType(String ext){
		if(exceptSentenceSeperatorFileTypeSet==null){
			String para = getConfigPara("except_SentenceSeperator_FileType");
			String[] fileType = para.split(",");
			exceptSentenceSeperatorFileTypeSet = new HashSet<String>();
			for (String tmp : fileType) exceptSentenceSeperatorFileTypeSet.add(tmp);
		}
		return !exceptSentenceSeperatorFileTypeSet.contains(ext.toLowerCase());
	}
	
	public char[] getSentenceSeperator(String ext){
		if(seperator==null){
			URL url = IndexDBUtil.class.getClassLoader().getResource(seperatorFileName);
			String content = readFromFile(url.getPath());
			seperator = content.toCharArray();
		}
		if(ext==null) return seperator;
		if(containsSentenceSeperatorFileType(ext)) return seperator;
		return null;
	}
	public String readFromFile(String filePath) {
	    BufferedReader br = null;
	    StringBuilder sb = new StringBuilder();
	    try {
	      //构造BufferedReader对象
	      br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
	      String line = null;
	      while ((line = br.readLine()) != null) {
	    	  sb.append(line);
	      }
	    } catch (IOException e) {
	      System.out.println("read file error filePath="+filePath);
	    }finally {
	    	//关闭BufferedReader
	    	if (br != null) {
	        try {br.close();}
	        catch (IOException e) {}
	      }
	    }
	    return sb.toString();
	}
	public String getParseCacheDir(){
		if(parseCacheDir==null){
			parseCacheDir = getConfigPara("parseContentCacheDir");
		}
		return parseCacheDir;
	}
	public String getSaveContentCharsetName(){
		if(saveContentCharsetName==null){
			saveContentCharsetName = getConfigPara("saveContentCharsetName");
		}
		return saveContentCharsetName;
	}
	
	public Analyzer getAnalyzer() {
		if(analyzer==null) analyzer = new IKAnalyzer(true);
		return analyzer;
	}
	public Map<String, IndexDB> getIndexDBMap() {
		return indexDBMap;
	}
	public void setIndexDBMap(Map<String, IndexDB> indexDBMap) {
		this.indexDBMap = indexDBMap;
	}
	public void setParaFileName(String paraFileName) {
		this.paraFileName = paraFileName;
	}
}
