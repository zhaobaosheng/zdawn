<#function haveFormatProperty entity,handleEntity>
<#if handleEntity!="" && handleEntity.dataItems??>
<#list handleEntity.dataItems as dataItem>
	<#list entity.properties as property>
		<#if property.name=dataItem[0]>
			<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
			<#return "true">
			</#if>
		</#if>
	</#list>
</#list>
<#else>
<#list entity.properties as property>
		<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
		<#return "true">
		</#if>
</#list>
</#if>
<#return "false">
</#function>