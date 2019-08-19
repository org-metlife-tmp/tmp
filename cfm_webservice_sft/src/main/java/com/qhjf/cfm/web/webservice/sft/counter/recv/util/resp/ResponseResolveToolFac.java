package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

public class ResponseResolveToolFac {

	private ResponseResolveToolFac(){}
	public static ResponseResolveToolFac getInstance(){
		return ResponseResolveToolFacInner.INSTANCE;
	}
	private static class ResponseResolveToolFacInner{
		private static final ResponseResolveToolFac INSTANCE = new ResponseResolveToolFac();
	}
	
	public ResponseResolveTool getRespResolveTool(SourceSysType type){
		ResponseResolveTool tool = null;
		switch(type){
			case LA_INSURE_QRY:
				tool = LaCounterRecvRespResolveTool.getInstance();
				break;
			case NB_INSURE_QRY:
				tool = NbCounterRecvRespResolveTool.getInstance();
				break;
			case LA_INSURE_COMFIRM:
				tool = LaCounterRecvComfirmRespResolveTool.getInstance();
				break;
			case LA_INSURE_CANCEL:
				tool = LaCounterRecvCancelRespResolveTool.getInstance();
				break;
			case EBS_CUSTOMER_ACC_QRY:
				tool = EbsCustomerAccQryRespResolveTool.getInstance();
				break;
			case EBS_BIZ_PAY_QRY:
				tool = EbsBizPayQryRespResolveTool.getInstance();
				break;
			case EBS_CUSTOMER_ACC_CONFIRM:
				tool = EbsCustomerAccConfirmRespResolveTool.getInstance();
				break;
			case EBS_CUSTOMER_ACC_CANCEL:
				tool = EbsCustomerAccCancelRespResolveTool.getInstance();
				break;
			case EBS_BIZ_PAY_CONFIRM:
				tool = EbsBizPayConfirmRespResolveTool.getInstance();
				break;
			case EBS_ADVANCE_RECEIPT_STATUS_QRY:
				tool = EbsAdvanceReceiptStatusQryRespResolveTool.getInstance();
				break;
		}
		return tool;
	}
	
	public enum SourceSysType{
		LA_INSURE_QRY("la保单查询"),
		NB_INSURE_QRY("nb保单查询"),
		LA_INSURE_COMFIRM("la保单确认"),
		LA_INSURE_CANCEL("la保单取消"),
		EBS_CUSTOMER_ACC_QRY("ebs客户账户查询"),
		EBS_BIZ_PAY_QRY("ebs收款业务查询"),
		EBS_CUSTOMER_ACC_CONFIRM("ebs客户账户确认"),
		EBS_CUSTOMER_ACC_CANCEL("ebs客户账户撤销"),
		EBS_BIZ_PAY_CONFIRM("ebs收款业务确认"),
		EBS_ADVANCE_RECEIPT_STATUS_QRY("预收ebs状态查询接口");
		private String desc;
		SourceSysType(String desc){
			this.desc = desc;
		}
		public String getDesc(){
			return this.desc;
		}
	}
}
