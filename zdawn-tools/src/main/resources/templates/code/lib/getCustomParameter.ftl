<#function getCustomParameter customParameters keyValue,defaultValue>
<#if customParameters[keyValue]??>
 	<#return customParameters[keyValue]>
  </#if>
  <#if customParameters[defaultValue]??>
 	<#return customParameters[defaultValue]>
  </#if>
<#return "">
</#function>