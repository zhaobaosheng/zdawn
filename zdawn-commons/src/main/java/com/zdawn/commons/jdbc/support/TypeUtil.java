package com.zdawn.commons.jdbc.support;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * create time 2006-5-16
 * @author nbs
 */
public class TypeUtil {
    private static Map<String,AbstractType> typeMap = new HashMap<String,AbstractType>();
    private static final Set<String> numberType = new HashSet<String>();
    private static final Set<String> dateType = new HashSet<String>();
    static{
        typeMap.put("java.lang.String",new StringType());
        AbstractType temp = new BooleanType();
		typeMap.put("java.lang.Boolean",temp);
		typeMap.put("boolean",temp);
		temp = new ShortType();
		typeMap.put("java.lang.Short",temp);
		typeMap.put("short",temp);
		temp = new IntegerType();
		typeMap.put("java.lang.Integer",temp);
		typeMap.put("int",temp);
		temp = new LongType();
		typeMap.put("java.lang.Long",temp);
		typeMap.put("long",temp);
		temp = new DoubleType();
		typeMap.put("java.lang.Double",temp);
		typeMap.put("double",temp);
		temp = new FloatType();
		typeMap.put("java.lang.Float",temp);
		typeMap.put("float",temp);
		typeMap.put("java.math.BigDecimal",new BigDecimalType());
		temp = new DateType();
		typeMap.put("java.util.Date",temp);
		typeMap.put("java.sql.Date",temp);
		typeMap.put("java.sql.Timestamp",new TimestampType());
		//lob
		typeMap.put("streamToClob",new ClobType(ClobType.STREAM_TO_CLOB));
		typeMap.put("stringToClob",new ClobType(ClobType.STRING_TO_CLOB));
		typeMap.put("streamToBlob",new BlobType(BlobType.STREAM_TO_BLOB));
		typeMap.put("bytearrayToBlob",new BlobType(BlobType.BYTEARRAY_TO_BLOB));
		//number
		numberType.add("java.lang.Short");
		numberType.add("java.lang.Integer");
		numberType.add("java.lang.Long");
		//date
		dateType.add("java.util.Date");
		dateType.add("java.sql.Date");
		dateType.add("java.sql.Timestamp");
    }
    public static AbstractType getDataType(String type) throws SQLException{
    	AbstractType typeObject = typeMap.get(type);
        if(typeObject==null) throw new SQLException("not suport type "+type);
        return typeObject;
    }
    public static void registerType(String key,AbstractType type){
    	if(typeMap.containsKey(key)){
    		System.out.println(key+" type already exist! replace old version");
    	}
    	typeMap.put(key, type);
    }
    public static void unregisterType(String key){
    	typeMap.remove(key);
    }
    public static boolean isNumber(String key){
    	return numberType.contains(key);
    }
    public static boolean isDate(String key){
    	return dateType.contains(key);
    }
}