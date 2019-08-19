package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.JSONUtil;
import com.qhjf.cfm.web.*;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.render.FileRender;
import com.qhjf.cfm.web.validates.Optype;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class CFMBaseController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(CFMBaseController.class);

    public static final String PAGE_NUM = "page_num";
    public static final String PAGE_SIZE = "page_size";
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    private static final AbstractOptypeMgr optypeMgr = BootScanRegisterMgr.getInstance().getOptypeMgr();

    protected JSONObject getObj() {
        Objects.requireNonNull(this.getAttr("_obj"));
        return this.getAttr("_obj");
    }

    //返回当前请求的optype
    protected String getOptype() {
        return getObj().getString("optype");
    }

    protected Ret ok(Object o) {
        return Ret.ok().set("optype", getOptype()).set("data", o);
    }

    protected Ret fail(BusinessException e) {
        return Ret.fail().set("error_code", e.getError_code()).set("error_msg", e.getMessage());
    }

    protected void renderFail(BusinessException e) {
        renderJson(fail(e));
    }

    protected void renderOk(Object o) {
        renderJson(ok(o));
    }

    protected JSONObject getParams() {
        return getObj().getJSONObject("params");
    }

    protected void renderOkPage(Page page) {
        Map<String, Object> root = new HashMap<>();
        root.put("optype", getOptype());
        root.put("total_line", page.getTotalRow());
        root.put("total_page", page.getTotalPage());
        root.put(PAGE_SIZE, page.getPageSize());
        root.put(PAGE_NUM, page.getPageNumber());
        root.put("data", page.getList());
        renderJson(root);
    }


    protected void renderOkPage(Page page, Record ext) {

        Map<String, Object> root = new HashMap<>();
        root.put("optype", getOptype());
        root.put("total_line", page.getTotalRow());
        root.put("total_page", page.getTotalPage());

        root.put(PAGE_SIZE, page.getPageSize());
        root.put(PAGE_NUM, page.getPageNumber());
        root.put("data", page.getList());
        root.put("ext", ext == null ? new Record() : ext);


        //TODO  merger dbt and jyyet
        //总条数
        root.put("total_num", ext == null ? 0 : TypeUtils.castToInt(ext.get("total_num") == null ? 0 : ext.get("total_num")));
        //总金额
        root.put("total_amount", ext == null ? 0 : TypeUtils.castToBigDecimal(ext.get("total_amount") == null ? 0 : ext.get("total_amount")));
        //成功金额
        root.put("success_amount", ext == null ? 0 : TypeUtils.castToBigDecimal(ext.get("success_amount") == null ? 0 : ext.get("success_amount")));

        renderJson(root);
    }

    /**
     * 普通表单参数处理，统一返回Record
     *
     * @return
     */
    protected Record getParamsToRecord() {
        return JSONUtil.parse(getParams());
    }

    /**
     * 带任意扩展参数的Record返回
     *
     * @return
     */
    protected Record getRecordByParamsStrong() {
        JSONObject param = getParams();
        if (param != null) {
            Record result = new Record();
            //一级基础信息数据
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                if (entry.getValue() instanceof JSONArray) {
                    result.set(entry.getKey(), addChild((JSONArray) entry.getValue()));
                } else {
                    result.set(entry.getKey(), entry.getValue());
                }
            }
            return result;
        }
        return null;
    }

    protected int getPageNum(Record record) {
        int page_num = TypeUtils.castToInt(record.get(PAGE_NUM, DEFAULT_PAGE_NUM));
        record.remove(PAGE_NUM);
        return page_num;
    }

    protected int getPageSize(Record record) {
        int page_size = TypeUtils.castToInt(record.get(PAGE_SIZE, DEFAULT_PAGE_SIZE));
        record.remove(PAGE_SIZE);
        return page_size;
    }

    private List<Object> addChild(JSONArray array) {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) array.get(i);
                Record record = new Record();
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    if (entry.getValue() instanceof JSONArray) {
                        record.set(entry.getKey(), addChild((JSONArray) entry.getValue()));
                    } else {
                        record.set(entry.getKey(), entry.getValue());
                    }
                }
                result.add(record);
            } else {
                result.add(array.get(i));
            }
        }
        return result;
    }


    protected boolean validateLoginReq(JSONObject obj) throws BusinessException {
        logger.debug("this.getRequest().getPathInfo() is :" + this.getRequest().getPathInfo());
        logger.debug("this.getRequest().getRequestURI() is :" + this.getRequest().getRequestURI());
        logger.debug("this.getRequest().getServletPath() is :" + this.getRequest().getServletPath());
        logger.debug("this.getRequest().getContextPath() is :" + this.getRequest().getContextPath());
        String path_info = this.getRequest().getPathInfo() != null ?
                this.getRequest().getPathInfo() : this.getRequest().getServletPath();
        if ("/login".equals(path_info)) {
            return optypeMgr.validareLoginReq(obj);
        } else {
            throw new ReqValidateException("PathInfo not validate; is:" + path_info);
        }

    }


    protected boolean validateReq(Optype.Mode mode, JSONObject obj) throws BusinessException {
        return optypeMgr.validateReq(mode, obj);
    }

    protected void keepParams(Optype.Mode mode) {
        JSONObject jobj = getObj();
        String[] keepParams = optypeMgr.getOptype(mode, jobj.getString("optype")).getKeepParams();
        //未找到或者长度为0 忽略
        if (keepParams == null || keepParams.length == 0) {
            return;
        }
        Record record = getParamsToRecord();
        if (record == null) {
            return;
        }
        record.keep(keepParams);
        jobj.put("params", record);
        setAttr("_obj", jobj);
    }

    protected UserInfo getUserInfo() {
        UserInfo userInfo = getAttr("me");
        Objects.requireNonNull(userInfo, "用户信息不存在！");
        return userInfo;
    }

    protected UodpInfo getCurUodp() throws  ReqDataException{
        return getUserInfo().getCurUodp();
    }


    protected  UodpInfo getDefaultUodp() throws  ReqDataException{
        List<UodpInfo> list = getUserInfo().getUodp();
        for (UodpInfo uodpInfo : list) {
            if (uodpInfo.getIs_default() == 1) {
                return uodpInfo;
            }
        }
        throw new ReqDataException("用户信息不存在！");
    }


    public String getRoute(Optype.Mode mode, String str_route) {
        return optypeMgr.getRoute(mode, str_route);
    }


    public void presubmit() {
        try {
            Record record = saveOrChg();
            List<Record> wfs = displayPossibleWf(record);
            record.set("workflows", wfs);
            renderOk(record);
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    public void submit() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/normal/wfprocess/submit");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

//    @Auth(hasForces = {"MyWFPLAT"})
    public void revoke() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/normal/wfprocess/revoke");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    @Auth(hasForces = {"MyWFPLAT"})
    public void agree() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/normal/wfprocess/agree");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    @Auth(hasForces = {"MyWFPLAT"})
    public void reject() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/normal/wfprocess/reject");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    @Auth(hasForces = {"MyWFPLAT"})
    public void append() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/normal/wfprocess/append");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    @Auth(hasForces = {"MyWFPLAT"})
    public void batchagree(){
        try {
            setAttr("_wfobjs", genBatchWfRequestObjs());
            forwardAction("/normal/wfprocess/batchagree");
        } catch (BusinessException e) {
            renderFail(e);
        }

    }

    @Auth(hasForces = {"MyWFPLAT"})
    public void batchappend(){
        try {
            setAttr("_wfobjs", genBatchWfRequestObjs());
            forwardAction("/normal/wfprocess/batchappend");
        } catch (BusinessException e) {
            renderFail(e);
        }

    }

    /**
     * 审批平台 - 我的待办列表（全部业务类型）
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void pendingtasksall() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/comm/wfquery/pendingtasksall");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    /**
     * 审批平台 - 待办任务统计
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void pendingtaskallnum(){
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/comm/wfquery/pendingtaskallnum");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**
     * 审批平台 - 指定业务类型待办列表
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void pendingtasks() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/comm/wfquery/pendingtasks");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**
     * 审批平台 - 指定业务类型已办列表（全部业务类型）
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void processtasksall() throws WorkflowException {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/comm/wfquery/processtasksall");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    /**
     *  审批平台 - 指定业务类型已办任务统计
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void processedtaskallnum() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/comm/wfquery/processedtaskallnum");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    @Auth(hasForces = {"MyWFPLAT"})
    public void processedtasks() {
        try {
            setAttr("_wfobj", genWfRequestObj());
            forwardAction("/comm/wfquery/processedtasks");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    /**
     * 获取审批流对象
     *
     * @return
     * @throws WorkflowException
     */
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        throw new WorkflowException("未指定具体的业务类型");
    }


    /**
     * 获取批量审批流对象
     *
     * @return
     * @throws WorkflowException
     */
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        throw new WorkflowException("未指定具体的业务类型");
    }


    /**
     * 修改或保存提交对象
     *
     * @return
     * @throws WorkflowException
     */
    protected Record saveOrChg() throws BusinessException {
        throw new WorkflowException("未指定具体的业务类型");
    }

    /**
     * 查找可使用的审批流信息
     *
     * @return
     * @throws WorkflowException
     */
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        throw new WorkflowException("未指定具体的业务类型");
    }

    protected Record getParamsToRecordStrong() {
        return JSONUtil.parse(getParams());
    }


    protected void doExport() {
        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
        }catch (Exception e){
            logger.info("获取当前用户机构信息出错！", e);
        }
        AbstractWorkBook abstractWorkBook = AbstractWorkBook.getWorkBook(getOptype(), getParamsToRecord(), uodpInfo);
        if (abstractWorkBook == null) {
            renderError(404);
            return;
        }
        Workbook workbook = abstractWorkBook.getWorkbook();
        if (workbook == null) {
            renderError(404);
            return;
        }
        render(new FileRender(abstractWorkBook.getFileName(), workbook));
    }
    /**
     * 解析文件上传的文件流与其他参数
     * @return
     */
    protected UploadFileScaffold resolveFileUpload(){
    	UploadFileScaffold ufs = new UploadFileScaffold(BasicTypeConstant.DEFAULT_UPLOAD_FILE_SIZE);
    	ufs.action(getRequest());
    	return ufs;
    }

    protected void doExport(Record ext) {
        Record record = getParamsToRecord();
        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
        }catch (Exception e){
            logger.info("获取当前用户机构信息出错！", e);
        }
        if(ext != null && ext.getColumns().size() > 0){
            record.setColumns(ext);
        }
        AbstractWorkBook abstractWorkBook = AbstractWorkBook.getWorkBook(getOptype(), record, uodpInfo);
        if (abstractWorkBook == null) {
            renderError(404);
            return;
        }
        Workbook workbook = abstractWorkBook.getWorkbook();
        if (workbook == null) {
            renderError(404);
            return;
        }

        render(new FileRender(abstractWorkBook.getFileName(), workbook));
    }



    /**
     * 用户日志记录
     */
    protected void optrace(){
        UserInfo me = getUserInfo();
        JSONObject json = getObj();
        String path_info = this.getRequest().getPathInfo() != null ?
                this.getRequest().getPathInfo() : this.getRequest().getServletPath();
        UserOpLogs logs = new UserOpLogs(me, json,path_info);
        new Thread(logs).start();
    }
}
