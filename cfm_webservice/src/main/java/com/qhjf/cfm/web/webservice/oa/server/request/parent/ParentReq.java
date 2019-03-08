package com.qhjf.cfm.web.webservice.oa.server.request.parent;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.webservice.ann.FieldValidate;
import com.qhjf.cfm.web.webservice.oa.constant.ErrorCode;
import com.qhjf.cfm.web.webservice.oa.exception.WebServiceException;
import com.qhjf.cfm.web.webservice.tool.OminiUtils;
import com.qhjf.cfm.web.webservice.tool.XmlTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ParentReq{
	
	private static Logger log = LoggerFactory.getLogger(ParentReq.class);
	
	private JSONObject json;
	public ParentReq(String param) throws Exception {
		log.debug("参数={}", StringKit.removeControlCharacter(param));
		try {
			this.json = XmlTool.documentToJSONObject(param);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebServiceException(ErrorCode.P0001);
		}
		
		try{
			Map<String,Object> map = JSONObject.parseObject(json.toJSONString(), Map.class);
			initSet(this,map);
		} catch(Exception e){
			e.printStackTrace();
			String errmsg = null;
			if(e.getMessage() == null || e.getMessage().length() >1000){
				errmsg = "系统异常";
			}else{
				errmsg = e.getMessage();
			}
			throw new WebServiceException(ErrorCode.P0099,errmsg);
		}
		
	}
	public JSONObject getJson() {
		return json;
	}

    /**
     * 初始化值
     * @param obj
     * @param map
     * @param <T>
     * @throws Exception
     */
    public  <T extends ParentReq> void initSet(T obj, Map<String, Object> map) throws Exception {
        if (!OminiUtils.isNullOrEmpty(map)) {
        	Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                FieldValidate dv = field.getAnnotation(FieldValidate.class);
                validateAndSetField(obj, field, dv, map.get(field.getName()));
            }


        } else {
            throw new Exception("初始化参数为空！");
        }
    }


    private  <T extends ParentReq> void validateAndSetField(T obj, Field field, FieldValidate dv, Object value) throws Exception {
        if (dv != null && dv.setable()) {
            if (!dv.nullable()) {
                if (OminiUtils.isNullOrEmpty(value)) {
                    throw new Exception(dv.description()+"不能为空");
                }

            }
            if (value != null && value.toString().length() > dv.maxLength() && dv.maxLength() != 0) {
                throw new Exception(dv.description() + "长度不能超过" + dv.maxLength());
            }
            if (value != null && value.toString().length() < dv.minLength() && dv.minLength() != 0) {
                throw new Exception(dv.description() + "长度不能小于" + dv.minLength());
            }

            Method setMothod = obj.getSetMothod(field);
            Object destValue = null;
            if (field.getType().equals(String.class)) {
                	destValue = value;
            } else if (field.getType().equals(List.class)) {
                if (value != null) {
                    List<Map<String, Object>> initParams = null;
                    if (List.class.isAssignableFrom(value.getClass())) {
                        initParams = (List<Map<String, Object>>) value;
                    } else {
                        throw new Exception(dv.description()  + "初始值类型不正确！");
                    }
                    List dest = new ArrayList();
                    if (ParentReq.class.isAssignableFrom(dv.nestClass())) {
                        for (Map<String, Object> param : initParams) {
                        	ParentReq instance = null;
                            try {
                                instance = (ParentReq) dv.nestClass().newInstance();
                            } catch (Exception e) {
                                throw new Exception("初始化请求对象失败:" + e.getMessage());
                            }
                            initSet(instance, param);
                            dest.add(instance);
                        }
                        destValue = dest;
                    } else {
                        throw new Exception(obj.getClass().getName() + " " + dv.description() + ", [" + field.getName() + "]初始值类型不正确！");
                    }
                }
            } else if (ParentReq.class.isAssignableFrom(field.getType())) {
            	ParentReq instance = null;
                try {
                    instance = (ParentReq) field.getType().newInstance();
                    initSet(instance, (Map<String, Object>) value);
                    destValue = instance;
                } catch (Exception e) {
                    throw new Exception(field.getName() + "无法初始化值:" + e.getMessage());
                }

            }

            try {
                if (!OminiUtils.isNullOrEmpty(destValue)) {
                    setMothod.invoke(obj, destValue);
                }
            } catch (Exception e) {
                throw new Exception("初始化请求对象失败:" + e.getMessage());
            }
 
        }
    }

    /**
     * 根据field对象u获取set方法
     * @param field
     * @return
     * @throws Exception
     */
    protected Method getSetMothod(Field field) throws Exception {
        String fieldName = field.getName();

        String methodName = new StringBuffer("set").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
        try {
            return this.getClass().getMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            throw new Exception("未找到[" + fieldName + "]的setMethod");
        }
    }
	

}
