<#function collectParseJsonChildEntityNames aboutEntityList,subEntityList>
<#local nValue = "">
<#list aboutEntityList as aboutEntity>
 	<#local nValue = nValue+",\""+aboutEntity.name?uncap_first+"\"">
 </#list>
 <#list subEntityList as subEntity>
 	<#local nValue = nValue+",\""+subEntity.name?uncap_first+"List\"">
 </#list>
  <#if nValue!="">
  <#local nValue = nValue?substring(1)>
  </#if>
<#return nValue>
</#function>