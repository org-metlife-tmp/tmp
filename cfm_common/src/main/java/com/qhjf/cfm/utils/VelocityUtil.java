package com.qhjf.cfm.utils;

import com.jfinal.ext.kit.DateKit;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class VelocityUtil {

	private static Properties pro;
	static{
		pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	}
	
	/**
	 * 根据Velocity生成字符串
	 * @param filePath
	 * @param map
	 * @return
	 */
	public static String genVelo(String filePath,Map<String,Object> map){

        VelocityEngine ve = new VelocityEngine(pro);
        VelocityContext context = new VelocityContext();
        context.put("map",map);
        StringWriter writer=new StringWriter();
        Template template = ve.getTemplate(filePath, "UTF-8");
        template.merge(context, writer);
        return writer.toString();
	}
	
	
	/**
	 * 根据Velocity生成字符串
	 * @param filePath
	 * @param map
	 * @return
	 */
	public static String genGBKVelo(String filePath,Map<String,Object> map){

        VelocityEngine ve = new VelocityEngine(pro);
        VelocityContext context = new VelocityContext();
        context.put("map",map);
        StringWriter writer=new StringWriter();
        Template template = ve.getTemplate(filePath, "GBK");
        template.merge(context, writer);
        return writer.toString();
	}
	
	
	public static void main(String[] args){
		/*Map<String,Object> map = new HashMap<String,Object>();
		map.put("SrvDate", "aaa");
		map.put("SrvTime", "bbb");
		map.put("SrvOpName", "ccc");
		map.put("MsgID", "ddd");
		map.put("CorrID", "eee");
		map.put("branch", "fff");
		map.put("company", "ggg");
		map.put("SrvDate", "hhh");
		Map<String,Object> d1 = new Detail("123","234","345","456","567").toMap();
		Map<String,Object> d2 = new Detail("1234","2345","3456","4567","5678").toMap();
		Map<String,Object> d3 = new Detail("12345","23456","34567","45678","56789").toMap();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list.add(d1);
		list.add(d2);
		list.add(d3);
		map.put("details", list);
		System.out.println(genVelo("CallBackReq.vm",map));*/
		System.out.println(DateKit.toStr(new Date(), "yyyy-MM-dd"));
		System.out.println(DateKit.toStr(new Date(), "HH:mm:ss"));
	}
	
	static class Detail{
		private String company;
		private String branch;
		private String reqnno;
		private String stflag;
		private String txtline;
		
		public Detail(String company,String branch,String reqnno,String stflag,String txtline){
			this.company = company;
			this.branch = branch;
			this.reqnno = reqnno;
			this.stflag = stflag;
			this.txtline = txtline;
		}
		
		public Map<String,Object> toMap(){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("company", this.company);
			map.put("branch", this.branch);
			map.put("reqnno", this.reqnno);
			map.put("stflag", this.stflag);
			map.put("txtline", this.txtline);
			return map;
		}
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		public String getBranch() {
			return branch;
		}
		public void setBranch(String branch) {
			this.branch = branch;
		}
		public String getReqnno() {
			return reqnno;
		}
		public void setReqnno(String reqnno) {
			this.reqnno = reqnno;
		}
		public String getStflag() {
			return stflag;
		}
		public void setStflag(String stflag) {
			this.stflag = stflag;
		}
		public String getTxtline() {
			return txtline;
		}
		public void setTxtline(String txtline) {
			this.txtline = txtline;
		}
		
		
	}
	
}
