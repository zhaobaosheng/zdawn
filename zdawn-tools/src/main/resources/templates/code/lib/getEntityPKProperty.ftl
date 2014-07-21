<#function getEntityPKProperty entity>
<#list entity.properties as tempProperty>
  <#if tempProperty.column =entity.uniqueColumn>
 	<#return tempProperty>
  </#if>
</#list>
<#return "">
</#function>