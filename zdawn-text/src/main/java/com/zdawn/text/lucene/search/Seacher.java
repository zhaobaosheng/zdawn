package com.zdawn.text.lucene.search;

import java.util.List;
import java.util.Map;
/**
 * 查询接口
 * @author zhaobs
 * <br>lucene查询语法
 * <br>语法关键字
 *  + - && || ! ( ) { } [ ] ^ " ~ * ? : 
 *  <br>查询词中本身包含关键字使用 \进行转义
 *  
 *  <br>1 查询域语法
 *  <br>  查询域和查询词之间用:分隔 example title:"Do it right"
 *  <br>  如果查询域没有指定在默认域中查找 example title:Do it right  则仅表示在title中查询Do 而it right要在默认域中查询
 *  
 *  <br>2 布尔操作符语法
 *  <br>  连接符AND OR 空格被认为是OR的关系
 *  <br>  修饰符 + - NOR 
 *  <br>  +表示一个查询条件是必须满足的(required) NOT和-表示一个查询条件禁止出现(prohibited)
 *   
 *   <br>3 区间查询语法
 *   <br>  包含边界[A TO B] example date:[20020101 TO 20030101]
 *   <br>  不包含边界{A TO B} date:{20020101 TO 20030101}
 *   
 *   <br>4 增加一个查询词的权重语法
 *   <br>  ^N设定查询词的权重 example A^4 B A权重是4
 *   
 *   <br>5 模糊查询语法
 *   <br>  两个词的差别小于某个比例就算匹配 A~0.8 即表示差别小于0.2，相似度大于0.8才算匹配
 *   
 *   <br>6 通配符查询语法
 *   <br>  ?表示一个字符
 *   <br>  *表示多个字符
 */
public interface Seacher {
	/**
	 * 查询最符合查询条件文档数据
	 * @param dbName 索引库名字
	 * @param para 查询条件
	 * <br>key=queryString lucene查询语法
	 * <br>key=topNum 查询最大记录数 默认100 可选
	 * <br>key=sort 排序字段(按文档评分检索出结果在按某个字段排序) 可选  暂时支持一个字段
	 * <br>key=sortDataType 排序字段数据类型 string or number 不传本参数默认为string
	 * <br>key=sortAsc 是否升序 true or false 不传本参数默认为false降序
	 * <br>key=fieldList 查询数据列表 逗号分隔 可选
	 * <br>key=defaultField 默认查询字段
	 * <br>key=accessFieldName存储权限字段
	 * <br>key=accessValues授权字段值 多个使用逗号分隔 可选
	 * @return 文档数据集合
	 */
	public List<Map<String,Object>> searchTopDocument(String dbName,Map<String,String> para) throws Exception;
	/**
	 * 分页查询最符合查询条件文档数据
	 * @param dbName 索引库名字
	 * @param para 查询条件
	 * <br>cacheId 缓存Id 可选
	 * <br>page 当前页从1开始，默认1
	 * <br>rows 每页查询记录数，默认10
 	 * <br>其它查询参数@searchTopDocument方法
	 * @return 分页数据
	 * <br>key=totalHits 检索总结果数量
	 * <br>key=cacheId 缓存Id，作为查询参数回传可利用上次查询结果
	 * <br>key=page 当前页
	 * <br>key= 总数
	 * <br>key=rows 分页数据
	 * <br>List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public Map<String,Object> searchPageDocument(String dbName,Map<String,String> para) throws Exception;
}
