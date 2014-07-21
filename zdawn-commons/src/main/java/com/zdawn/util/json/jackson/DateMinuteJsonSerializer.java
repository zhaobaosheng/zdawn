package com.zdawn.util.json.jackson;

public class DateMinuteJsonSerializer extends DateSecondJsonSerializer {
	public DateMinuteJsonSerializer(){
		this.dateFormat = "yyyy-MM-dd HH:mm";
	}
}
