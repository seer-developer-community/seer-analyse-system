package com.seer.operation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyDataFaucetPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyDataFaucetService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.utils.ExcelUtil;
import com.seer.operation.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

import static com.seer.operation.handleService.StaticFunHandle.*;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

/**
 * 用户和交易每日指标
 * 根据需求修改后的接口
 * 保留原来的接口 seer/daily/data/faucet/page
 */
@RestController
@RequestMapping(value = "seer/userTx")
@Slf4j
public class UserTransactionController {
    @Autowired
    private DailyDataFaucetService faucetService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private ExcelUtil excelUtil;

    @RequestMapping(value = "page")
    public ResponseVo selectUserTxPage(Integer current, Integer size, Long beginTime, Long endTime, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyDataFaucetPo> page = faucetService.selectPage(current, size, beginTime, endTime);
        JSONObject result = new JSONObject();
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        result.put("pages", page.getPages());
        //返回显示的资产
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        if (StringUtils.isBlank(asset)) {
            result.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            result.put("asset", asset);
        }
        //重构
        JSONArray newList = new JSONArray();
        List<DailyDataFaucetPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            DailyDataFaucetPo faucetPo = list.get(i);
            object.put("time", faucetPo.getTime());
            object.put("dailyRegistered", faucetPo.getDailyRegistered());
            object.put("totalRegistered", faucetPo.getTotalRegistered());
            object.put("dailyTrueplayers", faucetPo.getDailyTrueplayers());
            object.put("dailyPlayers", faucetPo.getDailyPlayers());
            object.put("totalPlayers", faucetPo.getTotalPlayers());
            object.put("totalActivePlayer", faucetPo.getTotalActivePlayer());
            object.put("dailyDepositCount", getAssetCount(faucetPo.getDailyDepositCount(), asset));
            object.put("totalDepositCount", getAssetCount(faucetPo.getTotalDepositCount(), asset));
            object.put("dailyDepositAmount", getAssetAmount(faucetPo.getDailyDepositAmount(), asset));
            object.put("dailyDepositAmountShow", convertAmount(getAssetAmount(faucetPo.getDailyDepositAmount(), asset)));
            object.put("totalDepositAmount", getAssetAmount(faucetPo.getTotalDepositAmount(), asset));
            object.put("totalDepositAmountShow", convertAmount(getAssetAmount(faucetPo.getTotalDepositAmount(), asset)));
            object.put("dailyTransferCount", getAssetCount(faucetPo.getDailyTransferCount(), asset));
            object.put("totalTransferCount", getAssetCount(faucetPo.getTotalTransferCount(), asset));
            object.put("dailyTransferAmount", getAssetAmount(faucetPo.getDailyTransferAmount(), asset));
            object.put("dailyTransferAmountShow", convertAmount(getAssetAmount(faucetPo.getDailyTransferAmount(), asset)));
            object.put("totalTransferAmount", getAssetAmount(faucetPo.getTotalTransferAmount(), asset));
            object.put("totalTransferAmountShow", convertAmount(getAssetAmount(faucetPo.getTotalTransferAmount(), asset)));
            newList.add(object);
        }
        result.put("records", newList);
        responseVo.setData(result);
        return responseVo;
    }

    @RequestMapping(value = "downExcel")
    public void userTxDownExcel(HttpServletRequest request, HttpServletResponse response) {
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");
        String asset = request.getParameter("asset");
        if (StringUtils.isBlank(asset)) {
            asset = configPo.getDefaultAsset();
        }
        Long beginTimes = 0L;
        Long endTimes = 0L;
        if (!StringUtils.isBlank(beginTime)) {
            beginTimes = Long.valueOf(beginTime);
        }
        if (!StringUtils.isBlank(endTime)) {
            endTimes = Long.valueOf(endTime);
        }
        String date1 = Times.formatDateByTimes(beginTimes);
        String date2 = Times.formatDateByTimes(endTimes);
        String date = "";
        if (date1.equals(date2)) {
            date = date1;
        } else {
            date = date1 + "至" + date2;
        }
        String fileName = date + "用户和交易每日指标.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("用户和交易每日指标");
        List<DailyDataFaucetPo> faucetPoList = faucetService.selectList(bZeroTime, eZeroTime);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("新增注册用户");
        list.add("累计注册用户");
        list.add("新增注册且投注用户数");
        list.add("新增投注用户");
        list.add("累计投注用户");
        list.add("活跃投注用户");
        list.add("充值笔数");
        list.add("累计充值笔数");
        list.add("充值额");
        list.add("累计充值额");
        list.add("转账笔数");
        list.add("累计转账笔数");
        list.add("转账额");
        list.add("累计转账额");
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 1; i <= 15; i++) {
            titleCol.add(30);
        }
        excelUtil.createTitle(workbook, sheet, list, titleCol);
        // 设置内容 列样式
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFCellStyle styleTip = workbook.createCellStyle();
        //自动换行
        style.setWrapText(true);
        styleTip.setWrapText(true);
        // 设置字体
        HSSFFont font = workbook.createFont();
        // 相当于 12px，1 px = 20 short
        font.setFontHeight((short) 240);
        font.setFontName("微软雅黑");
        style.setFont(font);
        styleTip.setFont(font);
        // 设置 水平、垂直居中
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleTip.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        //设置显示的默认资产
        CellRangeAddress cra = new CellRangeAddress(1, 1, 0, titleCol.size() - 1);
        HSSFRow row = sheet.createRow(1);
        row.setHeight((short) 960);//设置行高48px
        sheet.addMergedRegion(cra);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("当前显示的资产：" + asset);
        cell.setCellStyle(styleTip);
        int rowNum = 2;
        for (int i = 0; i < faucetPoList.size(); i++) {
            DailyDataFaucetPo faucetPo = faucetPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //日期
            cell = row.createCell(0);
            cell.setCellValue(faucetPo.getTime());
            cell.setCellStyle(style);
            //新增注册用户
            cell = row.createCell(1);
            cell.setCellValue(faucetPo.getDailyRegistered());
            cell.setCellStyle(style);
            //累计注册用户
            cell = row.createCell(2);
            cell.setCellValue(faucetPo.getTotalRegistered());
            cell.setCellStyle(style);
            //新增注册且投注用户数
            cell = row.createCell(3);
            cell.setCellValue(faucetPo.getDailyTrueplayers());
            cell.setCellStyle(style);
            //新增投注用户
            cell = row.createCell(4);
            cell.setCellValue(faucetPo.getDailyPlayers());
            cell.setCellStyle(style);
            //累计投注用户
            cell = row.createCell(5);
            cell.setCellValue(faucetPo.getTotalPlayers());
            cell.setCellStyle(style);
            //活跃投注用户
            cell = row.createCell(6);
            cell.setCellValue(faucetPo.getTotalActivePlayer());
            cell.setCellStyle(style);
            //充值笔数
            cell = row.createCell(7);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getDailyDepositCount(), asset));
            cell.setCellStyle(style);
            //累计充值笔数
            cell = row.createCell(8);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getTotalDepositCount(), asset));
            cell.setCellStyle(style);
            //充值额
            cell = row.createCell(9);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getDailyDepositAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计充值额
            cell = row.createCell(10);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getTotalDepositAmount(), asset).toString());
            cell.setCellStyle(style);
            //转账笔数
            cell = row.createCell(11);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getDailyTransferCount(), asset));
            cell.setCellStyle(style);
            //累计转账笔数
            cell = row.createCell(12);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getTotalTransferCount(), asset));
            cell.setCellStyle(style);
            //转账额
            cell = row.createCell(13);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getDailyTransferAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计转账额
            cell = row.createCell(14);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getTotalTransferAmount(), asset).toString());
            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == faucetPoList.size()) {
            cra = new CellRangeAddress(2, 2, 0, titleCol.size() - 1);
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            sheet.addMergedRegion(cra);
            // 设置第一列内容
            cell = row.createCell(0);
            if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                cell.setCellValue("没有选择日期，请选择后重新下载！");
            } else {
                cell.setCellValue("暂无数据！");
            }
            cell.setCellStyle(styleTip);
        }
        try {
            excelUtil.buildExcelDocument(fileName, workbook, response);
        } catch (Exception e) {
            log.error("导出Excel错误：{}", e.getMessage());
        }
    }
}
