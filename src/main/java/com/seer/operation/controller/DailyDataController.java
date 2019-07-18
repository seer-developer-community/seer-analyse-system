package com.seer.operation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyDataFaucetPo;
import com.seer.operation.entity.DailyDataHousesPo;
import com.seer.operation.entity.DailyDataTotalPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyDataFaucetService;
import com.seer.operation.service.DailyDataHousesService;
import com.seer.operation.service.DailyDataTotalService;
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

@RestController
@RequestMapping(value = "seer/daily/data")
@Slf4j
public class DailyDataController {
    @Autowired
    private DailyDataHousesService housesService;
    @Autowired
    private DailyDataFaucetService faucetService;
    @Autowired
    private DailyDataTotalService dataTotalService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private ExcelUtil excelUtil;

    @RequestMapping(value = "faucet/page")
    public ResponseVo selectFaucetPage(Integer current, Integer size, Long beginTime, Long endTime, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        Long beginTme = null;
        Long endTme = null;
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
            object.put("dailyDepositCountList", JSON.parseArray(faucetPo.getDailyDepositCount()));
            object.put("totalTransferCountFees", getAssetAmount(faucetPo.getTotalTransferCountFees(), asset));
            object.put("totalTransferCountFeesList", JSON.parseArray(faucetPo.getTotalTransferCountFees()));
            object.put("totalDepositCount", getAssetCount(faucetPo.getTotalDepositCount(), asset));
            object.put("totalDepositCountList", JSON.parseArray(faucetPo.getTotalDepositCount()));
            object.put("dailyDepositAmount", getAssetAmount(faucetPo.getDailyDepositAmount(), asset));
            object.put("dailyDepositAmountList", JSON.parseArray(faucetPo.getDailyDepositAmount()));
            object.put("totalDepositAmount", getAssetAmount(faucetPo.getTotalDepositAmount(), asset));
            object.put("totalDepositAmountList", JSON.parseArray(faucetPo.getTotalDepositAmount()));
            object.put("dailyTransferCount", getAssetCount(faucetPo.getDailyTransferCount(), asset));
            object.put("dailyTransferCountList", JSON.parseArray(faucetPo.getDailyTransferCount()));
            object.put("totalTransferCount", getAssetCount(faucetPo.getTotalTransferCount(), asset));
            object.put("totalTransferCountList", JSON.parseArray(faucetPo.getTotalTransferCount()));
            object.put("dailyTransferAmount", getAssetAmount(faucetPo.getDailyTransferAmount(), asset));
            object.put("dailyTransferAmountList", JSON.parseArray(faucetPo.getDailyTransferAmount()));
            object.put("totalTransferAmount", getAssetAmount(faucetPo.getTotalTransferAmount(), asset));
            object.put("totalTransferAmountList", JSON.parseArray(faucetPo.getTotalTransferAmount()));
            object.put("totalRegisteredFees", String.valueOf(faucetPo.getTotalRegisteredFees()));
            object.put("registeredFees", String.valueOf(faucetPo.getRegisteredFees()));
            object.put("transferCountFees", getAssetAmount(faucetPo.getTransferCountFees(), asset));
            object.put("transferCountFeesList", JSON.parseArray(faucetPo.getTransferCountFees()));
            object.put("createTime", faucetPo.getCreateTime());
            object.put("updateTime", faucetPo.getUpdateTime());
            newList.add(object);
        }
        result.put("records", newList);
        responseVo.setData(result);
        return responseVo;
    }

    @RequestMapping(value = "house/page")
    public ResponseVo selectHousePage(Integer current, Integer size, Long beginTime, Long endTime, String asset, String house) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        Long beginTme = null;
        Long endTme = null;
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyDataHousesPo> page = housesService.selectPage(current, size, beginTime, endTime);
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
        //返回显示的房主
        if (StringUtils.isBlank(house)) {
            result.put("house", configPo.getDefaultOwner());
            house = configPo.getDefaultOwner();
        } else {
            result.put("house", house);
        }
        //重构
        JSONArray newList = new JSONArray();
        List<DailyDataHousesPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            DailyDataHousesPo housesPo = list.get(i);
            object.put("time", housesPo.getTime());
            object.put("totalPvpProfit", getAssetAmount(housesPo.getTotalPvpProfit(), asset));
            object.put("totalPvpProfitList", JSON.parseArray(housesPo.getTotalPvpProfit()));
            object.put("totalSubsidy", String.valueOf(housesPo.getTotalSubsidy()));
            object.put("dailySubsidy", String.valueOf(housesPo.getDailySubsidy()));
            object.put("dailyNewRooms", getHouseCount(housesPo.getDailyNewRooms(), house));
            object.put("dailyNewRoomsList", JSON.parseArray(housesPo.getDailyNewRooms()));
            object.put("dailyActiveRoomsList", JSON.parseArray(housesPo.getDailyActiveRooms()));
            object.put("dailyPrtpRate", getHouseRate(housesPo.getDailyPrtpRate(), house));
            object.put("dailyPrtpRateList", JSON.parseArray(housesPo.getDailyPrtpRate()));
            object.put("totalRooms", getHouseCount(housesPo.getTotalRooms(), house));
            object.put("totalRoomsList", JSON.parseArray(housesPo.getTotalRooms()));
            object.put("totalPrtpRate", getHouseRate(housesPo.getTotalPrtpRate(), house));
            object.put("totalPrtpRateList", JSON.parseArray(housesPo.getTotalPrtpRate()));
            object.put("totalAdvplayAmount", getAssetAmount(housesPo.getTotalAdvplayAmount(), asset));
            object.put("totalAdvplayAmountList", JSON.parseArray(housesPo.getTotalAdvplayAmount()));
            object.put("totalPlayAmount", getAssetAmount(housesPo.getTotalPlayAmount(), asset));
            object.put("totalPlayAmountList", JSON.parseArray(housesPo.getTotalPlayAmount()));
            object.put("totalAdvprtpFees", String.valueOf(housesPo.getTotalAdvprtpFees()));
            object.put("totalAdvSettle", getAssetAmount(housesPo.getTotalAdvSettle(), asset));
            object.put("totalAdvSettleList", JSON.parseArray(housesPo.getTotalAdvSettle()));
            object.put("dailyOpeningRoom", getHouseCount(housesPo.getDailyOpeningRoom(), house));
            object.put("dailyOpeningRoomList", JSON.parseArray(housesPo.getDailyOpeningRoom()));
            object.put("prtpTimes", getHouseCount(housesPo.getPrtpTimes(), house));
            object.put("prtpTimesList", JSON.parseArray(housesPo.getPrtpTimes()));
            object.put("totalPrtpTimes", getHouseCount(housesPo.getTotalPrtpTimes(), house));
            object.put("totalPrtpTimesList", JSON.parseArray(housesPo.getTotalPrtpTimes()));
            object.put("totalPvpplayAmount", getAssetAmount(housesPo.getTotalPvpplayAmount(), asset));
            object.put("totalPvpplayAmountList", JSON.parseArray(housesPo.getTotalPvpplayAmount()));
            object.put("totalPvpprtpFees", String.valueOf(housesPo.getTotalPvpFees()));
            object.put("totalPvpSettle", getAssetAmount(housesPo.getTotalPvpSettle(), asset));
            object.put("totalPvpSettleList", JSON.parseArray(housesPo.getTotalPvpSettle()));
            object.put("totalPvpFees", String.valueOf(housesPo.getTotalPvpFees()));
            object.put("totalPvdplayAmount", getAssetAmount(housesPo.getTotalPvdplayAmount(), asset));
            object.put("totalPvdplayAmountList", JSON.parseArray(housesPo.getTotalPvdplayAmount()));
            object.put("totalPvdFees", String.valueOf(housesPo.getTotalPvdFees()));
            object.put("totalAdvFees", String.valueOf(housesPo.getTotalAdvFees()));
            object.put("createTime", housesPo.getCreateTime());
            object.put("updateTime", housesPo.getUpdateTime());
            newList.add(object);
        }
        result.put("records", newList);
        responseVo.setData(result);
        return responseVo;
    }

    @RequestMapping(value = "total/page")
    public ResponseVo selectTotalPage(Integer current, Integer size, Long beginTime, Long endTime, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        Long beginTme = null;
        Long endTme = null;
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyDataTotalPo> page = dataTotalService.selectPage(current, size, beginTime, endTime);
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
        List<DailyDataTotalPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            DailyDataTotalPo dataTotalPo = list.get(i);
            object.put("time", dataTotalPo.getTime());
            object.put("pvpplayAmount", getAssetAmount(dataTotalPo.getPvpplayAmount(), asset));
            object.put("pvpplayAmountList", JSON.parseArray(dataTotalPo.getPvpplayAmount()));
            object.put("pvpprtpFees", String.valueOf(dataTotalPo.getPvpprtpFees()));
            object.put("pvpSettle", getAssetAmount(dataTotalPo.getPvpSettle(), asset));
            object.put("pvpSettleList", JSON.parseArray(dataTotalPo.getPvpSettle()));
            object.put("pvpFees", String.valueOf(dataTotalPo.getPvpFees()));
            object.put("pvdplayAmount", getAssetAmount(dataTotalPo.getPvdplayAmount(), asset));
            object.put("pvdplayAmountList", JSON.parseArray(dataTotalPo.getPvdplayAmount()));
            object.put("pvdprtpFees", String.valueOf(dataTotalPo.getPvdprtpFees()));
            object.put("pvdSettle", getAssetAmount(dataTotalPo.getPvdSettle(), asset));
            object.put("pvdSettleList", JSON.parseArray(dataTotalPo.getPvdSettle()));
            object.put("totalRoomprtpAmount", getAssetAmount(dataTotalPo.getTotalRoomprtpAmount(), asset));
            object.put("totalRoomprtpAmountList", JSON.parseArray(dataTotalPo.getTotalRoomprtpAmount()));
            object.put("totalAccountBotprtpAmount", getAssetAmount(dataTotalPo.getTotalAccountBotprtpAmount(), asset));
            object.put("totalAccountBotprtpAmountList", JSON.parseArray(dataTotalPo.getTotalAccountBotprtpAmount()));
            object.put("pvdFees", String.valueOf(dataTotalPo.getPvdFees()));
            object.put("advplayAmount", getAssetAmount(dataTotalPo.getAdvplayAmount(), asset));
            object.put("advplayAmountList", JSON.parseArray(dataTotalPo.getAdvplayAmount()));
            object.put("advprtpFees", String.valueOf(dataTotalPo.getAdvprtpFees()));
            object.put("advSettle", getAssetAmount(dataTotalPo.getAdvSettle(), asset));
            object.put("advSettleList", JSON.parseArray(dataTotalPo.getAdvSettle()));
            object.put("advFees", String.valueOf(dataTotalPo.getAdvFees()));
            object.put("roomprtpAmount", getAssetAmount(dataTotalPo.getRoomprtpAmount(), asset));
            object.put("roomprtpAmountList", JSON.parseArray(dataTotalPo.getRoomprtpAmount()));
            object.put("roomprtpSettle", getAssetAmount(dataTotalPo.getRoomprtpSettle(), asset));
            object.put("roomprtpSettleList", JSON.parseArray(dataTotalPo.getRoomprtpSettle()));
            object.put("accountBotprtpAmount", getAssetAmount(dataTotalPo.getAccountBotprtpAmount(), asset));
            object.put("accountBotprtpAmountList", JSON.parseArray(dataTotalPo.getAccountBotprtpAmount()));
            object.put("totalRoomprtpSettle", getAssetAmount(dataTotalPo.getTotalRoomprtpSettle(), asset));
            object.put("totalRoomprtpSettleList", JSON.parseArray(dataTotalPo.getTotalRoomprtpSettle()));
            object.put("createTime", dataTotalPo.getCreateTime());
            object.put("updateTime", dataTotalPo.getUpdateTime());
            newList.add(object);
        }
        result.put("records", newList);
        responseVo.setData(result);
        return responseVo;
    }

    @RequestMapping(value = "faucet/downExcel")
    public void faucetDownExcel(HttpServletRequest request, HttpServletResponse response) {
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
        String fileName = date + "每日水龙头账户统计.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("水龙头账户统计");
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
        list.add("累计DAPP新注册用户转账支出");
        list.add("累计充值笔数");
        list.add("充值额");
        list.add("累计充值额");
        list.add("转账笔数");
        list.add("累计转账笔数");
        list.add("转账额");
        list.add("累计转账额");
        list.add("累计DAPP注册用户支出");
        list.add("DAPP注册用户支出");
        list.add("DAPP新注册用户转账支出");
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 1; i <= 19; i++) {
            titleCol.add(40);
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
        //设置默认资产和平台账户显示
        CellRangeAddress cra = new CellRangeAddress(1, 1, 0, titleCol.size() - 1);
        HSSFRow row = sheet.createRow(1);
        row.setHeight((short) 960);//设置行高48px
        sheet.addMergedRegion(cra);
        // 设置第一列内容
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("当前显示的资产：" + asset);
        cell.setCellStyle(styleTip);
        int rowNum = 2;
        for (int i = 0; i < faucetPoList.size(); i++) {
            DailyDataFaucetPo faucetPo = faucetPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //第一列
            cell = row.createCell(0);
            cell.setCellValue(faucetPo.getTime());
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue(faucetPo.getDailyRegistered());
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue(faucetPo.getTotalRegistered());
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue(faucetPo.getDailyTrueplayers());
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue(faucetPo.getDailyPlayers());
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue(faucetPo.getTotalPlayers());
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue(faucetPo.getTotalActivePlayer());
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getDailyDepositCount(), asset));
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getTotalTransferCountFees(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getTotalDepositCount(), asset));
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getDailyDepositAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getTotalDepositAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getDailyTransferCount(), asset));
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue(StaticFunHandle.getAssetCount(faucetPo.getTotalTransferCount(), asset));
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getDailyTransferAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getTotalTransferAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue(faucetPo.getTotalRegisteredFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue(faucetPo.getRegisteredFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue(StaticFunHandle.getAssetAmount(faucetPo.getTransferCountFees(), asset).toString());
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

    @RequestMapping(value = "house/downExcel")
    public void houseDownExcel(HttpServletRequest request, HttpServletResponse response) {
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");
        String asset = request.getParameter("asset");
        String house = request.getParameter("house");
        if (StringUtils.isBlank(asset)) {
            asset = configPo.getDefaultAsset();
        }
        if (StringUtils.isBlank(house)) {
            house = configPo.getDefaultOwner();
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
        String fileName = date + "每日平台房主账户统计.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("每日平台房主账户统计");
        List<DailyDataHousesPo> housesPoList = housesService.selectList(bZeroTime, eZeroTime);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("累计pvp抽成收入");
        list.add("累计dapp补贴");
        list.add("dapp补贴");
        list.add("新增房间数");
        list.add("新增房间参与率");
        list.add("累计房间数");
        list.add("累计房间参与率");
        list.add("累计高级投注总额");
        list.add("累计高级房间手续费收入");
        list.add("累计高级派奖总额");
        list.add("可投注总房间数");
        list.add("投注人次");
        list.add("累计投注人次");
        list.add("累计PVP投注总额");
        list.add("累计投注额");
        list.add("累计PVP房间手续费收入");
        list.add("累计PVP派奖总额");
        list.add("累计PVP房间手续费");
        list.add("累计PVD投注总额");
        list.add("累计PVD房间手续费");
        list.add("累计高级房间手续费");
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 1; i <= 22; i++) {
            titleCol.add(40);
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
        // 设置 垂直、水平居中
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleTip.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        //设置默认资产和平台账户显示
        CellRangeAddress cra = new CellRangeAddress(1, 1, 0, titleCol.size() - 1);
        HSSFRow row = sheet.createRow(1);
        row.setHeight((short) 960);//设置行高48px
        sheet.addMergedRegion(cra);
        // 设置第一列内容
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("当前显示的资产：" + asset + "  当前显示的平台账户：" + house);
        cell.setCellStyle(styleTip);
        int rowNum = 2;
        for (int i = 0; i < housesPoList.size(); i++) {
            DailyDataHousesPo housesPo = housesPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //第一列
            cell = row.createCell(0);
            cell.setCellValue(housesPo.getTime());
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalPvpProfit(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue(housesPo.getTotalSubsidy().toString());
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue(housesPo.getDailySubsidy().toString());
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue(StaticFunHandle.getHouseCount(housesPo.getDailyNewRooms(), house));
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue(StaticFunHandle.getHouseRate(housesPo.getDailyPrtpRate(), house).toString());
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue(StaticFunHandle.getHouseCount(housesPo.getTotalRooms(), house));
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue(StaticFunHandle.getHouseRate(housesPo.getTotalPrtpRate(), house).toString());
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalAdvplayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue(housesPo.getTotalAdvprtpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalAdvSettle(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue(StaticFunHandle.getHouseCount(housesPo.getDailyOpeningRoom(), house));
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue(StaticFunHandle.getHouseCount(housesPo.getPrtpTimes(), house));
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue(StaticFunHandle.getHouseCount(housesPo.getTotalPrtpTimes(), house));
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalPvpplayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue(housesPo.getTotalPvpprtpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalPvpSettle(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue(housesPo.getTotalPvpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(19);
            cell.setCellValue(StaticFunHandle.getAssetAmount(housesPo.getTotalPvdplayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(20);
            cell.setCellValue(housesPo.getTotalPvdFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(21);
            cell.setCellValue(housesPo.getTotalAdvFees().toString());
            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == housesPoList.size()) {
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

    @RequestMapping(value = "total/downExcel")
    public void totalDownExcel(HttpServletRequest request, HttpServletResponse response) {
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
        String fileName = date + "每日总体指标统计.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("每日总体指标统计");
        List<DailyDataTotalPo> totalPoList = dataTotalService.selectList(bZeroTime, eZeroTime);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("PVP投注总额");
        list.add("PVP房间手续费收入");
        list.add("PVP派奖总额");
        list.add("PVP房间手续费");
        list.add("PVD投注总额");
        list.add("PVD房间手续费收入");
        list.add("PVD派奖总额");
        list.add("累计DAPP所有房间收入");
        list.add("累计机器人总体支出");
        list.add("PVD房间手续费");
        list.add("高级投注总额");
        list.add("高级房间手续费收入");
        list.add("高级派奖总额");
        list.add("高级房间手续费");
        list.add("DAPP所有房间收入");
        list.add("DAPP所有房间支出");
        list.add("机器人总体支出");
        list.add("累计DAPP所有房间支出");
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 1; i <= 19; i++) {
            titleCol.add(40);
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
        //设置默认资产和平台账户显示
        CellRangeAddress cra = new CellRangeAddress(1, 1, 0, titleCol.size() - 1);
        HSSFRow row = sheet.createRow(1);
        row.setHeight((short) 960);//设置行高48px
        sheet.addMergedRegion(cra);
        // 设置第一列内容
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("当前显示的资产：" + asset);
        cell.setCellStyle(styleTip);
        int rowNum = 2;
        for (int i = 0; i < totalPoList.size(); i++) {
            DailyDataTotalPo dataTotalPo = totalPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //第一列
            cell = row.createCell(0);
            cell.setCellValue(dataTotalPo.getTime());
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getPvpplayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue(dataTotalPo.getPvpprtpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getPvpSettle(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue(dataTotalPo.getPvpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getPvdplayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue(dataTotalPo.getPvdprtpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getPvdSettle(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getTotalRoomprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getTotalAccountBotprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue(dataTotalPo.getPvdFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getAdvplayAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue(dataTotalPo.getAdvprtpFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getAdvSettle(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue(dataTotalPo.getAdvFees().toString());
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getRoomprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getRoomprtpSettle(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getAccountBotprtpAmount(), asset).toString());
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue(StaticFunHandle.getAssetAmount(dataTotalPo.getTotalRoomprtpSettle(), asset).toString());
            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == totalPoList.size()) {
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
