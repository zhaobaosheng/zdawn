<#function haveEntityValidator entityName,validatorsList>
<#list validatorsList as oneValidator>
  <#if entityName?lower_case = oneValidator.entityName?lower_case>
 	<#return "true">
  </#if>
</#list>
<#return "false">
</#function>