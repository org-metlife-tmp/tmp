package com.qhjf.cfm.web.service;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.AttachmentException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RandomUtil;
import com.qhjf.cfm.utils.SizeConverter;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;
import com.qhjf.cfm.web.utils.comm.file.tool.FileTransToolFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


//@Auth(hasRoles = {"admin", "normal"})
public class AttachmentService {

    private final static Log logger = LogbackLog.getLog(AttachmentService.class);



    /**
     * 保存附件
     * @param originFileName
     * @param extName
     * @param inputStream
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record saveAttachment(String originFileName, String extName, InputStream inputStream, Record record) throws BusinessException {
        logger.debug("Enter into AttachmentService.saveAttachment(String originFileName, String extName, InputStream inputStream)");
        byte[] data = getContentFormInput(inputStream);
        String objectId = FileTransToolFactory.getInstance()
        		.addNewFileByArray(MD5Kit.string2MD5(originFileName).concat(RandomUtil.currentTimeStamp()).concat(".").concat(extName),data);
        if(objectId != null && !"".equals(objectId)){
            record.set("object_id",objectId).set("original_file_name",originFileName)
                    .set("file_extension_suffix",extName).set("file_size",data.length)
                    .set("file_display_size",SizeConverter.BTrim.convert(data.length));
            if(!Db.save("common_attachment_info",record)){
                throw new AttachmentException("保存附件失败[数据库]！");
            };
        }else{
            throw new AttachmentException("保存附件失败[mongo]！");
        }
        return record;
    }

    /**
     * 下载附件
     * @param record
     * {
     *     "optype": "download",
     *     "params": {
     *         "object_id": "5b36e96fc1caec329127a897"
     *     }
     * }
     */
    public FileInfo downloadAttachment(final Record record) throws  BusinessException{
        logger.debug("Enter into getAllTopLevel()");
        return FileTransToolFactory.getInstance().getFileByObjectid(record.getStr("object_id"));
    }



    /**
     * 从输入流中读取数据
     * @param inputStream
     * @return
     * @throws BusinessException
     */
    private byte[] getContentFormInput(InputStream inputStream) throws BusinessException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                output.write(buffer,0,len);
            }
            return output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AttachmentException("读取文件流失败失败！");
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Record> getFilesByBillidAndBiztype(Record record){
        return Db.find(Db.getSql("attachment.files"),record.get("biz_type"),record.get("bill_id"));
    }
}
