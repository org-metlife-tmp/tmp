package com.qhjf.cfm.web.webservice.oa.server;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.oa.constant.ErrorCode;
import com.qhjf.cfm.web.webservice.oa.constant.HandleStatus;
import com.qhjf.cfm.web.webservice.oa.exception.WebServiceException;
import com.qhjf.cfm.web.webservice.oa.server.processQueue.ProcessQueueBean;
import com.qhjf.cfm.web.webservice.oa.server.processQueue.ProductQueue;
import com.qhjf.cfm.web.webservice.oa.server.request.OAQueryStatusReq;
import com.qhjf.cfm.web.webservice.oa.server.request.OAReciveDateReq;
import com.qhjf.cfm.web.webservice.oa.server.response.OAQueryStatusResp;
import com.qhjf.cfm.web.webservice.oa.server.response.OAReciveDataResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class OAServer implements IOAServer{
	
	private static Logger log = LoggerFactory.getLogger(OAServer.class);

	@Override
	public String reciveDate(String xml) {
		OAReciveDateReq req = null;
		OAReciveDataResp resp = null;
		try {
			req = new OAReciveDateReq(xml);
			Record originData = saveOriginData(req);
			ProcessQueueBean bean = new ProcessQueueBean(req,originData);
			ProductQueue productQueue = new ProductQueue(bean);
			new Thread(productQueue).start();
			resp = new OAReciveDataResp(req.getJson(),HandleStatus.OA_INTER_RECV_SUCCESS.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			WebServiceException exception = null;
			if(e instanceof WebServiceException){
				exception = (WebServiceException) e;
			}else{
				exception = new WebServiceException(ErrorCode.P0099);
			}
			
			resp = new OAReciveDataResp(req==null?null:req.getJson(),HandleStatus.OA_INTER_RECV_FAIL.getKey(),exception.getErrorCode().toString(),exception.getMessage());
		}
		String result  = resp.toXmlString();
		log.debug("返回result="+result);
		return resp.toXmlString();
	}
	
	@Override
	public String queryStatus(String xml) {
		OAQueryStatusReq req = null;
		OAQueryStatusResp resp = null;
		try {
			req = new OAQueryStatusReq(xml);
			Record originDataRecord = Db.findFirst(Db.getSql("oa_interface.getOiriginInterfaceStatus"),req.getFlow_id(),req.getSend_count());
			
			if(originDataRecord == null){
				resp = new OAQueryStatusResp(req==null?null:req.getJson(),HandleStatus.OA_INTER_RECV_SUCCESS.getKey(),null,WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey(),ErrorCode.P0007.toString(),ErrorCode.P0007.getDesc());
			}else{
				resp = new OAQueryStatusResp(req==null?null:req.getJson(),HandleStatus.OA_INTER_RECV_SUCCESS.getKey(),null,originDataRecord.getInt("interface_status"),originDataRecord.getStr("interface_fb_code"),originDataRecord.getStr("interface_fb_msg"));
			}
			
		} catch (Exception e) {
			resp = new OAQueryStatusResp(req.getJson(),HandleStatus.OA_INTER_RECV_FAIL.getKey(),e.getMessage(),null,null,null);
			e.printStackTrace();
		}
		String result  = resp.toXmlString();
		log.debug("返回result="+result);
		return resp.toXmlString();
	}
	
	
	
	
	
	private Record saveOriginData(OAReciveDateReq req) throws Exception{
		Record originRecord = new Record();
		originRecord.set("flow_id", req.getFlow_id());
		originRecord.set("bill_no", req.getBill_no());
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
		originRecord.set("interface_status", WebConstant.OaInterfaceStatus.OA_INTER_RECV_SUCCESS.getKey());

		boolean saveOriginFlg = Db.save("oa_origin_data", originRecord);
		if(!saveOriginFlg){
			throw new Exception("数据接收失败");
		}
		return originRecord;
	}
	public static void main(String[] args) {
		         
         //定义WebService的发布地址，这个地址就是提供给外界访问Webervice的URL地址，URL地址格式为：http://ip:端口号/xxxx
         String address = "http://127.0.0.1:8090/oa";
         //使用Endpoint类提供的publish方法发布WebService，发布时要保证使用的端口号没有被其他应用程序占用
         Endpoint.publish(address, new OAServer());
         System.out.println("发布webservice成功！");
		         
   }

}
