package cn.metlife.ebs_sit.services.FundingPlatformPayBack;

import java.rmi.RemoteException;

public class Test {
	
	public static void main(String[] args) throws RemoteException{
		String wsdl = "http://ebs-sit.metlife.cn/services/FundingPlatformPayBack?wsdl";
		FundingPlatformPayBackProxy service = new FundingPlatformPayBackProxy(wsdl);
		String xml = "<?xml version='1.0' encoding='UTF-8'?>"+
	"<Root>"+
		"<Policy>"+
		"<PayNo>2019000000305032</PayNo>"+
		"<PayDate>2019-01-18</PayDate>"+
		"<PayTime>14:24:21</PayTime>"+
		"<PayResult>SC</PayResult>"+
		"<ErrCol>1</ErrCol>"+
		"<ErrCode>1</ErrCode>"+
		"<ErrMsg>1</ErrMsg>"+
		"<PayMoney>13700.00</PayMoney>"+
		"<PayBankCode>41</PayBankCode>"+
		"<PayBankAccNo>54416464161321346546</PayBankAccNo>"+
		"</Policy>"+
	"</Root>";
		
		String response = service.saveXML(xml);
		System.out.println(response);
	}
	
	
	

}
