package com.qhjf.cfm.web.plugins.excelexp;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.UodpInfo;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther zhangyuanyuan
 * @create 2018/6/19
 */

public abstract class AbstractWorkBook {

    private static final Map<String, AbstractWorkBook> ALLWORKBOOK = new HashMap<>();


    public static void registerWorkBook(AbstractWorkBook book){
        ALLWORKBOOK.put(book.optype , book);
    }


    public static AbstractWorkBook getWorkBook(String optype, Record record, UodpInfo uodpInfo) {
        AbstractWorkBook workBook = ALLWORKBOOK.get(optype);
        if(workBook != null){
            workBook.setRecord(record);
            workBook.setUodpInfo(uodpInfo);
        }
        return workBook;
    }




    protected String optype ;
    protected String fileName;
    protected String[] titles;
    protected String[] titleNames;
    protected String sheetName;
    private Record record;
    private UodpInfo uodpInfo;

    public abstract Workbook getWorkbook();

    public String getFileName() {
        return fileName;
    }


    public Record getRecord() {
        return record;
    }


    public void setRecord(Record record) {
        this.record = record;
    }

    public UodpInfo getUodpInfo(){
        return uodpInfo;
    }
    public void setUodpInfo(UodpInfo uodpInfo){
        this.uodpInfo = uodpInfo;
    }
}
