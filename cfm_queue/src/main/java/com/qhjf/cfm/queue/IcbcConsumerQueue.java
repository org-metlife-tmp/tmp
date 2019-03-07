package com.qhjf.cfm.queue;

import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.cfm.queue.patch.IcbcTransQueryProcessPatch;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;

public class IcbcConsumerQueue implements Runnable{

//	private static ICBCTestConfigSection configSection = ICBCTestConfigSection.getInstance();

	@Override
	public void run() {
		while(true){
			QueueBean queueBean = null;
			ISysAtomicInterface sysInter = null;
			try {
				queueBean = Queue.getInstance().getIcbcQueue().take();
				sysInter = queueBean.getSysInter();

				/*如果sysInter.getChannelInter() == IcbcCurrTransQueryInter || IcbcHisTransQueryInter
				 *	则走特定的分页查询逻辑
				 */
				String jsonStr = null;
				IChannelInter channelInter = sysInter.getChannelInter();

				if ("IcbcElectronicImgQueryInter".equals(channelInter.getClass().getSimpleName())) {//工行不支持下载电子回单图片

				}else if ("IcbcCurrTransQueryInter".equals(channelInter.getClass().getSimpleName())
						|| "IcbcHisTransQueryInter".equals(channelInter.getClass().getSimpleName())) {
					jsonStr = IcbcTransQueryProcessPatch.process(channelInter.getInter(), queueBean.getParams());
				}else {
					/*Map<String, Object> params = queueBean.getParams();
					//如果为支付指令，则加签名时间
					if ("IcbcSinglePayInter".equals(channelInter.getClass().getSimpleName())) {
						params.put("SignTime", getSpecifiedDayBefore(new Date(), configSection.getPreDay(), "yyyyMMddHHmmssSSS"));
					}*/
					jsonStr = ProcessEntrance.getInstance().process(channelInter.getInter(), queueBean.getParams());
				}

//				String jsonStr = ProcessEntrance.getInstance().process(sysInter.getChannelInter().getInter(), queueBean.getParams());
				sysInter.callBack(jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					sysInter.callBack(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}

	}

/*	private static String getSpecifiedDayBefore(Date date, int dayNum, String format) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - dayNum);

		String dayBefore = new SimpleDateFormat(format).format(c.getTime());
		return dayBefore;
	}*/

}
