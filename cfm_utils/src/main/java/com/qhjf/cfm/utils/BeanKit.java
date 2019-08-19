package com.qhjf.cfm.utils;

import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体类工具
 * @author yunxuewen
 *
 */
public class BeanKit {
	
	/**
	 * 将bean转成Map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) { 
        Map<String, Object> params = new HashMap<String, Object>(0); 
        try { 
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
            for (int i = 0; i < descriptors.length; i++) { 
                String name = descriptors[i].getName(); 
                if (!"class".equals(name)) { 
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name)); 
                } 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return params; 
   }
	
	public static Map<String, Object> beanToMapData(Object obj) { 
        Map<String, Object> params = new HashMap<String, Object>(0); 
        try { 
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
            for (int i = 0; i < descriptors.length; i++) { 
                String name = descriptors[i].getName(); 
                if (!"class".equals(name)) { 
                    params.put(fieldNameToData(name), propertyUtilsBean.getNestedProperty(obj, name)); 
                } 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return params; 
   }
	
	private static String fieldNameToData(String fieldName){
		if(fieldName == null || fieldName.length()<0){
			return fieldName;
		}
		int fieldNameLength = fieldName.length();
		for(int i = 0;i<fieldNameLength;i++){
			char c = fieldName.charAt(i);
			if (Character.isUpperCase(c)){
				fieldName = fieldName.substring(0,i)+"_"+Character.toLowerCase(c)+fieldName.substring(i+1,fieldNameLength);
				fieldNameLength = fieldNameLength + 1;
			}
		}
		return fieldName;
	}
	
	public static void main(String[] args){
		System.out.println(BeanKit.fieldNameToData("serviceSerialNumber"));
	}

}
