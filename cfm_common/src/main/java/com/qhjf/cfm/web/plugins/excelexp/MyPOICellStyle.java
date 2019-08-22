package com.qhjf.cfm.web.plugins.excelexp;

import org.apache.poi.ss.usermodel.*;

public class MyPOICellStyle {

    private CellStyle cellStyle;


    public MyPOICellStyle(Workbook workbook) {
        this.cellStyle = workbook.createCellStyle();
    }

    public MyPOICellStyle setFillPattren(FillPatternType fillPatternType) {
        cellStyle.setFillPattern(fillPatternType);
        return this;
    }

    public MyPOICellStyle setBgColor(short color) {
        cellStyle.setFillBackgroundColor(color);
        return this;
    }

    public MyPOICellStyle setFillForegroundColor(short color){
        cellStyle.setFillForegroundColor(color);
        return this;
    }

    public MyPOICellStyle setBorder(BorderStyle style) {
        cellStyle.setBorderBottom(style);
        cellStyle.setBorderLeft(style);
        cellStyle.setBorderRight(style);
        cellStyle.setBorderTop(style);
        return this;
    }

    public MyPOICellStyle setAlignment(HorizontalAlignment align){
        cellStyle.setAlignment(align);
        return this;
    }

    public MyPOICellStyle setVerticalAlignment(VerticalAlignment alignment){
        cellStyle.setVerticalAlignment(alignment);
        return this;
    }

    public MyPOICellStyle setFont(Font font){
        cellStyle.setFont(font);
        return this;
    }

    public MyPOICellStyle setDataFormat(short fmt){
        cellStyle.setDataFormat(fmt);
        return this;
    }

    public CellStyle build() {
        return cellStyle;
    }

}
