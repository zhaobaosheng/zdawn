package com.zdawn.util.vo;

public class VOUtil {
	public static ResponseMessage createResponseMessage(String result,String desc,Object data){
		ResponseMessage msg = new ResponseMessage();
		msg.setResult(result);
		msg.setDesc(desc);
		msg.setData(data);
		return msg;
	}
	public static ResponseMessage createResponseMessage(boolean success,String desc,Object data){
		return createResponseMessage(String.valueOf(success),desc,data);
	}
}
