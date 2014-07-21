<#macro handleEntityValidator nonceEntity aboutEntityList subEntityList validatorsList exceptionType tabCount>
	<#if (validatorsList?size > 0)>
<@indentTabLine tabCount/>//执行验证	
<@indentTabLine tabCount/>String[] result = null;
	</#if>
	<#local entityName = nonceEntity.name,
		entityNameLower = entityName?uncap_first>
	<#list validatorsList as oneValidator>
		<#if nonceEntity.name?lower_case = oneValidator.entityName?lower_case>
			<#local entityProperty = getEntityPropertyByPropertyName(nonceEntity.properties,oneValidator.propertyName),
			        propertyName = entityProperty.name?cap_first>
			<#if oneValidator.type="rule">
<@indentTabLine tabCount/>result = ValidatorUtil.validateRequire(${entityNameLower}.get${propertyName}(),"${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount/>if(result[0].equals("false")) throw new ${exceptionType} ("${entityName}.${propertyName?uncap_first}",result[1]);
			<#elseif oneValidator.type="lengthRule">
<@indentTabLine tabCount/>result = ValidatorUtil.validateLength(${entityNameLower}.get${propertyName}(),${oneValidator.require},${oneValidator.minLength},${oneValidator.maxLength},"${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount/>if(result[0].equals("false")) throw new ${exceptionType} ("${entityName}.${propertyName?uncap_first}",result[1]);
			<#elseif oneValidator.type="regxRule">
<@indentTabLine tabCount/>result = ValidatorUtil.validateRegx(${entityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.expression}","${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount/>if(result[0].equals("false")) throw new ${exceptionType} ("${entityName}.${propertyName?uncap_first}",result[1]);			
			<#elseif oneValidator.type="exceptRule">
				<#if oneValidator.codeDicName?? && oneValidator.codeDicName!="">
<@indentTabLine tabCount/>result = DataDictUtil.validateExcept(${entityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.codeDicName}","${entityProperty.description}","${oneValidator.errorMessage}");
				<#else>
<@indentTabLine tabCount/>result = ValidatorUtil.validateExcept(${entityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.content}","${entityProperty.description}","${oneValidator.errorMessage}");
				</#if>
<@indentTabLine tabCount/>if(result[0].equals("false")) throw new ${exceptionType} ("${entityName}.${propertyName?uncap_first}",result[1]);
			</#if>
		</#if>
	</#list>
	<#list aboutEntityList as aboutEntity>
	<#if haveEntityValidator(aboutEntity.name,validatorsList)="true">
	<#local aboutEntityName = aboutEntity.name?cap_first,
			aboutEntityNameLower = aboutEntityName?uncap_first>
<@indentTabLine tabCount/>${aboutEntityName} ${aboutEntityNameLower} = ${entityNameLower}.get${aboutEntityName}();
<@indentTabLine tabCount/>if(${aboutEntityNameLower}!=null){
	<#list validatorsList as oneValidator>
		<#if aboutEntity.name?lower_case = oneValidator.entityName?lower_case>
			<#local entityProperty = getEntityPropertyByPropertyName(aboutEntity.properties,oneValidator.propertyName),
			        propertyName = entityProperty.name?cap_first>
			<#if oneValidator.type="rule">
<@indentTabLine tabCount+1/>result = ValidatorUtil.validateRequire(${aboutEntityNameLower}.get${propertyName}(),"${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount+1/>if(result[0].equals("false")) throw new ${exceptionType} ("${aboutEntityName}.${propertyName?uncap_first}",result[1]);
			<#elseif oneValidator.type="lengthRule">
<@indentTabLine tabCount+1/>result = ValidatorUtil.validateLength(${aboutEntityNameLower}.get${propertyName}(),${oneValidator.require},${oneValidator.minLength},${oneValidator.maxLength},"${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount+1/>if(result[0].equals("false")) throw new exceptionType ("${aboutEntityName}.${propertyName?uncap_first}",result[1]);
			<#elseif oneValidator.type="regxRule">
<@indentTabLine tabCount+1/>result = ValidatorUtil.validateRegx(${aboutEntityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.expression}","${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount+1/>if(result[0].equals("false")) throw new ${exceptionType} ("${aboutEntityName}.${propertyName?uncap_first}",result[1]);			
			<#elseif oneValidator.type="exceptRule">
				<#if oneValidator.codeDicName?? && oneValidator.codeDicName!="">
<@indentTabLine tabCount+1/>result = DataDictUtil.validateExcept(${aboutEntityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.codeDicName}","${entityProperty.description}","${oneValidator.errorMessage}");
				<#else>
<@indentTabLine tabCount+1/>result = ValidatorUtil.validateExcept(${aboutEntityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.content}","${entityProperty.description}","${oneValidator.errorMessage}");
				</#if>
<@indentTabLine tabCount+1/>if(result[0].equals("false")) throw new ${exceptionType} ("${aboutEntityName}.${propertyName?uncap_first}",result[1]);
			</#if>
		</#if>
	</#list>	
<@indentTabLine tabCount/>}
	</#if>
	</#list>
	<#list subEntityList as subEntity>
	<#if haveEntityValidator(subEntity.name,validatorsList)="true">
	<#local subEntityName = subEntity.name?cap_first,
			subEntityNameLower = subEntityName?uncap_first>
			
<@indentTabLine tabCount/>List<${subEntityName}> ${subEntityNameLower}List = ${entityNameLower}.get${subEntityName}List();
<@indentTabLine tabCount/>if(${subEntityNameLower}List!=null){
<@indentTabLine tabCount+1/>for(${subEntityName} ${subEntityNameLower}:${subEntityNameLower}List){
	<#list validatorsList as oneValidator>
		<#if subEntity.name?lower_case = oneValidator.entityName?lower_case>
			<#local entityProperty = getEntityPropertyByPropertyName(subEntity.properties,oneValidator.propertyName),
			        propertyName = entityProperty.name?cap_first>
			<#if oneValidator.type="rule">
<@indentTabLine tabCount+2/>result = ValidatorUtil.validateRequire(${subEntityNameLower}.get${propertyName}(),"${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount+2/>if(result[0].equals("false")) throw new ${exceptionType} ("${subEntityName}.${propertyName?uncap_first}",result[1]);
			<#elseif oneValidator.type="lengthRule">
<@indentTabLine tabCount+2/>result = ValidatorUtil.validateLength(${subEntityNameLower}.get${propertyName}(),${oneValidator.require},${oneValidator.minLength},${oneValidator.maxLength},"${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount+2/>if(result[0].equals("false")) throw new ${exceptionType} ("${subEntityName}.${propertyName?uncap_first}",result[1]);
			<#elseif oneValidator.type="regxRule">
<@indentTabLine tabCount+2/>result = ValidatorUtil.validateRegx(${subEntityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.expression}","${entityProperty.description}","${oneValidator.errorMessage}");
<@indentTabLine tabCount+2/>if(result[0].equals("false")) throw new ${exceptionType} ("${subEntityName}.${propertyName?uncap_first}",result[1]);			
			<#elseif oneValidator.type="exceptRule">
				<#if oneValidator.codeDicName?? && oneValidator.codeDicName!="">
<@indentTabLine tabCount+2/>result = DataDictUtil.validateExcept(${subEntityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.codeDicName}","${entityProperty.description}","${oneValidator.errorMessage}");
				<#else>
<@indentTabLine tabCount+2/>result = ValidatorUtil.validateExcept(${subEntityNameLower}.get${propertyName}(),${oneValidator.require},"${oneValidator.codeDicName}","${entityProperty.description}","${oneValidator.errorMessage}");
				</#if>
<@indentTabLine tabCount+2/>if(result[0].equals("false")) throw new ${exceptionType} ("${subEntityName}.${propertyName?uncap_first}",result[1]);
			</#if>
		</#if>
	</#list>
<@indentTabLine tabCount+1/>}
<@indentTabLine tabCount/>}
	</#if>
	</#list>
</#macro>