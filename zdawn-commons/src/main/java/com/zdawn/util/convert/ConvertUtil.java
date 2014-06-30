package com.zdawn.util.convert;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 数据转换
 * <br>java.lang.Boolean
 * <br>java.lang.String
 * <br>java.lang.Short
 * <br>java.lang.Integer
 * <br>java.lang.Long
 * <br>java.lang.Double
 * <br>java.lang.Float
 * <br>java.math.BigDecimal
 * <br>java.util.Date|java.sql.Date
 * <br>java.sql.Timestamp
 * @author zhaobs
 */
public class ConvertUtil {
	private final static Map<String,Integer> clazzFlagMap = new HashMap<String, Integer>();
	private static final Set<String> trueValues = new HashSet<String>(5);
	private static final Set<String> falseValues = new HashSet<String>(5);
	static{
		clazzFlagMap.put("java.lang.String",0);
		clazzFlagMap.put("java.lang.Boolean",1);
		clazzFlagMap.put("boolean",1);
		clazzFlagMap.put("java.lang.Short",2);
		clazzFlagMap.put("short",2);
		clazzFlagMap.put("java.lang.Integer",3);
		clazzFlagMap.put("int",3);
		clazzFlagMap.put("java.lang.Long",4);
		clazzFlagMap.put("long",4);
		clazzFlagMap.put("java.lang.Double",5);
		clazzFlagMap.put("double",5);
		clazzFlagMap.put("java.lang.Float",6);
		clazzFlagMap.put("float",6);
		clazzFlagMap.put("java.math.BigDecimal",7);
		clazzFlagMap.put("java.util.Date",8);
		clazzFlagMap.put("java.sql.Date",8);
		clazzFlagMap.put("java.sql.Timestamp",9);
		
		trueValues.add("true");
		falseValues.add("false");

		trueValues.add("on");
		falseValues.add("off");

		trueValues.add("yes");
		falseValues.add("no");

		trueValues.add("1");
		falseValues.add("0");
		
		trueValues.add("Y");
		falseValues.add("N");
	}
	/**
	 * 字符串转换成指定类型对象，转换失败返回 null。
	 * @param clazz 类名
	 * @param value 字符串
	 * @param format 字符串格式
	 */
	public static Object convertToObject(String clazz,String value,String format){
		Integer flag = clazzFlagMap.get(clazz);
		if(flag==null){
			System.out.println("warning not found "+clazz);
			return null;
		}
		Object obj = null;
		switch (flag) {
		case 0://String 
			obj = value;
			break;
		case 1://boolean 
			obj = convertBoolean(value);
			break;
		case 2://short 
			obj = convertShort(value);
			break;
		case 3://integer
			obj = convertInteger(value);
			break;
		case 4://long
			obj = convertLong(value);
			break;
		case 5://double
			obj = convertDouble(value);
			break;
		case 6://float
			obj = convertFloat(value);
			break;
		case 7://bigDecimal
			obj = convertBigDecimal(value);
			break;
		case 8://date
			obj = convertDate(value, format);
			break;
		case 9://timestamp 
			obj = convertTimestamp(value, format);
			break;
		default:
			break;
		}
		return obj;
	}
	/**
	 * 转换为指定数据类型对象,转换失败返回 null.
	 * @param clazz 类名
	 * @param value 待转换对象
	 * @param format 字符串格式
	 */
	public static Object convertToObject(String clazz,Object value,String format){
		Integer flag = clazzFlagMap.get(clazz);
		if(flag==null){
			System.out.println("warning not found "+clazz);
			return null;
		}
		Object obj = null;
		switch (flag) {
		case 0://String 
			obj = value;
			break;
		case 1://boolean 
			obj = convertBoolean(value);
			break;
		case 2://short 
			obj = convertShort(value);
			break;
		case 3://integer
			obj = convertInteger(value);
			break;
		case 4://long
			obj = convertLong(value);
			break;
		case 5://double
			obj = convertDouble(value);
			break;
		case 6://float
			obj = convertFloat(value);
			break;
		case 7://bigDecimal
			obj = convertBigDecimal(value);
			break;
		case 8://date
			obj = convertDate(value, format);
			break;
		case 9://timestamp 
			obj = convertTimestamp(value, format);
			break;
		default:
			break;
		}
		return obj;
	}
	/**
	 * 对象转换字符串
	 * <br>Date类型 按format格式转换
	 * <br>其他调用toString方法
	 */
	public static String convertToString(Object value,String format){
		if(value==null) return "";
		if(value instanceof String) return value.toString();
		if(value instanceof Date){
			return convertDateFormat(value,format);
		}
		return value.toString();
	}
	
	public static Integer convertInteger(Object value){
		if(value==null) return null;
		if(value instanceof String){
			return new Integer(value.toString());
		}
		return (Integer)value;
	}
	public static Integer convertInteger(String value){
		if(value==null) return null;
		return new Integer(value.toString());
	}
	
	public static Long convertLong(Object value){
		if(value==null) return null;
		if(value instanceof String){
			return new Long(value.toString());
		}
		return (Long)value;
	}
	public static Long convertLong(String value){
		if(value==null) return null;
		return new Long(value.toString());
	}
	
	public static Double convertDouble(Object value){
		if(value==null) return null;
		if(value instanceof String){
			return new Double(value.toString());
		}
		return (Double)value;
	}
	public static Double convertDouble(String value){
		if(value==null) return null;
		return new Double(value.toString());
	}
	
	public static Float convertFloat(Object value){
		if(value==null) return null;
		if(value instanceof String){
			return new Float(value.toString());
		}
		return (Float)value;
	}
	public static Float convertFloat(String value){
		if(value==null) return null;
		return new Float(value.toString());
	}
	
	public static BigDecimal convertBigDecimal(Object value){
		if(value==null) return null;
		if(value instanceof String){
			return new BigDecimal(value.toString());
		}
		return (BigDecimal)value;
	}
	public static BigDecimal convertBigDecimal(String value){
		if(value==null) return null;
		return new BigDecimal(value.toString());
	}
	/**
	 * 字符串格式
	 * <br>format=long example 1401370882231
	 * <br>format=yyyy-MM-dd example 2014-04-23
	 */
	public static Date convertDate(Object value,String format){
		if(value==null) return null;
        Date date = null;
        if (format.equals("yyyy-MM-dd"))
        	date = new Date(Timestamp.valueOf(value+" 0:0:0").getTime());
        else if(format.equals("long"))
        	date = new Date(Long.parseLong(value.toString()));
        return date;
	}
	/**
	 * 字符串格式
	 * <br>format=long example 1401370882231
	 * <br>format=yyyy-MM-dd example 2014-04-23
	 * <br>format=yyyy-MM-dd HH example 2014-04-23 12
	 * <br>format=yyyy-MM-dd HH:mm example 2014-04-23 12:20
	 * <br>format=yyyy-MM-dd HH:mm:ss example 2014-04-23 12:20:15
	 */
	public static Timestamp convertTimestamp(Object value,String format){
		if(value==null) return null;
        Timestamp timestamp = null;
        if (format.equals("yyyy-MM-dd"))
            timestamp = Timestamp.valueOf(value+" 0:0:0");
        else if (format.equals("yyyy-MM-dd HH"))
            timestamp = Timestamp.valueOf(value+":0:0");
        else if (format.equals("yyyy-MM-dd HH:mm"))
            timestamp = Timestamp.valueOf(value+":0");
        else if (format.equals("yyyy-MM-dd HH:mm:ss"))
            timestamp = Timestamp.valueOf(value.toString());
        else if(format.equals("long"))
        	timestamp = new Timestamp(Long.parseLong(value.toString()));
        return timestamp;
	}
	public static Boolean convertBoolean(Object value) {
		if(value==null) return null;
		if(value instanceof String){
			if (trueValues.contains(value)) {
				return Boolean.TRUE;
			}else if (falseValues.contains(value)) {
				return Boolean.FALSE;
			}
		}
		return null;
	}
	
	public static Short convertShort(Object value){
		if(value==null) return null;
		if(value instanceof String){
			return new Short(value.toString());
		}
		return (Short)value;
	}
	public static Short convertShort(String value){
		if(value==null) return null;
		return new Short(value.toString());
	}
	public static String convertDateFormat(Object value,String format){
		if(value==null) return "";
		SimpleDateFormat df =new SimpleDateFormat(format);
		return df.format(value);
	}
	/**
	 * default format yyyy-MM-dd
	 */
	public static String convertDateFormat(Object value){
		return convertDateFormat(value,"yyyy-MM-dd");
	}
}
