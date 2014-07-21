<#function exceptPropertyByNames propertyName,propertyNameList>
<#list propertyNameList as oneName>
  <#if oneName?lower_case = propertyName?lower_case>
 	<#return "true">
  </#if>
</#list>
<#return "false">
</#function>