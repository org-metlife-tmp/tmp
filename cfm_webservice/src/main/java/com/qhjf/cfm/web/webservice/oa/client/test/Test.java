package com.qhjf.cfm.web.webservice.oa.client.test;

import com.qhjf.cfm.web.webservice.oa.client.OAServer;
import com.qhjf.cfm.web.webservice.oa.client.OAServerService;

public class Test {
	
	public static void main(String[] args){
		TranTest();
		queryStatusTest();
	}
	
	/**
	 * 发送交易测试
	 */
    public static void TranTest(){

		//创建一个用于产生WebServiceImpl实例的工厂，WebServiceImplService类是wsimport工具生成的
		OAServerService factory = new OAServerService();
		//通过工厂生成一个WebServiceImpl实例，WebServiceImpl是wsimport工具生成的
		OAServer oaServer = factory.getOAServerPort();
		String xml = "<oa>"
				+"<flow_id>0000000001</flow_id>"
				+"<bill_no>1000000001</bill_no>"
				+"<biz_type>1</biz_type>"
				+"<apply_user>lala</apply_user>"
				+"<budget_user>0005</budget_user>"
				+"<apply_date>2018-08-20</apply_date>"
				+"<recv_accno> 6226690200028929 </recv_accno>"
				+"<recv_accname>张三</recv_accname>"
				+"<recv_bank>中国工商银行股份有限公司北京市分行营业部</recv_bank>"
				+"<recv_province>北京</recv_province>"
				+"<recv_city>朝阳</recv_city>"
				+"<amount>100.00</amount>"
				+"<send_count>1</send_count>"
				+"<memo>发送报文示例</memo>"
				+"<apply_org>gdym00001</apply_org>"
				+"<apply_dept>dept01</apply_dept>"
				+"<pay_org_type>1</pay_org_type>"
				+"</oa>";
		System.out.println(oaServer.reciveDate(xml));

    }
	
    /**
     * 交易状态查询测试
     */
    public static void queryStatusTest(){
    	//创建一个用于产生WebServiceImpl实例的工厂，WebServiceImplService类是wsimport工具生成的
		OAServerService factory = new OAServerService();
		//通过工厂生成一个WebServiceImpl实例，WebServiceImpl是wsimport工具生成的
		OAServer oaServer = factory.getOAServerPort();
		String xml = "<oa>"
				+"<flow_id>0000000001</flow_id>"
				+"<send_count>1</send_count>"
				+"</oa>";

		System.out.println(oaServer.queryStatus(xml));
	}

}
