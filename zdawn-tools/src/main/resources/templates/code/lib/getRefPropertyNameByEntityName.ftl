<#function getRefPropertyNameByEntityName entity,refEntityName>
<#if entity.relations??>
<#list entity.relations as relation>
  <#if relation.entityName?lower_case = refEntityName?lower_case>
 	<#return relation.propertyName>
  </#if>
</#list>
</#if>
<#return "">
</#function>