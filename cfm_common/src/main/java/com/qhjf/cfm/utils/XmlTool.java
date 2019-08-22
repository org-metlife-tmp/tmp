package com.qhjf.cfm.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class XmlTool {

	/**
	 * String 转 org.dom4j.Document
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static Document strToDocument(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml);
	}

	/**
	 * org.dom4j.Document 转 com.alibaba.fastjson.JSONObject
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static JSONObject documentToJSONObject(String xml) throws DocumentException {
		return elementToJSONObject(strToDocument(xml).getRootElement());
	}

	/**
	 * org.dom4j.Element 转 com.alibaba.fastjson.JSONObject
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject elementToJSONObject(Element node) {
		JSONObject result = new JSONObject(); // 当前节点的名称、文本内容和属性
		List<Attribute> listAttr = node.attributes();// 当前节点的所有属性的list
		for (Attribute attr : listAttr) {// 遍历当前节点的所有属性
			result.put(attr.getName(), attr.getValue());
		}
		// 递归遍历当前节点所有的子节点
		List<Element> listElement = node.elements();// 所有一级子节点的list
		if (!listElement.isEmpty()) {
			for (Element e : listElement) {// 遍历所有一级子节点
				if (e.attributes().isEmpty() && e.elements().isEmpty()) // 判断一级节点是否有属性和子节点
					result.put(e.getName(), e.getTextTrim());// 沒有则将当前节点作为上级节点的属性对待
				else {
					if (!result.containsKey(e.getName())) // 判断父节点是否存在该一级节点名称的属性
						result.put(e.getName(), new JSONArray());// 没有则创建
					((JSONArray) result.get(e.getName())).add(elementToJSONObject(e));// 将该一级节点放入该节点名称的属性对应的值中
				}
			}
		}
		return result;
	}
	
	public static String format(String xmlStr) throws Exception {
		SAXReader reader = new SAXReader();  
	    Document document = reader.read(new StringReader(xmlStr));  
	    String requestXML = null;  
	    XMLWriter writer = null;  
	    if (document != null) {  
	      try {  
	        StringWriter stringWriter = new StringWriter();  
	        OutputFormat format = new OutputFormat(" ", true);  
	        writer = new XMLWriter(stringWriter, format);  
	        writer.write(document);  
	        writer.flush();  
	        requestXML = stringWriter.getBuffer().toString();  
	      } finally {  
	        if (writer != null) {  
	          try {  
	            writer.close();  
	          } catch (IOException e) {  
	          }  
	        }  
	      }  
	    }  
	    return requestXML;  
    }
	
	public static String transferSpecialChar(String xmlStr){
		if (null == xmlStr) {
			return xmlStr;
		}
		if (xmlStr.contains("&")){
			xmlStr = xmlStr.replaceAll("&", "&amp;");
		}
		if (xmlStr.contains("<")){
			xmlStr = xmlStr.replaceAll("<", "&lt;");
		}
		if (xmlStr.contains(">")){
			xmlStr = xmlStr.replaceAll(">", "&gt;");
		}
		if (xmlStr.contains("\"")){
			xmlStr = xmlStr.replaceAll("\"", "&quot;");
		}
		if (xmlStr.contains("\'")){
			xmlStr = xmlStr.replaceAll("\'", "&apos;");
		}
		return xmlStr;
	}
	
	public static void main(String[] args) {
		System.out.println(transferSpecialChar("&123&"));
		System.out.println(transferSpecialChar("<123<"));
		System.out.println(transferSpecialChar(">123>"));
		System.out.println(transferSpecialChar("'123'"));
		System.out.println(transferSpecialChar("\"123\""));
		System.out.println(transferSpecialChar("123"));
		System.out.println(transferSpecialChar(null));
	}

}
