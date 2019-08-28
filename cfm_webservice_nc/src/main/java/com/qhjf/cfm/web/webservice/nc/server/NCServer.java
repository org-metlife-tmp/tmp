package com.qhjf.cfm.web.webservice.nc.server;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.nc.server.processQueue.ProcessQueueBean;
import com.qhjf.cfm.web.webservice.nc.server.response.NCReciveDataResp;
import com.qhjf.cfm.web.webservice.nc.constant.ErrorCode;
import com.qhjf.cfm.web.webservice.nc.constant.HandleStatus;
import com.qhjf.cfm.web.webservice.nc.exception.WebServiceException;
import com.qhjf.cfm.web.webservice.nc.server.processQueue.ProductQueue;
import com.qhjf.cfm.web.webservice.nc.server.request.NCReciveDateReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class NCServer implements NCIServer{
	
	private static Logger log = LoggerFactory.getLogger(NCServer.class);

	@Override
	public String reciveDate(String xml) {
		NCReciveDateReq req = null;
		NCReciveDataResp resp = null;
		try {
			log.info("nc接口请求："+xml);
			req = new NCReciveDateReq(xml);
			Record originData = saveOriginData(req);
			ProcessQueueBean bean = new ProcessQueueBean(req,originData);
			ProductQueue productQueue = new ProductQueue(bean);
			new Thread(productQueue).start();
			resp = new NCReciveDataResp(req.getJson(),HandleStatus.NC_INTER_RECV_SUCCESS.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			WebServiceException exception = null;
			if(e instanceof WebServiceException){
				exception = (WebServiceException) e;
			}else{
				exception = new WebServiceException(ErrorCode.P0099);
			}
			
			resp = new NCReciveDataResp(req==null?null:req.getJson(),HandleStatus.NC_INTER_RECV_FAIL.getKey(),exception.getErrorCode().toString(),exception.getMessage());
		}
		String result  = resp.toXmlString();
		log.debug("返回result="+result);
		return resp.toXmlString();
	}

	public String queryBank(String xml) {
		return null;
	}


	private Record saveOriginData(NCReciveDateReq req) throws Exception{
		Record originRecord = new Record();
		originRecord.set("flow_id", req.getFlow_id());
		originRecord.set("datasource", req.getDatasource());
		originRecord.set("outer_biz_type", req.getBiz_type());
		originRecord.set("apply_user", req.getApply_user());
		originRecord.set("budget_user", req.getBudget_user());
		originRecord.set("apply_date", req.getApply_date());
		originRecord.set("recv_acc_no", req.getRecv_accno());
		originRecord.set("recv_acc_name", req.getRecv_accname());
		originRecord.set("recv_bank", req.getRecv_bank());
		originRecord.set("recv_bank_prov", req.getRecv_province());
		originRecord.set("recv_bank_city", req.getRecv_city());
		originRecord.set("recv_bank_addr", req.getRecv_address());
		originRecord.set("recv_bank_type", req.getRecv_banktype());
		originRecord.set("recv_bank_cnaps", req.getRecv_cnaps());
		originRecord.set("amount", req.getAmount());
		originRecord.set("send_count", req.getSend_count());
		originRecord.set("cashier_process", req.getCashier_process());
		originRecord.set("cashier_process_date", req.getCashier_process_date());
		originRecord.set("memo", req.getMemo());
		originRecord.set("apply_org", req.getApply_org());
		originRecord.set("apply_dept", req.getApply_dept());
		originRecord.set("pay_org_type", req.getPay_org_type());
		originRecord.set("pay_mode", req.getPay_mode());
		originRecord.set("interface_status", WebConstant.InterfaceStatus.INTER_RECV_SUCCESS.getKey());

		boolean saveOriginFlg = Db.save("nc_origin_data", originRecord);
		if(!saveOriginFlg){
			throw new Exception("数据接收失败");
		}
		return originRecord;
	}
	public static void main(String[] args) {
		         
         //定义WebService的发布地址，这个地址就是提供给外界访问Webervice的URL地址，URL地址格式为：http://ip:端口号/xxxx
         String address = "http://127.0.0.1:8090/oa";
         //使用Endpoint类提供的publish方法发布WebService，发布时要保证使用的端口号没有被其他应用程序占用
         Endpoint.publish(address, new NCServer());
         System.out.println("发布webservice成功！");
		         
   }

}
