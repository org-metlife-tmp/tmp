package cfm_webservice_sft;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillCancelReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillComfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillCancelRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillQryRespBean;

/**
 * la 柜面收 保单查询测试
 * @author CHT
 *
 */
public class LaCounterRecvTest {

	/**
	 * 个单保单查询
	 */
	@Test
	public void insureBillQry(){
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		PersonBillQryReqBean bean = new PersonBillQryReqBean("50001402");
		try {
			PersonBillQryRespBean rs = call.qryBillByInsureBillNo(bean);
			System.out.println(rs);
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 个单保单确认
	 */
	/**
	@Test
	public void insureBillConfirm(){
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		List<PersonBillComfirmReqBean> list = new ArrayList<>();
		PersonBillComfirmReqBean bean1 = new PersonBillComfirmReqBean("199","1001190709023101878","中美联泰大都会人寿保险有限公司","CCB_YL","47213614","D","31");
		list.add(bean1);
		PersonBillComfirmReqBean bean2 = new PersonBillComfirmReqBean("199","591902896710704","银企直连专用账户9","CCB_YL","47213615","D","31");
		list.add(bean2);
		
		try {
			List<PersonBillConfirmRespBean> rs = call.personConfirmBill(list);
			System.out.println(rs);
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}
	*/
	/**
	 * 个单保单撤销
	 */
	@Test
	public void insureBillCancel(){
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		PersonBillCancelReqBean bean = new PersonBillCancelReqBean("123");
		
		try {
			PersonBillCancelRespBean rs = call.personCancelBill(bean);
			System.out.println(rs);
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}
}
