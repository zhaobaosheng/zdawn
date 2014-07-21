package com.zdawn.util.json.jackson;

public class DateJsonSerializer extends DateSecondJsonSerializer {
	public DateJsonSerializer(){
		this.dateFormat = "yyyy-MM-dd";
	}
}
