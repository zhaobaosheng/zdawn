package com.zdawn.util.json.jackson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

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
