package com.qhjf.cfm.web.plugins.excelexp;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @auther zhangyuanyuan
 * @create 2018/6/22
 */

public class MyPOIDataFormat {

    private DataFormat dataFormat;

    public MyPOIDataFormat(Workbook workbook){
        this.dataFormat = workbook.createDataFormat();
    }

    public short build(String pFormat){
        return dataFormat.getFormat(pFormat);
    }
}
