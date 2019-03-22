package com.qhjf.cfm.web.channel;

import com.qhjf.cfm.web.channel.util.CmbcParamsUtil;

public class CmbcParamsUtilTest {
	public static void main(String[] args) {
		System.out.println(CmbcParamsUtil.TranType.getTranType("BYSU").getCTrstyp());
		System.out.println(CmbcParamsUtil.TranType.getTranType("BYSA").getCTrstyp());
	}
}
