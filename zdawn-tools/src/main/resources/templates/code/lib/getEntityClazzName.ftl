<#function getEntityClazzName entity>
<#if entity.clazz!="">
	<#local index =  entity.clazz?last_index_of(".") >
	<#if index!=-1>
		<#return entity.clazz?substring(index+1)>
	</#if>
</#if>
<#return entity.name?cap_first>
</#function>