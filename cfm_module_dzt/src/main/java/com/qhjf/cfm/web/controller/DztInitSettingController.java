package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztInitSettingService;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 对账初始化设置
 */
public class DztInitSettingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztInitSettingController.class);
    private DztInitSettingService service = new DztInitSettingService();
    
    /**
     * 新增
     */
    public void add() {
    	logger.debug("对账通 - 对账初始化设置 - add()");
    	try {
            Record record = getRecordByParamsStrong();
            logger.debug(String.format("对账通 - 对账初始化设置 - 新增() - params:%s", record));
            Record r = service.add(record);
            renderOk(r);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
    /**
     * 修改
     */
    public void chg(){
    	logger.debug("对账通 - 对账初始化设置 - upd()");
    	try {
            Record record = getRecordByParamsStrong();
            logger.debug(String.format("对账通 - 对账初始化设置 - 修改() - params:%s", record));
            Record r = service.chg(record);
            renderOk(r);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
    /**
     * 启用
     */
    public void enable() {
    	logger.debug("对账通 - 对账初始化设置 - enable()");
    	try {
            Record record = getRecordByParamsStrong();
            logger.debug(String.format("对账通 - 对账初始化设置 - 启用() - params:%s", record));
            Record r = service.enable(record);
            renderOk(r);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
    /**
     * 查询详情
     */
    public void detail(){
    	logger.debug("对账通 - 对账初始化设置 - detail()");
    	Record record = getRecordByParamsStrong();
        logger.debug(String.format("对账通 - 对账初始化设置 - 详情() - params:%s", record));
        List<Record> items = service.detail(record);
        renderOk(items);
    }
    /**
     * 期初余额列表
     */
    public void list(){
    	logger.debug("对账通 - 对账初始化设置 - list()");
    	Record record = getRecordByParamsStrong();
        logger.debug(String.format("对账通 - 对账初始化设置 - 列表() - params:%s", record));
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.list(pageNum, pageSize);
        renderOkPage(page);
    }
}
