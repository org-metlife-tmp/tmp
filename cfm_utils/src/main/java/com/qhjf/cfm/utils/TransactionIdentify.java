package com.qhjf.cfm.utils;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * "交易唯一标识"生成器,没有的字段传null。
 * 账号ID:acctID|交易金额：amount|direction:收付方向1：付、2：收 |对方账户号:oppAcctno|对方账户名称:oppAcctName|交易时间:tradeTime
 * |摘要:summary|用途:useCN|附言:postScript
 * @author CHT
 *
 */
public class TransactionIdentify {
	/**
	 * 账号ID
	 */
	private Long acctID;
	/**
	 * 交易金额
	 */
	private BigDecimal amount;
	/**
	 * 收付方向1：付、2：收 
	 */
	private Integer direction;
	/**
	 * 对方账户号
	 */
	private String oppAcctno;
	/**
	 * 对方账户名称
	 */
	private String oppAcctName;
	/**
	 * 交易日期与交易时间的字符串，格式：yyyy-MM-dd HH:mm:ss
	 */
	private String tradeTime;
	/**
	 * 摘要
	 */
	private String summary;
	/**
	 * 用途
	 */
	private String useCN;
	/**
	 * 附言
	 */
	private String postScript;

	public TransactionIdentify(Long acctID, BigDecimal amount, Integer direction, String oppAcctno, String oppAcctName,
			String tradeTime, String summary, String useCN, String postScript) {
		this.acctID = acctID;
		this.amount = amount;
		this.direction = direction;
		this.oppAcctName = oppAcctName;
		this.oppAcctno = oppAcctno;
		this.tradeTime = tradeTime;
		this.summary = summary;
		this.useCN = useCN;
		this.postScript = postScript;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(this.getIdentify()).append("[").append(genDetail()).append("]");
		return result.toString();
	}

	private String genDetail() {
		StringBuffer result = new StringBuffer();
		result.append("acctID:").append(this.acctID).append(",amount:").append(this.amount).append(",direction:")
				.append(this.direction).append(",oppAcctno:").append(this.oppAcctno).append(",oppAcctName:")
				.append(this.oppAcctName).append(",tradeTime:").append(this.tradeTime).append(",summary:")
				.append(this.summary).append(",useCN:").append(this.useCN).append(",postScript:")
				.append(this.postScript);
		return result.toString();
	}

	public String getIdentify() {
		return UUID.nameUUIDFromBytes(genDetail().getBytes()).toString();
	}
}
