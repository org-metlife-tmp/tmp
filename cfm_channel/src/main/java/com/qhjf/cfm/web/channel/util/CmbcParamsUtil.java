package com.qhjf.cfm.web.channel.util;

/**
 * 交易类型转换工具
 * 
 * @author CHT
 *
 */
public interface CmbcParamsUtil {
	/**
	 * N03010: 代发工资	
	 * N03020: 代发	   
	 * 
	 * N03030: 代扣
	 * @author CHT
	 *
	 */
	public enum TranType {
		BYSU("N03020", "代发保险费", "BYSU"),
		BYTF("N03020", "代发报销款", "BYTF"),
		BYSA("N03010", "代发工资", "BYSA"),
		
		
		AYIS("N03030", "代扣保险费", "AYIS"),
		AYBK("N03030", "代扣其他", "AYBK");
		
		String busCod;
		String cTrstyp;
		String trstyp;

		TranType(String busCod, String cTrstyp, String trstyp) {
			this.busCod = busCod;
			this.cTrstyp = cTrstyp;
			this.trstyp = trstyp;
		}

		public String getBusCod() {
			return this.busCod;
		}

		public String getCTrstyp() {
			return this.cTrstyp;
		}

		public String getTrstyp() {
			return this.trstyp;
		}
		/**
		 * 获取交易类型
		 * @param tranType	交易类型字符串
		 * @return
		 */
		public static TranType getTranType(String tranType){
			TranType result = null;
			TranType[] values = TranType.values();
			for (TranType v : values) {
				if (v.name().equals(tranType)) {
					result = v;
				}
			}
			
			return result;
		}
		
	}
	
}
