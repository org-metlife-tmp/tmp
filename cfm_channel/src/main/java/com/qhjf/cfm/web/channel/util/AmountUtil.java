package com.qhjf.cfm.web.channel.util;

import java.math.BigDecimal;

/**
 *金额处理工具
 *
 */
public class AmountUtil {
	
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	public static BigDecimal icbcAmountHandle(BigDecimal amount){
		BigDecimal result = null;
		if (null != amount) {
			result = amount.divide(ONE_HUNDRED);
		}
		return result;
	}
}
