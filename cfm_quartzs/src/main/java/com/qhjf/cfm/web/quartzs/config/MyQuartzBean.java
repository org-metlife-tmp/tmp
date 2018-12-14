package com.qhjf.cfm.web.quartzs.config;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import org.quartz.Job;

import com.alibaba.fastjson.JSONObject;

public class MyQuartzBean{

	private final static Log logger = LogbackLog.getLog(MyQuartzBean.class);
	
	private Long id;
	private String name;
	private String className;
	private Class<? extends Job> jobClass;
	private String groups;
	private String cron;
	private Integer enable;
	private String param;
	private JSONObject paramJson;
	
	
	@SuppressWarnings("unchecked")
	public MyQuartzBean(Long id, String name,String className, String groups, String cron,
			Integer enable, String param) {
		super();
		this.id = id;
		this.name = name;
		this.className = className;
		this.groups = groups;
		this.cron = cron;
		this.enable = enable;
		this.param = param;
		this.paramJson = JSONObject.parseObject(param);
		if(this.paramJson == null){
			this.paramJson = new JSONObject();
		}
		if (className != null && !"".equals(className)){
			try {
				this.jobClass = (Class<? extends Job>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.error(name+"["+id+"]'s className is : "+className+" , NOT FOUND");
				e.printStackTrace();
			}
		}else{
			logger.error(name+"["+id+"]'s className is null");
		}

	}
	
	public static void main(String[] args){
		System.out.println(JSONObject.parseObject(""));
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Class<? extends Job> getJobClass() {
		return jobClass;
	}
	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public JSONObject getParamJson() {
		return paramJson;
	}
	public void setParamJson(JSONObject paramJson) {
		this.paramJson = paramJson;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((cron == null) ? 0 : cron.hashCode());
		result = prime * result + ((enable == null) ? 0 : enable.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((param == null) ? 0 : param.hashCode());
		result = prime * result + ((paramJson == null) ? 0 : paramJson.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyQuartzBean other = (MyQuartzBean) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (cron == null) {
			if (other.cron != null)
				return false;
		} else if (!cron.equals(other.cron))
			return false;
		if (enable == null) {
			if (other.enable != null)
				return false;
		} else if (!enable.equals(other.enable))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (param == null) {
			if (other.param != null)
				return false;
		} else if (!param.equals(other.param))
			return false;
		if (paramJson == null) {
			if (other.paramJson != null)
				return false;
		} else if (!paramJson.equals(other.paramJson))
			return false;
		return true;
	}
	
}
