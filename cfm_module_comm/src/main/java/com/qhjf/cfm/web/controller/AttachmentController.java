package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.oreilly.servlet.multipart.*;
import com.qhjf.cfm.exceptions.AttachmentException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.render.AttachmentRender;
import com.qhjf.cfm.web.service.AttachmentService;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;
import com.qhjf.cfm.web.validates.OptypeValidate;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


@Auth(hasRoles = {"admin", "normal"})
public class AttachmentController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(AttachmentController.class);

    private final static Integer DEFAULT_SIZE = 10485760; // 默认的文件上传大小限制(10M)

    private final static String DEFAULT_ENCODEING = "UTF-8";

    private AttachmentService service = new AttachmentService();

    private final static OptypeValidate  vld = new OptypeValidate("download",
            new RequiredParamsValidate(new String[]{"object_id"}));


    /**
     * 上传附件
     */
    public void upload(){
        log.debug("Enter into upload()");
        try {
            List<Record> records = saveUploadfile();
            renderOk(records);
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    private List<Record> saveUploadfile() throws BusinessException{
        log.debug("Enter into upload()");
        List<Record>  result  = null;
        try{
            MultipartParser multipartParser = new MultipartParser(getRequest(), DEFAULT_SIZE, true, false, DEFAULT_ENCODEING);
            result = new ArrayList<Record>();
            Part part = null;

            while((part = multipartParser.readNextPart()) != null){
                String name = part.getName();
                if(part.isFile()){
                    FilePart filePart = (FilePart) part;
                    String fileName = filePart.getFileName();
                    if (!(fileName == null || fileName.length() <= 0)){
                        FileRenamePolicy fileRename = new DefaultFileRenamePolicy();
                        filePart.setRenamePolicy(fileRename);
                        String originFileName = Jsoup.clean(fileName, Whitelist.basicWithImages()); //对文件名进行XSS防御处理
                        int pos = originFileName.lastIndexOf(".");
                        String extName = pos > 0 ? originFileName.substring(pos + 1).toLowerCase() : "";
                        Record record = initUploadUser();
                        service.saveAttachment(originFileName, extName ,filePart.getInputStream(),record);
                        result.add(record);
                    }
                }else if(part.isParam()){
                    ParamPart paramPart = (ParamPart)part;
                    String filename = "paramfile";
                    String extName = "";
                    if(name != null && !"".equals(name)){
                        filename = name;
                        int pos = filename.lastIndexOf(".");
                        extName = pos > 0 ? filename.substring(pos + 1).toLowerCase() : "cfm";
                    }
                    Record record = initUploadUser();
                    service.saveAttachment(filename, extName ,new ByteArrayInputStream(paramPart.getValue()),record);
                    result.add(record);
                }else{
                    throw new AttachmentException("附件上传参数有误！");
                }


            }
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw (BusinessException)e;
            }else{
                throw  new AttachmentException(e);
            }
        }
        return result;
    }


    private Record initUploadUser(){
        UserInfo userInfo = getUserInfo();
        Record record = new Record();
        record.set("upload_by", userInfo.getUsr_id());
        record.set("upload_user_name",userInfo.getName());
        return record;
    }


    /**
     * 下载附件
     */
    public void download(){
        log.debug("Enter into download()");
        JSONObject jobj = this.getAttr("_obj");
        try{
            if(validateReq(jobj)){
                FileInfo file = service.downloadAttachment(getParamsToRecord());
                if(file != null ){
                    render(new AttachmentRender(file));
                }else{
                    renderError(404);
                    return;
                }
            }
        }catch (BusinessException e){
            e.printStackTrace();
            renderFail(e);
        }
    }


    protected void renderOk(Object o) {
        renderJson(Ret.ok().set("files", o));
    }

    protected boolean validateReq(JSONObject obj) throws BusinessException {
        return vld.validate(obj);
    }

    public void list(){
        Record record = getParamsToRecord();
        List<Record> list = service.getFilesByBillidAndBiztype(record);
        renderOk(list);
    }
}
