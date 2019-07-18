package com.seer.operation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyProfitPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyProfitService;
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

import static com.seer.operation.handleService.StaticFunHandle.convertAmount;
import static com.seer.operation.handleService.StaticFunHandle.getAssetAmount;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

/**
 * 收益每日指标
 * 根据需求修改后的接口
 * 保留原来的接口 seer/daily/data/house/page
 */
@RestController
@RequestMapping(value = "seer/dailyProfit")
@Slf4j
public class DailyProfitController {
    @Autowired
    private DailyProfitService dailyProfitService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private ExcelUtil excelUtil;

    @RequestMapping(value = "page")
    public ResponseVo selectHouseRoomPage(Integer current, Integer size, Long beginTime, Long endTime, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyProfitPo> page = dailyProfitService.selectPage(current, size, beginTime, endTime);
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
        List<DailyProfitPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            DailyProfitPo dailyProfitPo = list.get(i);
            object.put("time", dailyProfitPo.getTime());
            object.put("dappProfit", getAssetAmount(dailyProfitPo.getDappProfit(), asset));
            object.put("dappProfitShow", convertAmount(getAssetAmount(dailyProfitPo.getDappProfit(), asset)));
            object.put("totalDappProfit", getAssetAmount(dailyProfitPo.getTotalDappProfit(), asset));
            object.put("totalDappProfitShow", convertAmount(getAssetAmount(dailyProfitPo.getTotalDappProfit(), asset)));
            object.put("faucetProfit", dailyProfitPo.getFaucetProfit());
            object.put("faucetProfitShow", convertAmount(dailyProfitPo.getFaucetProfit()));
            object.put("totalFaucetProfit", dailyProfitPo.getTotalFaucetProfit());
            object.put("totalFaucetProfitShow", convertAmount(dailyProfitPo.getTotalFaucetProfit()));
            object.put("feesProfit", dailyProfitPo.getFeesProfit());
            object.put("feesProfitShow", convertAmount(dailyProfitPo.getFeesProfit()));
            object.put("totalFeesProfit", dailyProfitPo.getTotalFeesProfit());
            object.put("totalFeesProfitShow", convertAmount(dailyProfitPo.getTotalFeesProfit()));
            object.put("dailySubsidy", dailyProfitPo.getDailySubsidy());
            object.put("dailySubsidyShow", convertAmount(dailyProfitPo.getDailySubsidy()));
            object.put("totalSubsidy", dailyProfitPo.getTotalSubsidy());
            object.put("totalSubsidyShow", convertAmount(dailyProfitPo.getTotalSubsidy()));
            object.put("accountBotprtpAmount", getAssetAmount(dailyProfitPo.getAccountBotprtpAmount(), asset));
            object.put("accountBotprtpAmountShow", convertAmount(getAssetAmount(dailyProfitPo.getAccountBotprtpAmount(), asset)));
            object.put("totalAccountBotprtpAmount", getAssetAmount(dailyProfitPo.getTotalAccountBotprtpAmount(), asset));
            object.put("totalAccountBotprtpAmountShow", convertAmount(getAssetAmount(dailyProfitPo.getTotalAccountBotprtpAmount(), asset)));
            object.put("roomprtpAmount", getAssetAmount(dailyProfitPo.getRoomprtpAmount(), asset));
            object.put("roomprtpAmountShow", convertAmount(getAssetAmount(dailyProfitPo.getRoomprtpAmount(), asset)));
            object.put("totalRoomprtpAmount", getAssetAmount(dailyProfitPo.getTotalRoomprtpAmount(), asset));
            object.put("totalRoomprtpAmountShow", convertAmount(getAssetAmount(dailyProfitPo.getTotalRoomprtpAmount(), asset)));
            object.put("roomprtpSettle", getAssetAmount(dailyProfitPo.getRoomprtpSettle(), asset));
            object.put("roomprtpSettleShow", convertAmount(getAssetAmount(dailyProfitPo.getRoomprtpSettle(), asset)));
            object.put("totalRoomprtpSettle", getAssetAmount(dailyProfitPo.getTotalRoomprtpSettle(), asset));
            object.put("totalRoomprtpSettleShow", convertAmount(getAssetAmount(dailyProfitPo.getTotalRoomprtpSettle(), asset)));
            object.put("registeredFees", dailyProfitPo.getRegisteredFees());
            object.put("registeredFeesShow", convertAmount(dailyProfitPo.getRegisteredFees()));
            object.put("totalRegisteredFees", dailyProfitPo.getTotalRegisteredFees());
            object.put("totalRegisteredFeesShow", convertAmount(dailyProfitPo.getTotalRegisteredFees()));
            object.put("transferCountFees", getAssetAmount(dailyProfitPo.getTransferCountFees(), asset));
            object.put("transferCountFeesShow", convertAmount(getAssetAmount(dailyProfitPo.getTransferCountFees(), asset)));
            object.put("totalTransferCountFees", getAssetAmount(dailyProfitPo.getTotalTransferCountFees(), asset));
            object.put("totalTransferCountFeesShow", convertAmount(getAssetAmount(dailyProfitPo.getTotalTransferCountFees(), asset)));
            object.put("updateTime", dailyProfitPo.getUpdateTime());
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
        String fileName = date + "收益每日指标.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("收益每日指标");
        List<DailyProfitPo> dailyProfitPoList = dailyProfitService.selectList(bZeroTime, eZeroTime);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("Dapp收入");
        list.add("累计dapp收入");
        list.add("DAPP水龙头收入");
        list.add("累计DAPP水龙头收入");
        list.add("手续费收入");
        list.add("累计手续费收入");
        list.add("dapp补贴");
        list.add("累计dapp补贴");
        list.add("机器人总体支出");
        list.add("累计机器人总体支出");
        list.add("DAPP所有房间收入");
        list.add("累计DAPP所有房间收入");
        list.add("DAPP所有房间支出");
        list.add("累计DAPP所有房间支出");
        list.add("DAPP注册用户支出");
        list.add("累计DAPP注册用户支出");
        list.add("DAPP新注册用户转账支出");
        list.add("累计DAPP新注册用户转账支出");
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 1; i <= 19; i++) {
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
        for (int i = 0; i < dailyProfitPoList.size(); i++) {
            DailyProfitPo dailyProfitPo = dailyProfitPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //日期
            cell = row.createCell(0);
            cell.setCellValue(dailyProfitPo.getTime());
            cell.setCellStyle(style);
            //Dapp收入
            cell = row.createCell(1);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getDappProfit(), asset).toString());
            cell.setCellStyle(style);
            //累计dapp收入
            cell = row.createCell(2);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getTotalDappProfit(), asset).toString());
            cell.setCellStyle(style);
            //DAPP水龙头收入
            cell = row.createCell(3);
            cell.setCellValue(dailyProfitPo.getFaucetProfit().toString());
            cell.setCellStyle(style);
            //累计DAPP水龙头收入
            cell = row.createCell(4);
            cell.setCellValue(dailyProfitPo.getTotalFaucetProfit().toString());
            cell.setCellStyle(style);
            //手续费收入
            cell = row.createCell(5);
            cell.setCellValue(dailyProfitPo.getFeesProfit().toString());
            cell.setCellStyle(style);
            //累计手续费收入
            cell = row.createCell(6);
            cell.setCellValue(dailyProfitPo.getTotalFeesProfit().toString());
            cell.setCellStyle(style);
            //dapp补贴
            cell = row.createCell(7);
            cell.setCellValue(dailyProfitPo.getDailySubsidy().toString());
            cell.setCellStyle(style);
            //累计dapp补贴
            cell = row.createCell(8);
            cell.setCellValue(dailyProfitPo.getTotalSubsidy().toString());
            cell.setCellStyle(style);
            //机器人总体支出
            cell = row.createCell(9);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getAccountBotprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计机器人总体支出
            cell = row.createCell(10);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getTotalAccountBotprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            //DAPP所有房间收入
            cell = row.createCell(11);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getRoomprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计DAPP所有房间收入
            cell = row.createCell(12);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getTotalRoomprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            //DAPP所有房间支出
            cell = row.createCell(13);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getRoomprtpSettle(), asset).toString());
            cell.setCellStyle(style);
            //累计DAPP所有房间支出
            cell = row.createCell(14);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getTotalRoomprtpSettle(), asset).toString());
            cell.setCellStyle(style);
            //DAPP注册用户支出
            cell = row.createCell(15);
            cell.setCellValue(dailyProfitPo.getRegisteredFees().toString());
            cell.setCellStyle(style);
            //累计DAPP注册用户支出
            cell = row.createCell(16);
            cell.setCellValue(dailyProfitPo.getTotalRegisteredFees().toString());
            cell.setCellStyle(style);
            //DAPP新注册用户转账支出
            cell = row.createCell(17);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getTransferCountFees(), asset).toString());
            cell.setCellStyle(style);
            //累计DAPP新注册用户转账支出
            cell = row.createCell(18);
            cell.setCellValue(getAssetAmount(dailyProfitPo.getTotalTransferCountFees(), asset).toString());
            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == dailyProfitPoList.size()) {
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
