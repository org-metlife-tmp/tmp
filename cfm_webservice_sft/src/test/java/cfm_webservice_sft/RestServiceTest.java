package cfm_webservice_sft;

import org.junit.Test;

import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.nb.NbInsureBillQryBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.util.RestClientTool;

/**
 * NB保单查询测试
 * @author CHT
 *
 */
public class RestServiceTest {

	private static final String url = "http://10.164.27.43:7083/uwca-service/contrec/policyInfo";
	
	@Test
	public void insureBillQryTest() throws ReqDataException{
		NbInsureBillQryBean bean = new NbInsureBillQryBean("50001402");
		RestClientTool tool = RestClientTool.getInstance();
		String result = tool.sendPost(url, bean.getJsonStr());
		System.out.println(result);
	}
}
