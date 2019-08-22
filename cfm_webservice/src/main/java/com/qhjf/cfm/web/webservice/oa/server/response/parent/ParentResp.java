package com.qhjf.cfm.web.webservice.oa.server.response.parent;

import java.lang.reflect.Field;

public class ParentResp {
	
	private final String type = "oa";
	
	public String toXmlString(){
		Class<?> cls = this.getClass();
		Field[] fields = cls.getDeclaredFields();
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<").append(this.type).append(">");
		for(Field field : fields){
			String fieldName = field.getName();
			Object value = null;
			try {
				field.setAccessible(true);
				value = field.get(this);
				if(value == null){
					value = "";
				}
				field.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append("<").append(fieldName).append(">").append(value).append("</").append(fieldName).append(">");
		}
		sb.append("</").append(this.type).append(">");
		return sb.toString();
	}

}
