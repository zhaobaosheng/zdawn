package com.zdawn.util.json.jackson;

public class DateHourJsonSerializer extends DateSecondJsonSerializer {
	public DateHourJsonSerializer(){
		this.dateFormat = "yyyy-MM-dd HH";
	}
}
