package com.qhjf.cfm.clients.test;

import com.qhjf.cfm.clients.MetlifeApplyPayService;
import com.qhjf.cfm.clients.MetlifeApplyPayServicePortType;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class TestClients {
	@Test
	public void test1()
	{
		//1) 使用HTTP协议方式访问服务端
		//String[] args =null;//初始参数
		//MetlifeApplyPayServicePortType_MetlifeApplyPayServiceHttpEndpoint_Client.main(args);
		
		//2) 使用HTTP11的文件描述协方式问服务端
		//String[] args =null;//初始参数
		//MetlifeApplyPayServicePortType_MetlifeApplyPayServiceHttpSoap11Endpoint_Client.main(args);
		
		//3）使用HTTP12的文件描述协方式问服务端
		//String[] args =null;//初始参数
		//MetlifeApplyPayServicePortType_MetlifeApplyPayServiceHttpSoap12Endpoint_Client.main(args);
		
		
		//自定义访问接口客户端
		String wsdl_dir = "http://oa-sit.metlife.com.cn/seeyon/services/metlifeApplyPayService?wsdl";//TODO 从配置文件中加载接口服务端的WSDL地址
		String xieyi = "SOAP11";//TODO 从配置文件中，加载服务端接口通讯支持的协议：HTTP协议；SOAP1.1版本的协议，SOAP1.2版本的协议
		URL wsdlURL = null;
		try {
			if(wsdl_dir !=null && wsdl_dir.trim().length()>0 && xieyi != null && xieyi.trim().length()>0)
			{
				wsdlURL = new URL(wsdl_dir.trim());
				//定义服务端
				QName SERVICE_NAME = new QName("www.seeyon.com", "metlifeApplyPayService");

				MetlifeApplyPayService ss = new MetlifeApplyPayService(wsdlURL, SERVICE_NAME);
				//生成客户端实例
				System.out.println("正在以"+xieyi+"协议生成客户端，对应的服务端地址为："+wsdl_dir);
				MetlifeApplyPayServicePortType port = null;
		        if("HTTP".equals(xieyi.trim().toUpperCase(Locale.ENGLISH))) {
		        	port = ss.getMetlifeApplyPayServiceHttpEndpoint();
		        }else if("SOAP11".equals(xieyi.trim().toUpperCase(Locale.ENGLISH))) {
		        	port = ss.getMetlifeApplyPayServiceHttpSoap11Endpoint();
		        }else if("SOAP12".equals(xieyi.trim().toUpperCase(Locale.ENGLISH))) {
		        	port = ss.getMetlifeApplyPayServiceHttpSoap12Endpoint();
		        }
		        BindingProvider bindingProvider = (BindingProvider) port;
		        Map<String, Object> requestContext = bindingProvider.getRequestContext();
		        requestContext.put("com.sun.xml.internal.ws.connection.timeout", 600 * 1000);
		        requestContext.put("com.sun.xml.internal.ws.request.timeout", 600 * 1000);
		        
		        //调用服务端
		        if(port != null)
		        {
			        System.out.println("通过客户端调用服务端的pushStatus服务...");
			        String _pushStatus_token = "111111";	//TODO 参数token定义
			        String _pushStatus_xmlString = "<oa></oa>";//TODO 接口报文定义
			        //调用接口服务获得响应内容
			        String _pushStatus__return = port.pushStatus(_pushStatus_token, _pushStatus_xmlString);
			        System.out.println("通过客户端调用服务端的pushStatus服务，响应报文为：" + _pushStatus__return);
		        }
			}
		} catch (MalformedURLException e) {
			// TODO 接口的容错处理
			e.printStackTrace();
		}
		
	}
}
