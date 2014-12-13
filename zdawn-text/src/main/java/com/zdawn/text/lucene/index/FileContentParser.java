package com.zdawn.text.lucene.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

public class FileContentParser {
	/**
	 * 解析文件文本内容
	 * @param filePath 文件路径
	 * @param cacheDir 解析后文本缓存路径
	 * @return 解析文本文件全路径
	 */
	public String parseContent(String filePath,String cacheDir,String charsetName) throws Exception{
		InputStream is = null;
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		FileOutputStream os = null;
		String outpath = null;
		try {
			AutoDetectParser autoParser = new AutoDetectParser();
			Metadata metadata = new Metadata();
			File file  = new File(filePath);
			if(!file.exists()) throw new Exception(filePath+" not found");
			is = new FileInputStream(file);
			outpath = cacheDir+File.separator+UUID.randomUUID().toString()+".txt";
			os = new FileOutputStream(outpath);
			osw=new OutputStreamWriter(os,charsetName);
			bw = new BufferedWriter(osw);
			metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
			BodyContentHandler handler = new BodyContentHandler(bw);
			autoParser.parse(is, handler, metadata);
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				if(is!=null) is.close();
			} catch (Exception e) {}
			try {
				if(bw!=null) bw.close();
			} catch (Exception e) {}
			try {
				if(osw!=null) osw.close();
			} catch (Exception e) {}
			try {
				if(os!=null) os.close();
			} catch (Exception e) {}
		}
		return outpath;
	}
	/**
	 * 删除文件
	 * @param path 文件路径
	 */
	public static void deleteFile(String path){
		File file = new File(path);
		if(file.exists()) file.delete();
	}
	
	public static void main(String[] arg){
		try {
			FileContentParser parser = new FileContentParser();
			String path = parser.parseContent(
					"C:/Users/zhaobs/Desktop/test/通州区域卫生平台项目概要设计说明书zxy10月12.docx",
					"C:/Users/zhaobs/Desktop/temp", "utf-8");
			System.out.println(path);
			ContentSpliter contentSpliter = new ContentSpliter(path,
					"UTF-8",1024*2,new char[]{'.','?','。','？','！','!'});
			String block = contentSpliter.getNextBlockContent();
			while(block!=null){
				System.out.println(block);
				System.out.println("---------------"+block.length()+"----------------");
				//next
				block = contentSpliter.getNextBlockContent();
			}
			contentSpliter.releaseResource();
			FileContentParser.deleteFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
