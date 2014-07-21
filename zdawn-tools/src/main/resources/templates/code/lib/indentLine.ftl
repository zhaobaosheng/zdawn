<#macro indentTabLine count>
<#if count gt 0>
	<#local tabText="">
	<#list 1..count as x>
	<#local tabText=tabText+"\t">
	</#list>
${tabText}<#rt>
</#if>
</#macro>