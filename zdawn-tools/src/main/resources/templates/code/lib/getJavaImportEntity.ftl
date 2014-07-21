<#macro getJavaImportEntity packagePath oneEntityList twoEntityList>
	<#-- one in two -->
	<#list oneEntityList as oneEntity>
		<#local same = "false">
		<#list twoEntityList as twoEntity>
			<#if oneEntity.name=twoEntity.name>
				<#local same = "true">
				<#break>
			</#if>
		</#list>
		<#if same="false">
			<#if oneEntity.clazz!="">
import ${oneEntity.clazz};
			<#else>
import ${packagePath}.dao.model.${oneEntity.name?cap_first};
			</#if>
		</#if>
	</#list>
	<#-- two in one -->
	<#list twoEntityList as twoEntity>
		<#local same = "false">
		<#list oneEntityList as oneEntity>
			<#if twoEntity.name=oneEntity.name>
				<#local same = "true">
				<#break>
			</#if>
		</#list>
		<#if same="false">
			<#if twoEntity.clazz!="">
import ${twoEntity.clazz};
			<#else>
import ${packagePath}.dao.model.${twoEntity.name?cap_first};
			</#if>
		</#if>
	</#list>
</#macro>
<#macro getJavaImportNonceEntity packagePath nonceEntity>
	<#if nonceEntity.clazz!="">
import ${nonceEntity.clazz};
	<#else>
import ${packagePath}.dao.model.${nonceEntity.name?cap_first};
	</#if>
</#macro>
<#macro getJavaImportListEntity packagePath entityList>
	<#list entityList as entity>
	<#if entity.clazz!="">
import ${entity.clazz};
	<#else>
import ${packagePath}.dao.model.${entity.name?cap_first};
	</#if>
	</#list>
</#macro>