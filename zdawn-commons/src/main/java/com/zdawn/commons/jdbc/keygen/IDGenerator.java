package com.zdawn.commons.jdbc.keygen;


/**
 * ID标识生成接口
 * @author zhaobs
 */
public interface IDGenerator {
	/**
	 * 生成标识-生成失败抛出RuntimeException
	 * @param entityName 实体标识
	 * @return 唯一标识
	 * @throws RuntimeException
	 */
	public String generateString(String entityName);
	
	public Long generateLong(String entityName);

	public Integer generateInteger(String entityName);
}
