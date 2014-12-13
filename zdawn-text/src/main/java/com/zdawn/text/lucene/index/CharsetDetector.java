package com.zdawn.text.lucene.index;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.txt.Icu4jEncodingDetector;

public class CharsetDetector {
	/**
	 * 探测文件的编码
	 * <br>使用tika实现
	 * @param filePah 文件路径
	 * @return charset name
	 * <br> null 没有探测编码
	 */
	public static String detectCharSet(String filePath){
		TikaInputStream tikaInputStream=null;
		InputStream in = null;
		String encode = null;
		try {
			in  = new FileInputStream(filePath);
			tikaInputStream=TikaInputStream.get(in);
			Icu4jEncodingDetector encodingDetector=new Icu4jEncodingDetector();
			Charset charset=encodingDetector.detect(tikaInputStream,new Metadata());
			encode = charset.name();
		} catch (IOException e) {
			System.out.println(e.toString());
		}finally{
			try {
				if(tikaInputStream!=null) tikaInputStream.close();
			} catch (IOException e) {}
			try {
				if(in!=null) in.close();
			} catch (IOException e) {}
		}
		return encode;
	}
}
