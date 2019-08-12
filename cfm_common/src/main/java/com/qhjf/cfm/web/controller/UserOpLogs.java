package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UserOpLogs implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(UserOpLogs.class);

    private UserInfo userInfo;

    private JSONObject json;

    private String op_url;

    UserOpLogs(UserInfo userInfo, JSONObject json, String op_url) {
        this.userInfo = userInfo;
        this.json = json;
        this.op_url = op_url;
    }


    @Override
    public void run() {
        log.info("Begin log user op!");
        if(userInfo != null){
            Record record = new Record();
            record.set("usr_id", userInfo.getUsr_id());
            record.set("login_name", userInfo.getLogin_name());
            record.set("token", MD5Kit.string2MD5(userInfo.getLastToken()));
            UodpInfo uodpInfo = userInfo.getCurUodp();
            if (uodpInfo != null) {
                record.set("org_id", uodpInfo.getOrg_id());
                record.set("org_name", uodpInfo.getOrg_name());
                record.set("dept_id", uodpInfo.getDept_id());
                record.set("dept_name", uodpInfo.getDept_name());
                record.set("pos_id", uodpInfo.getPos_id());
                record.set("pos_name", uodpInfo.getPos_name());
            }
            record.set("optype", json.getString("optype"));
            record.set("op_params", json.getJSONObject("params") != null ?json.getJSONObject("params").toJSONString():"");
            record.set("op_url",this.op_url);
            record.set("op_time", new Date());
            try{
                Db.save("user_op_logs", record);
                log.info("log user op finsh !");
            }catch (Exception e){
                log.error("记录日志错误:"+e.getMessage());
            }

        }else{
            log.error("记录日志错误, userInfo is null");
        }
    }
}
