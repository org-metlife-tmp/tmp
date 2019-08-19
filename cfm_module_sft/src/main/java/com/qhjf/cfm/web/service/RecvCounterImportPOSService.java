package com.qhjf.cfm.web.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

/**
 * 柜面收 pos机导入详情
 * 
 * @author pc_liweibing
 *
 */
public class RecvCounterImportPOSService {

	private final static Log logger = LogbackLog.getLog(RecvCounterImportPOSService.class);
	
	/**
	 * 
	 * @param record
	 * @param curUodp
	 * @param pageNum 
	 * @param pageSize 
	 * @return 
	 */
	public Page<Record> list(Record record, UodpInfo curUodp, int pageSize, int pageNum) {
		SqlPara sqlPara = Db.getSqlPara("recv_counter_pos_import.recvcounterPoslist", Kv.by("map", record.getColumns()));
		Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		return paginate;
		
	}
   
       
}

