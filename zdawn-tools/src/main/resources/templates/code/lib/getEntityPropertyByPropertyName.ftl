<#function getEntityPropertyByPropertyName propertyList,propertyName>
<#list propertyList as tempProperty>
  <#if tempProperty.name?lower_case = propertyName?lower_case>
 	<#return tempProperty>
  </#if>
</#list>
<#return "">
</#function>