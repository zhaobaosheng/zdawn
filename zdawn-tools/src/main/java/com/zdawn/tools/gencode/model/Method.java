package com.zdawn.tools.gencode.model;

import java.util.ArrayList;
import java.util.List;

public class Method {
		private String name;
		private String nameCn;
		private String type;
		private boolean genServiceClazz;
		private List<ValidateRule> validators = new ArrayList<ValidateRule>();
		private List<HandleEntity>  handleEntities = new ArrayList<HandleEntity>();
		private boolean usingClazz;
		
		public List<HandleEntity> getHandleEntities() {
			return handleEntities;
		}
		public void setHandleEntities(List<HandleEntity> handleEntities) {
			this.handleEntities = handleEntities;
		}
		public void addHandleEntity(HandleEntity handleEntity){
			if(handleEntities==null) handleEntities = new ArrayList<HandleEntity>();
			handleEntities.add(handleEntity);
		}
		public List<ValidateRule> getValidators() {
			return validators;
		}
		public void setValidators(List<ValidateRule> validators) {
			this.validators = validators;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getNameCn() {
			return nameCn;
		}
		public void setNameCn(String nameCn) {
			if(nameCn != null)
			{
			this.nameCn = nameCn;
			}
			else 
			this.nameCn ="";
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public boolean isGenServiceClazz() {
			return genServiceClazz;
		}
		public void setGenServiceClazz(boolean genServiceClazz) {
			this.genServiceClazz = genServiceClazz;
		}
		public boolean isUsingClazz() {
			return usingClazz;
		}
		public void setUsingClazz(boolean usingClazz) {
			this.usingClazz = usingClazz;
		}
}
