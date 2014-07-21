<#function haveHandleEntityByEntityName entityName,handleEntityList>
<#if (handleEntityList?size>0)>
<#list handleEntityList as handleEntity>
  <#if entityName?lower_case = handleEntity.name?lower_case>
 	<#return handleEntity>
  </#if>
</#list>
</#if>
<#return "">
</#function>