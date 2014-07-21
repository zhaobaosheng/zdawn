<#function collectChildEntityNames aboutEntityList,subEntityList>
<#local nValue = "">
<#if (aboutEntityList?size>0)>
	<#list aboutEntityList as aboutEntity>
 	<#local nValue = nValue+",\""+aboutEntity.name+"\"">
 	</#list>
  </#if>
  <#if (subEntityList?size>0)>
	<#list subEntityList as subEntity>
 	<#local nValue = nValue+",\""+subEntity.name+"\"">
 	</#list>
  </#if>
  <#if nValue!="">
  <#local nValue = nValue?substring(1)>
  </#if>
<#return nValue>
</#function>