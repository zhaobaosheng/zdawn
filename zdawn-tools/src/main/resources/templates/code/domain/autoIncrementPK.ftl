<#function autoIncrementPK pkProperty>
<#if pkProperty.defaultValue?? && pkProperty.defaultValue ="auto_increment">
 	<#return "true">
  </#if>
<#return "false">
</#function>