<#macro add_updateCommonService metaMethod>
	/**
	 * ${metaMethod.nameCn}
	 */
	@RequestMapping(value="/${_methodName}.do",method = RequestMethod.POST)
	public ModelAndView ${_methodName}(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model = new ModelAndView("jackson");
		try {
			//收集提交参数
			Map<String, String> para = HttpRequestUtil.getParameter(request);
			${entityName} ${entityNameLower} = null;
			//获取提交数据类型
			String isJson =  para.get("isJson");
			isJson = isJson==null ? "true":isJson;
			if(isJson.equals("true")){
				String jsonData = para.get("data");
				if(jsonData==null || jsonData.equals("")){
					throw new DawnException("json","参数data无数据");
				}
				${entityNameLower} = bindJsonDataFor${_methodName?cap_first}(jsonData);
			}else{
				//将提交参数装配成对象		
				${entityNameLower} = bindKeyValueDataFor${_methodName?cap_first}(para);
			}
			//调用方法
			Map<String,Object> context = new HashMap<String,Object>();
			//current user
			context.put("userInfo",prepareCurrentUser(request));
			context.put("para", para);
			${entityNameLower} = ${metaClazz.name?uncap_first}.${_methodName}(${entityNameLower},context);
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(true,"${metaMethod.nameCn}成功",${entityNameLower}));
			context.clear();
		}catch (DawnException e) {
			Object errorData = e.getErrorObject()==null ? e.toString():e.getErrorObject();
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),errorData));
			log.error("${_methodName}", e);
		}catch (Exception e) {
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
			log.error("${_methodName}", e);
		}
		return model;
	}
	// 键值对绑定对象
	private ${entityName} bindKeyValueDataFor${_methodName?cap_first}(Map<String, String> para){
		//装配当前对象
		Map<String,String> entityAttri = Utils.truncateKeyPrefix("${entityNameLower}_",para,false);
		if(entityAttri.size()==0) return null;
		<#assign tmpHandleEntity = haveHandleEntityByEntityName(methodEntityMap[metaMethod.name+"nonceEntity"].name,metaMethod.handleEntities),
					_existFormatProperty = haveFormatProperty(methodEntityMap[metaMethod.name+"nonceEntity"],tmpHandleEntity)>
		<#if _existFormatProperty="true">
		Map<String,String> propertyNameFormat = new HashMap<String, String>();
		<#list methodEntityMap[metaMethod.name+"nonceEntity"].properties as property>
			<#if tmpHandleEntity.dataItems??>
				<#list tmpHandleEntity.dataItems as dataItem>
					<#if property.name=dataItem[0]>
						<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
		propertyNameFormat.put("${property.name}","${property.toStringformat}");
						</#if>
					</#if>
				</#list>
			<#else>
				<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
		propertyNameFormat.put("${property.name}","${property.toStringformat}");
				</#if>
			</#if>
		</#list>
		${entityName} ${entityNameLower} = BeanUtil.bindBean(${entityName}.class,entityAttri,null, propertyNameFormat);
		<#else>
		${entityName} ${entityNameLower} = BeanUtil.bindBean(${entityName}.class,entityAttri,null,null);
		</#if>
		//装配相关对象
		<#list methodEntityMap[metaMethod.name+"aboutEntityList"] as aboutEntity>
		<#assign aboutEntityName = aboutEntity.name,
				 aboutEntityNameLower = aboutEntityName?uncap_first,
				 tmpHandleEntity = haveHandleEntityByEntityName(aboutEntity.name,metaMethod.handleEntities),
				 _existFormatProperty = haveFormatProperty(aboutEntity,tmpHandleEntity)>
		entityAttri = Utils.truncateKeyPrefix("${aboutEntityNameLower}_",para,false);
		if(entityAttri.size()>0){
			<#if _existFormatProperty="true">
			Map<String,String> tmpPropertyNameFormat = new HashMap<String, String>();
			<#list aboutEntity.properties as property>
				<#if tmpHandleEntity.dataItems??>
					<#list _existFormatProperty.dataItems as dataItem>
						<#if property.name=dataItem[0]>
							<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
							</#if>
						</#if>
					</#list>
				<#else>
					<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
					</#if>
				</#if>
			</#list>
			${aboutEntityName} ${aboutEntityNameLower} = BeanUtil.bindBean(${aboutEntityName}.class,entityAttri,null, propertyNameFormat);
			<#else>
			${aboutEntityName} ${aboutEntityNameLower} = BeanUtil.bindBean(${aboutEntityName}.class,entityAttri,null,null);
			</#if>
			${entityNameLower}.set${aboutEntityName}(${aboutEntityNameLower});
		 }
		</#list>
		//装配子对象
		<#list methodEntityMap[metaMethod.name+"subEntityList"] as subEntity>
		<#assign subEntityName = subEntity.name,
				 subEntityNameLower = subEntityName?uncap_first,
				 tmpHandleEntity = haveHandleEntityByEntityName(subEntity.name,metaMethod.handleEntities),
				 _existFormatProperty = haveFormatProperty(subEntity,tmpHandleEntity)>
		<#if subEntity_index=0>
		String childJsonData = para.get("${subEntityNameLower}List");
		ObjectMapper childMapper = new ObjectMapper();
		<#else>
		childJsonData = para.get("${subEntityNameLower}List");
		</#if>
		if(childJsonData!=null){
			List<Map<String,String>> list = JsonUtil.parseArrayOrSingleRequestData(childMapper,childJsonData);
			List<${subEntityName}> ${subEntityNameLower}List  = new ArrayList<${subEntityName}>();
			<#if _existFormatProperty="true">
			Map<String,String> tmpPropertyNameFormat = new HashMap<String, String>();
			<#list subEntity.properties as property>
				<#if tmpHandleEntity.dataItems??>
					<#list tmpHandleEntity.dataItems as dataItem>
						<#if property.name=dataItem[0]>
							<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
							</#if>
						</#if>
					</#list>
				<#else>
					<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
					</#if>
				</#if>
			</#list>
			</#if>
			for(Map<String, String> childAttri : list) {
				if(childAttri.size()==0) continue;
				<#if _existFormatProperty="true">
				${subEntityName} ${subEntityNameLower} = BeanUtil.bindBean(${subEntityName}.class,entityAttri,null, propertyNameFormat);
				<#else>
				${subEntityName} ${subEntityNameLower} = BeanUtil.bindBean(${subEntityName}.class,entityAttri,null,null);
				</#if>
				${subEntityNameLower}List.add(${subEntityNameLower});
			}
			if(${subEntityNameLower}List.size()>0){
				${entityNameLower}.set${subEntityName}List(${subEntityNameLower}List);
			}
		}
		</#list>
		return ${entityNameLower};
	}
	//读取前台json数据并且解析进行数据绑定
	private ${entityName} bindJsonDataFor${_methodName?cap_first}(String jsonData){
		<#assign entityNames=collectParseJsonChildEntityNames(methodEntityMap[metaMethod.name+"aboutEntityList"],methodEntityMap[metaMethod.name+"subEntityList"])>
		//装配当前对象
		<#if methodEntityMap[metaMethod.name+"aboutEntityList"]?size=0 && methodEntityMap[metaMethod.name+"subEntityList"]?size=0>
		Map<String,String> entityAttri = JsonUtil.parseCommonJsonData(jsonData,null);
		<#else>
		Map<String,Object> childData = new  HashMap<String,Object>();
		String[] includeChilds = new String[]{${entityNames}};
		Map<String,String> entityAttri = JsonUtil.parseCommonJsonData(jsonData,childData,includeChilds);
		</#if>
		if(entityAttri.size()==0) return null;
		<#assign tmpHandleEntity = haveHandleEntityByEntityName(methodEntityMap[metaMethod.name+"nonceEntity"].name,metaMethod.handleEntities),
					_existFormatProperty = haveFormatProperty(methodEntityMap[metaMethod.name+"nonceEntity"],tmpHandleEntity)>
		<#if _existFormatProperty="true">
		Map<String,String> propertyNameFormat = new HashMap<String, String>();
		<#list methodEntityMap[metaMethod.name+"nonceEntity"].properties as property>
			<#if tmpHandleEntity.dataItems??>
				<#list tmpHandleEntity.dataItems as dataItem>
					<#if property.name=dataItem[0]>
						<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
		propertyNameFormat.put("${property.name}","${property.toStringformat}");
						</#if>
					</#if>
				</#list>
			<#else>
				<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
		propertyNameFormat.put("${property.name}","${property.toStringformat}");
				</#if>
			</#if>
		</#list>
		${entityName} ${entityNameLower} = BeanUtil.bindBean(${entityName}.class,entityAttri,null, propertyNameFormat);
		<#else>
		${entityName} ${entityNameLower} = BeanUtil.bindBean(${entityName}.class,entityAttri,null,null);
		</#if>
		//装配相关对象
		<#list methodEntityMap[metaMethod.name+"aboutEntityList"] as aboutEntity>
		<#assign aboutEntityName = aboutEntity.name,
				 aboutEntityNameLower = aboutEntityName?uncap_first,
				 tmpHandleEntity = haveHandleEntityByEntityName(aboutEntity.name,metaMethod.handleEntities),
				 _existFormatProperty = haveFormatProperty(aboutEntity,tmpHandleEntity)>
		entityAttri = (Map<String,String>) childData.get("${aboutEntityNameLower}");
		if(entityAttri !=null && entityAttri.size()>0){
			<#if _existFormatProperty="true">
			Map<String,String> tmpPropertyNameFormat = new HashMap<String, String>();
			<#list aboutEntity.properties as property>
				<#if tmpHandleEntity.dataItems??>
					<#list tmpHandleEntity.dataItems as dataItem>
						<#if property.name=dataItem[0]>
							<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
							</#if>
						</#if>
					</#list>
				<#else>
					<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
					</#if>
				</#if>
			</#list>
			${aboutEntityName} ${aboutEntityNameLower} = BeanUtil.bindBean(${aboutEntityName}.class,entityAttri,null, tmpPropertyNameFormat);
			<#else>
			${aboutEntityName} ${aboutEntityNameLower} = BeanUtil.bindBean(${aboutEntityName}.class,entityAttri,null,null);
			</#if>
			${entityNameLower}.set${aboutEntityName}(${aboutEntityNameLower});
		 }
		</#list>
		//装配子对象
		<#list methodEntityMap[metaMethod.name+"subEntityList"] as subEntity>
		<#assign subEntityName = subEntity.name,
				 subEntityNameLower = subEntityName?uncap_first,
				 tmpHandleEntity = haveHandleEntityByEntityName(subEntity.name,metaMethod.handleEntities),
				 _existFormatProperty = haveFormatProperty(subEntity,tmpHandleEntity)>
		<#if subEntity_index=0>
		List<Map<String,String>> list = (List<Map<String,String>>)childData.get("${subEntityNameLower}List");
		<#else>
		list = childData.get("${subEntityNameLower}List");
		</#if>
		if(list!=null){
			List<${subEntityName}> ${subEntityNameLower}List  = new ArrayList<${subEntityName}>();
			<#if _existFormatProperty="true">
			Map<String,String> tmpPropertyNameFormat = new HashMap<String, String>();
			<#list subEntity.properties as property>
				<#if tmpHandleEntity.dataItems??>
					<#list tmpHandleEntity.dataItems as dataItem>
						<#if property.name=dataItem[0]>
							<#if property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp">
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
							</#if>
						</#if>
					</#list>
				<#else>
					<#if property.using && (property.type="java.util.Date" || property.type="java.sql.Date" || property.type="java.sql.Timestamp")>
			tmpPropertyNameFormat.put("${property.name}","${property.toStringformat}");
					</#if>
				</#if>
			</#list>
			</#if>
			for(Map<String, String> childAttri : list) {
				if(childAttri.size()==0) continue;
				<#if _existFormatProperty="true">
				${subEntityName} ${subEntityNameLower} = BeanUtil.bindBean(${subEntityName}.class,entityAttri,null, tmpPropertyNameFormat);
				<#else>
				${subEntityName} ${subEntityNameLower} = BeanUtil.bindBean(${subEntityName}.class,entityAttri,null,null);
				</#if>
				${subEntityNameLower}List.add(${subEntityNameLower});
			}
			if(${subEntityNameLower}List.size()>0){
				${entityNameLower}.set${subEntityName}List(${subEntityNameLower}List);
			}
		}
		</#list>
		return ${entityNameLower};
	}
</#macro>
<#macro get_delCommonService metaMethod>
	/**
	 * ${metaMethod.nameCn}
	 */
	@RequestMapping(value="/${_methodName}.do",method = RequestMethod.POST)
	public ModelAndView ${_methodName}(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model = new ModelAndView("jackson");
		try {
			//收集提交参数
			Map<String, String> para = HttpRequestUtil.getParameter(request);
			Map<String,Object> context = new HashMap<String,Object>();
			//current user
			context.put("userInfo",prepareCurrentUser(request));
			context.put("para", para);
			Object id = ConvertUtil.convertToObject("${pkProperty.type}",para.get("id"),"");
			<#if metaMethod.type = "get">
			${entityName} ${entityNameLower} = ${metaClazz.name?uncap_first}.${_methodName}(id,context);
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(true,"${metaMethod.nameCn}成功",${entityNameLower}));
			<#elseif metaMethod.type = "delete">
			${metaClazz.name?uncap_first}.${_methodName}(id,context);
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(true,"${metaMethod.nameCn}成功",""));
			</#if>
			context.clear();
		}catch (DawnException e) {
			Object errorData = e.getErrorObject()==null ? e.toString():e.getErrorObject();
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),errorData));
			log.error("${_methodName}", e);
		}catch (Exception e) {
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
			log.error("${_methodName}", e);
		}
		return model;
	}
</#macro>
<#macro customCommonService metaMethod>
	/**
	 *  ${metaMethod.nameCn}
	 */
	@RequestMapping(value="/${_methodName}.do",method = RequestMethod.POST)
	public ModelAndView ${_methodName}(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model = new ModelAndView("jackson");
		try {
			//收集提交参数
			Map<String, String> para = HttpRequestUtil.getParameter(request);
			Map<String,Object> context = new HashMap<String,Object>();
			//current user
			context.put("userInfo",prepareCurrentUser(request));
			context.put("para", para);
			${entityName} ${entityNameLower} = null;
			//todo 
			${entityNameLower} = ${_methodName}(${entityNameLower} ,context);
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(true,"${metaMethod.nameCn}成功",${entityNameLower}));
			context.clear();
		}catch (DawnException e) {
			Object errorData = e.getErrorObject()==null ? e.toString():e.getErrorObject();
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),errorData));
			log.error("${_methodName}", e);
		}catch (Exception e) {
			model.addObject("ResponseMessage",VOUtil.createResponseMessage(false, e.getMessage(),e.toString()));
			log.error("${_methodName}", e);
		}
		return model;
	}
</#macro>
<#macro prepareCurrentUserMethod>
	/**
	 * 用户信息说明
	 * sid 会话ID
	 * userID 用户标识
	 * userName 用户名
	 * displayName 中文名
	 * organID 单位标识
	 * organName 单位名称
	 * zoneCode 所属区域
	 * employeeID 人员ID
	 * employeeCode人员编号
	 * @return 登录用户信息
	 */
	private Map<String,String> prepareCurrentUser(HttpServletRequest request){
		Map<String,String> userMap = new HashMap<String,String>();
//		String sid = request.getSession().getId();
//		LoginInfo info = LogonContext.getInstance().getLoginUser(sid);
//		userMap.put("sid",sid);
//		userMap.put("userID",info.getUid());
//		userMap.put("userName",info.getUname());
//		userMap.put("displayName",info.getDisplayName());
//		userMap.put("organID",info.getOrganID().get(0));
//		userMap.put("organName",info.getOrganName().get(0));
//		userMap.put("zoneCode",info.getZoneCode());
//		userMap.put("employeeID",info.getEmployeeID());
//		userMap.put("employeeCode",info.getEmployeeCode());
		return userMap;
	}
</#macro>