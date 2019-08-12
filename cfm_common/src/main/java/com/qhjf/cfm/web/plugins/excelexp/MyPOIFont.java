package com.qhjf.cfm.web.plugins.excelexp;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @auther zhangyuanyuan
 * @create 2018/6/21
 */

public class MyPOIFont {

    private Font font;

    public MyPOIFont(Workbook workbook){
        this.font = workbook.createFont();
    }

    public MyPOIFont setFontHeightInPoints(short height){
        font.setFontHeightInPoints(height);
        return this;
    }

    public MyPOIFont setBold(boolean bool){
        font.setBold(bool);
        return this;
    }

    public MyPOIFont setFontColor(short color){
        font.setColor(color);
        return this;
    }

    public Font build(){
        return font;
    }
}
