package com.zdawn.text.lucene.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ContentSpliter {
	private String filePath;
	private String charsetName;
	private int blockSize;
	private BufferedReader reader = null;
	private int state = 0;
	private String line;
	private char[] seperator;
	
	public ContentSpliter(String filePath,String charsetName,int blockSize,char[] seperator){
		this.filePath = filePath;
		this.charsetName = charsetName;
		this.blockSize = blockSize;
		this.seperator = seperator;
	}
	private void initReader (){
		try {
			if(charsetName==null || charsetName.equals("")) charsetName = CharsetDetector.detectCharSet(filePath);
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),charsetName));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("unsupport encoding "+charsetName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file not found "+filePath);
		}
	}
	/**
	 * if return null  mean not have block content
	 */
	public String getNextBlockContent(){
		if(state==0){
			initReader();
			state = 1;
		}
		if(state==2) {
			if(line!=null) return line;
			return null;
		}
		StringBuilder sb = new StringBuilder();
		try {
			//line not null
			if(line!=null){
				if(line.length()>blockSize){
					line = splitRemains(sb,line,reader);
					return sb.toString();
				}else{
					if(sb.length()>0){
						char c = sb.charAt(sb.length()-1);
						if(c!=' ' && !exist(seperator,c)) sb.append(' ');						
					}
					sb.append(line);
				}
			}
			while(true){
				line=readLine(reader);
				if(line==null){
					state=2;
					break;
				}
				if(sb.length()+line.length()>blockSize){
					line = splitRemains(sb,line,reader);
					break;
				}else{
					if(sb.length()>0){
						char c = sb.charAt(sb.length()-1);
						if(c!=' ' && !exist(seperator,c)) sb.append(' ');						
					}
					sb.append(line);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("io error "+filePath);
		}
		return sb.length()==0 ? null:sb.toString();
	}
	/**
	 * finally this method should  invoked
	 */
	public void releaseResource(){
		try {
			if(reader!=null) reader.close();
		} catch (IOException e) {}
	}
	//must sb.length()+line.length()>blockSize
	private String splitRemains(StringBuilder sb,String line,BufferedReader reader){
		int index = blockSize - sb.length();
		String temp = line;
		if(index>0){
			sb.append(line.substring(0,index));
			temp = line.substring(index);
		}
		index = -1;
		for (int i = 0; i < temp.length(); i++) {
			if(exist(seperator, temp.charAt(i))){
				sb.append(temp.charAt(i));
				index = i;
				break;
			}else{
				sb.append(temp.charAt(i));
			}
		}
		if(index==-1){//line not found end char
			temp = cutLastSeperator(sb,reader);
		}else{
			temp = temp.substring(index+1);
		}
		//check seperator and ignore
		if(temp!=null){
			index = -1;
			for (int i = 0; i < temp.length(); i++) {
				if(exist(seperator, temp.charAt(i))) index=i;
				else break;
			}
			if(index>0) temp = temp.substring(index+1);
		}
		return temp;
	}
	private String cutLastSeperator(StringBuilder sb,BufferedReader reader){
		if(reader==null) return null;
		String temp = null;
		boolean found = false;
		try {
			while(true){
				temp=readLine(reader);
				if(temp==null){
					state=2;
					break;
				}
				for (int i = 0; i < temp.length(); i++) {
					if(exist(seperator, temp.charAt(i))){
						sb.append(temp.charAt(i));
						temp = temp.substring(i+1);
						found = true;
						break;
					}else{
						if(i==0 && sb.length()>0){
							char c = sb.charAt(sb.length()-1);
							if(c!=' ' && !exist(seperator,c)) sb.append(' ');
						}
						sb.append(temp.charAt(i));
					}
				}
				if(found) break;
				if(sb.length()>blockSize*2){
					temp = null;
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("io error "+filePath);
		}
		return temp;
	}
	private boolean exist(char[] seperator,char c){
		if(seperator==null) return false;
		for (int i = 0; i < seperator.length; i++) {
			if(seperator[i]==c) return true;
		}
		return false;
	}
	private String readLine(BufferedReader reader) throws IOException{
		String line = null;
		try {
			while((line=reader.readLine())!=null){
				line = filterWhiteSpace(line);
				if(line!=null) break;
			}
		} catch (IOException e) {
			throw e;
		}
		return line;
	}
	private String filterWhiteSpace(String origin){
		StringBuilder sb = new StringBuilder();
		int length = origin.length();
		char prev = ' ';
		for (int i = 0; i < length; i++) {
			char c = origin.charAt(i);
			if(c=='\n' || c=='\r' || c=='\t') c=' ';
			if(c==' '){
				if(prev !=' '){
					sb.append(c);
					prev=' ';
				}
			}else{
				sb.append(c);
				prev = c;
			}
		}
		return sb.length()>0 ? sb.toString():null;
	}
}
