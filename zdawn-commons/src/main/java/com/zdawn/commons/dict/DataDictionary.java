package com.zdawn.commons.dict;

import com.zdawn.commons.dict.model.DictData;
import com.zdawn.commons.dict.model.MetaDict;


public interface DataDictionary {
	/**
	 * 获取编码元数据
	 * @param dictName 编码表名称
	 * @return MetaDict
	 */
	public MetaDict getMetaDict(String dictName);
	/**
	 * 获取编码数据
	 * @param dictName 编码表名称
	 * @return DictData
	 */
	public DictData getDictData(String dictName);
	/**
	 * 根据条件,过滤数据
	 * @param dictName 编码表名称
	 * @param condition 暂时sql条件
	 * @return DictData
	 */
	public DictData getDictData(String dictName,String condition);
}
