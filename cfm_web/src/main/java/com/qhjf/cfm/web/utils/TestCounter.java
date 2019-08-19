package com.qhjf.cfm.web.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;

public class TestCounter {
	public static void main(String[] args) {
		Record pay_batch_total = null;
		List<Record> list = null;	
		for(int i=0;i<15000;i++) 
		{
			pay_batch_total = new Record();
			pay_batch_total.set("child_batchno", CommonService.getSftSonBatchno())
			.set("master_batchno", "20190812142210_SubPKN_000001")
			.set("org_id", 35)
			.set("total_num", 1)
			.set("total_amount", "201.33")
			.set("success_num", 0)
			.set("success_amount", "201.33")
			.set("fail_amount", ".00")
			.set("service_status",7)
			.set("source_sys", 0)
			.set("create_by", "呼呼")
			.set("create_on", new Date());
			list.add(pay_batch_total);
		}
		System.out.println("集合长度："+list.size());
		System.out.println("=========开始执行=======");
		
		for(Record r : list) 
		{
			boolean save = Db.save("pay_batch_total", "id", r);
			
		}
		System.out.println("=========执行完毕=======");
	}
}
