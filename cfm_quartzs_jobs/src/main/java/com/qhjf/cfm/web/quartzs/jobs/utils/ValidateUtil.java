package com.qhjf.cfm.web.quartzs.jobs.utils;
/**
 * 校验工具类
 * @author CHT
 *
 */
public class ValidateUtil {
	private static final String ACCNO_REGEX = "^[A-Za-z0-9-]+$";
	/**
	 * 对于客户账号，除“数字”、“字母”、“- ”以外的字符均为非法字符，由TMP拒收，返回支付失败。
	 * @param accNo
	 * @return
	 */
	public static boolean accNoValidate(String accNo){
		if (accNo.matches(ACCNO_REGEX)) {
			return true;
		}else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String[] a = {"1234253","dsf","QWE","------","123WQE"
				,"231qwe","1234--","qwe---","DSFER--","weqWQE"
				,"-1","-d","--ASD","_dsf234","是567898765",","
				,".","?","/","\\","|","]","}","[","{","!","@","#","$","%","^","&","*"};
		for (int i = 0 ; i < a.length; i++) {
			System.out.println(a[i] +"    "+accNoValidate(a[i]));
		}
	}
}
