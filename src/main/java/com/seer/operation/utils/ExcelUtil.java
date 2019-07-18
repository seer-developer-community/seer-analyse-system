package com.seer.operation.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Service
public class ExcelUtil {
    /**
     * 创建表头
     *
     * @param workbook
     * @param sheet
     */
    public void createTitle(HSSFWorkbook workbook, HSSFSheet sheet, List<String> title, List<Integer> cols) {
        HSSFRow row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        for (int i = 0; i < cols.size(); i++) {
            sheet.setColumnWidth(i, cols.get(i) * 256);
        }

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("微软雅黑");
        // 1px = 20 short，此处相当于 14px
        font.setFontHeight((short) 280);
        font.setColor(Font.COLOR_RED);
        // 设置 水平、垂直居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(font);
        //设置行高 .. 36
        row.setHeight((short) 720);
        //自动换行
        style.setWrapText(true);
        HSSFCell cell;

        for (int i = 0; i < title.size(); i++) {
            cell = row.createCell(i); //表头列数 0 开始 属于第一列
            cell.setCellValue(title.get(i)); //表头名称
            cell.setCellStyle(style); //表头样式
        }
    }

    /**
     * 生成 Excel 文件
     *
     * @param filename
     * @param workbook
     * @throws Exception
     */
    public void buildExcelFile(String filename, HSSFWorkbook workbook) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();
    }

    /**
     * 往客户浏览器下载Excel
     *
     * @param filename
     * @param workbook
     * @param response
     * @throws Exception
     */
    public void buildExcelDocument(String filename, HSSFWorkbook workbook, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


}
