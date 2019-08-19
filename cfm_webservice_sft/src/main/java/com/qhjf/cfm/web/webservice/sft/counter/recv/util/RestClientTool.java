package com.qhjf.cfm.web.webservice.sft.counter.recv.util;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.StringKit;

public class RestClientTool {

	private static Logger log = LoggerFactory.getLogger(RestClientTool.class);

	private static final String DEFAULT_CODE = "UTF-8";

	private RestClientTool() {
	}

	public static RestClientTool getInstance() {
		return Inner.INSTANCE;
	}

	/**
	 * 
	 * @param url
	 *            请求地址
	 * @param jsonString
	 *            请求数据
	 * @return
	 * @throws ReqDataException
	 */
	public String sendPost(String url, String jsonString) throws ReqDataException {
		long currTime = System.currentTimeMillis();
		log.debug("{}:URL:{},NB柜收保单查询发送报文：{}", currTime, url, 
				StringKit.removeControlCharacter(jsonString != null ? jsonString : "请求报文为空"));
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(jsonString, DEFAULT_CODE);

		httpPost.setEntity(entity);
		httpPost.setHeader("Content-Type", "application/json;charset=utf8");

		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			log.debug("响应状态为:{}", statusLine);
			
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String result = EntityUtils.toString(responseEntity);
				log.debug("{}:NB柜收保单查询响应报文：{}", currTime,
						StringKit.removeControlCharacter(result != null ? result : "请求报文为空"));
				if (200 == statusLine.getStatusCode()) {
					return result;
				}else {
					return null;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static class Inner {
		private static final RestClientTool INSTANCE = new RestClientTool();
	}
}
