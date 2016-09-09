package com.zdawn.util.json.jackson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateSecondJsonSerializer extends JsonSerializer<Date> {
	protected String dateFormat = "yyyy-MM-dd HH:mm:ss";
	@Override
	public void serialize(Date value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,JsonProcessingException {
		DateFormat df = new SimpleDateFormat(dateFormat);
		String strDate = df.format(value);
		jgen.writeString(strDate);
	}
}
