<#function parseNameFromFullClazzName fullClazzName>
<#local nValue = "">
<#local index = fullClazzName?last_index_of(".") >
<#if index!=-1>
	<#local nValue = fullClazzName?substring(index+1)>
<#else>
	<#local nValue =fullClazzName>
</#if>
<#return nValue>
</#function>