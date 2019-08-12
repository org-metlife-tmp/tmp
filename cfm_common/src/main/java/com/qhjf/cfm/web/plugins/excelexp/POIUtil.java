package com.qhjf.cfm.web.plugins.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther zhangyuanyuan
 * @create 2018/6/19
 */

public class POIUtil {

    public static Workbook createExcel(List<Record> list, AbstractWorkBook abstractWorkBook) {
        String[] titles = abstractWorkBook.titles;
        String[] titleNames = abstractWorkBook.titleNames;
        String sheetName = abstractWorkBook.sheetName;

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //创建表头
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;
        for (int i = 0; i < titles.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(titles[i]);
        }
        //填充数据
        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < titleNames.length; j++) {
                String value = StrKit.isBlank(list.get(i).getStr(titleNames[j])) ? "" : list.get(i).getStr(titleNames[j]);
                cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        return workbook;
    }

    public static Workbook exportExcel(List<Record> list, Record record, String[] titles, String[] titleNames, String sheetName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);

        HSSFRow rows;
        HSSFCell cells;

        //抬头
        rows = sheet.createRow(0);
        rows.setHeightInPoints(20);
        cells = rows.createCell(0);
        cells.setCellValue(sheetName + "清单");
        cells.setCellStyle(new MyPOICellStyle(workbook).setFont(
                new MyPOIFont(workbook)
                        .setFontColor(HSSFColor.GREY_50_PERCENT.index)
                        .setFontHeightInPoints((short) 12)
                        .setBold(true)
                        .build()
        ).build());

        CellRangeAddress cra = new CellRangeAddress(0, 0, 0, titles.length - 1); // 起始行, 终止行, 起始列, 终止列
        //设置合并汇总
        CellRangeAddress summaryCra = new CellRangeAddress(list.size() + 2, list.size() + 2, 0, titles.length - 1); // 起始行, 终止行, 起始列, 终止列


        RegionUtil.setBorderBottom(1, cra, sheet); // 下边框
        RegionUtil.setBorderLeft(1, cra, sheet); // 左边框
        RegionUtil.setBorderRight(1, cra, sheet); // 右边框
        RegionUtil.setBorderTop(1, cra, sheet); // 上边框

        //构建表头
        rows = sheet.createRow(1);
        rows.setHeightInPoints(17);
        for (int i = 0; i < titles.length; i++) {
            cells = rows.createCell(i);
            cells.setCellValue(titles[i]);

            cells.setCellStyle(new MyPOICellStyle(workbook)
                    .setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index)
                    .setBorder(BorderStyle.THIN)
                    .setAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.CENTER)
                    .setFillPattren(FillPatternType.SOLID_FOREGROUND)
                    .build());
        }
        CellStyle border = new MyPOICellStyle(workbook).setBorder(BorderStyle.THIN).build();
        Map<Integer, Integer> map = new HashMap<>();

        //填充数据
        for (int i = 0; i < list.size(); i++) {
            rows = sheet.createRow(i + 2);
            rows.setHeightInPoints(17);

            for (int j = 0; j < titleNames.length; j++) {
                String value = StrKit.isBlank(list.get(i).getStr(titleNames[j])) ? "" : list.get(i).getStr(titleNames[j]);
                Integer titleName = titles[j].getBytes().length + 3;
                cells = rows.createCell(j);
                cells.setCellValue(value);
                cells.setCellStyle(border);

                if (map.get(j) == null) {
                    map.put(j, value.getBytes().length == 0 ? titleName : value.getBytes().length);
                } else {
                    if (map.get(j) < value.getBytes().length) {
                        map.put(j, value.getBytes().length == 0 ? titleName : value.getBytes().length);
                    }
                }


            }
        }

        for (int i = 0; i < map.size(); i++) {
            sheet.setColumnWidth(i, (map.get(i) + 3) * 256);
        }

        System.out.println(map);

        sheet.addMergedRegion(cra);
        sheet.addMergedRegion(summaryCra);

        //设置汇总
        rows = sheet.createRow(list.size() + 2);
        rows.setHeightInPoints(20);
        cells = rows.createCell(0);
        cells.setCellValue("总笔数: " + TypeUtils.castToString(record.get("total_count") == null ? "0" : TypeUtils.castToString(record.get("total_count"))) +
                " 笔;总金额: " + TypeUtils.castToString(record.get("total_amount") == null ? "0" : TypeUtils.castToString(record.get("total_amount"))) +
                " 元;成功笔数: " + TypeUtils.castToString(record.get("success_count") == null ? "0" : TypeUtils.castToString(record.get("success_count"))) +
                " 笔;成功金额: " + TypeUtils.castToString(record.get("success_amount") == null ? "0" : TypeUtils.castToString(record.get("success_amount"))) +
                " 元;失败笔数: " + TypeUtils.castToString(record.get("fail_count") == null ? "0" : TypeUtils.castToString(record.get("fail_count"))) +
                " 笔;失败金额: " + TypeUtils.castToString(record.get("fail_amouunt") == null ? "0" : TypeUtils.castToString(record.get("fail_amouunt"))) + " 元;");

        RegionUtil.setBorderBottom(1, summaryCra, sheet); // 下边框
        RegionUtil.setBorderLeft(1, summaryCra, sheet); // 左边框
        RegionUtil.setBorderRight(1, summaryCra, sheet); // 右边框
        RegionUtil.setBorderTop(1, summaryCra, sheet); // 上边框

        return workbook;
    }
}
