package com.qhjf.cfm.web.channel.util;

/**
 * 交易类型转换工具
 * 
 * @author CHT
 *
 */
public interface CmbcParamsUtil {
	public enum TranType {
		DKBXF("N03030", "代扣保险费", "AYIS");
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
	}
}
