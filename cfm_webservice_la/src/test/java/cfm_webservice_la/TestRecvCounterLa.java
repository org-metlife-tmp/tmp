package cfm_webservice_la;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.webservice.la.LaCallbackBean;
import com.qhjf.cfm.web.webservice.la.eai.tool.LaSendTool;
import com.qhjf.cfm.web.webservice.la.util.LaPayOMElementSkipEAIUtil2;

/**
 * LA柜面收测试
 * @author CHT
 *
 */
public class TestRecvCounterLa {
//	public static final String SIT = "http://10.164.26.43/esbwebentry/ESBWebEntry.asmx?wsdl";
	
	public List<Record> getTestData(){
		List<Record> data = new ArrayList<>();
		Record origin = new Record();
		origin.set("CHDRCOY", "8");
		origin.set("CHDRNUM", "02593619");
		origin.set("EFFDATE", "20190509");
		data.add(origin);
		return data;
	}
	@Test
	public void testCase() throws ReqDataException {

		LaSendTool tool = new LaSendTool(getTestData(), "POLService", "SH", "1");
		OMElement rs = tool.sendEAI();
		JSONObject json = null;
		try {
			json = XmlTool.documentToJSONObject(rs.toString());
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
