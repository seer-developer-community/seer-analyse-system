package com.seer.operation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyRoomService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.utils.ExcelUtil;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.seer.operation.request.Constants.*;

@RestController
@RequestMapping(value = "seer/daily/room")
public class DailyRoomController {
    @Autowired
    private DailyRoomService dailyRoomService;
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private ExcelUtil excelUtil;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "page")
    public ResponseVo selectPage(Integer current, Integer size, Long beginTime, Long endTime, String room, String asset) {
        Long beginTme = null;
        Long endTme = null;
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyRoomDetailsPo> page = dailyRoomService.selectPage(current, size, room, beginTime, endTime);
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        //前端动态加载表头
        JSONArray colsArray = new JSONArray();
        UserConfigPo configPo = userConfigService.selectOne(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            List<String> strings = Arrays.asList(configPo.getAssets().split(CONFIG_ASSET_SPLIT));
            for (String str : strings) {
                JSONObject object1 = new JSONObject();
                object1.put("name", str);
                colsArray.add(object1);
            }
        }
        object.put("cols", colsArray);
        //返回显示的资产
        if (StringUtils.isBlank(asset)) {
            object.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            object.put("asset", asset);
        }
        //重构返回结果
        JSONArray newList = new JSONArray();
        List<DailyRoomDetailsPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            DailyRoomDetailsPo detailsPo = list.get(i);
            jsonObject.put("time", detailsPo.getTime());
            jsonObject.put("house", detailsPo.getHouse());
            jsonObject.put("room", detailsPo.getRoom());
            jsonObject.put("description", detailsPo.getDescription());
            jsonObject.put("dailyPlayerCount", detailsPo.getDailyPlayerCount());
            jsonObject.put("dailyShares", detailsPo.getDailyShares());
            jsonObject.put("dappProfit", detailsPo.getDappProfit());
            jsonObject.put("updateTime", detailsPo.getUpdateTime());
            Integer type = detailsPo.getType();
            if (PVP_ROOM == type) {
                jsonObject.put("type", "PVP");
            } else if (PVD_ROOM == type) {
                jsonObject.put("type", "PVD");
            } else if (ADV_ROOM == type) {
                jsonObject.put("type", "高级");
            }
            JSONArray array = JSON.parseArray(detailsPo.getDappTotalAmount());
            //处理投注额分列显示
            for (int k = 0; k < colsArray.size(); k++) {
                boolean isNotExist = true;
                boolean isNotExistAsset = true;
                for (int j = 0; j < array.size(); j++) {
                    //返回当前指定显示资产
                    if (asset.equals(array.getJSONObject(j).getString("asset"))) {
                        jsonObject.put("showAsset", array.getJSONObject(j).getBigDecimal("amount"));
                        isNotExistAsset = false;
                    }
                    if (colsArray.getJSONObject(k).getString("name").equals(
                            array.getJSONObject(j).getString("asset")
                    )) {
                        isNotExist = false;
                    }
                    jsonObject.put(array.getJSONObject(j).getString("asset"),
                            array.getJSONObject(j).getString("amount"));
                }
                if (isNotExist) {
                    jsonObject.put(colsArray.getJSONObject(k).getString("name"), "0.00000");
                }
                if (isNotExistAsset) {
                    jsonObject.put("showAsset", 0);
                }
            }
            newList.add(jsonObject);
        }
        object.put("records", newList);
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        responseVo.setData(object);
        return responseVo;
    }

    @RequestMapping(value = "downExcel")
    public void downExcel(HttpServletRequest request, HttpServletResponse response) {
        UserConfigPo configPo = userConfigService.selectOne(USER_CONFIG_ID);
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");
        String asset = request.getParameter("asset");
        String room = request.getParameter("room");
        String[] assets = configPo.getAssets().split(CONFIG_ASSET_SPLIT);
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
        String fileName = date + "房间统计表.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("房间每日统计表");
        List<DailyRoomDetailsPo> detailsPoList = dailyRoomService.selectList(bZeroTime, eZeroTime, room);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("平台号");
        list.add("房间号");
        list.add("房间类型");
        list.add("房间描述");
        list.add("投注人次");
        list.add("投注额度");
        list.add("DAPP收入");
        list.add("资产" + asset + " DAPP补贴");
        list.add("更新时间");
        //将资产分列展示
//        for (String str : assets) {
//            list.add("资产" + str + " DAPP补贴");
//        }
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            titleCol.add(20);
        }
        titleCol.add(60);
//        for (int i = 0; i < assets.length; i++) {
//            titleCol.add(30);
//        }
        for (int i = 0; i < 5; i++) {
            titleCol.add(30);
        }
        excelUtil.createTitle(workbook, sheet, list, titleCol);
        // 设置内容 列样式
        HSSFCellStyle style = workbook.createCellStyle();
        //自动换行
        style.setWrapText(true);
        // 设置字体
        HSSFFont font = workbook.createFont();
        // 相当于 12px，1 px = 20 short
        font.setFontHeight((short) 240);
        font.setFontName("微软雅黑");
        style.setFont(font);
        // 设置 水平、垂直居中
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        int rowNum = 1;
        for (int i = 0; i < detailsPoList.size(); i++) {
            DailyRoomDetailsPo detailsPo = detailsPoList.get(i);
            // 表示创建第一行
            HSSFRow row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            // 设置第一列内容
            //日期
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(detailsPo.getTime());
            cell.setCellStyle(style);
            //平台号
            cell = row.createCell(1);
            cell.setCellValue(detailsPo.getHouse());
            cell.setCellStyle(style);
            //房间号
            cell = row.createCell(2);
            cell.setCellValue(detailsPo.getRoom());
            cell.setCellStyle(style);
            //房间类型
            cell = row.createCell(3);
            if (PVD_ROOM == detailsPo.getType()) {
                cell.setCellValue("PVD");
            } else if (PVP_ROOM == detailsPo.getType()) {
                cell.setCellValue("PVP");
            } else if (ADV_ROOM == detailsPo.getType()) {
                cell.setCellValue("高级");
            }
            cell.setCellStyle(style);
            //房间描述
            cell = row.createCell(4);
            cell.setCellValue(detailsPo.getDescription());
            cell.setCellStyle(style);
            //投注人次
            cell = row.createCell(5);
            cell.setCellValue(detailsPo.getDailyPlayerCount());
            cell.setCellStyle(style);
            //投注额度
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(detailsPo.getDailyShares()));
            cell.setCellStyle(style);
            //DAPP收入
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(detailsPo.getDappProfit()));
            cell.setCellStyle(style);
            //DAPP补贴
            cell = row.createCell(8);
            cell.setCellValue(StaticFunHandle.getAssetAmount(detailsPo.getDappTotalAmount(), asset).toString());
            cell.setCellStyle(style);
            //更新时间
            cell = row.createCell(9);
            cell.setCellValue(Times.formatToDate(detailsPo.getUpdateTime()));
            cell.setCellStyle(style);
            //DAPP补贴，分列显示
//            JSONArray array = JSONArray.parseArray(detailsPo.getDappTotalAmount());
//            for (int j = 0; j < assets.length; j++) {
//                cell = row.createCell(6 + j);
//                boolean isNotExist = true;
//                for (int k = 0; k < array.size(); k++) {
//                    JSONObject object = array.getJSONObject(k);
//                    if (assets[j].equals(object.getString("asset"))) {
//                        cell.setCellValue(object.getString("amount"));
//                        isNotExist = false;
//                        break;
//                    }
//                }
//                if (isNotExist) {
//                    cell.setCellValue("0.00000");
//                }
//                cell.setCellStyle(style);
//            }
//            //房间描述
//            cell = row.createCell(5 + assets.length + 1);
//            cell.setCellValue(detailsPo.getDescription());
//            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == detailsPoList.size()) {
            CellRangeAddress cra = new CellRangeAddress(1, 1, 0, titleCol.size() - 1);
            HSSFRow row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            sheet.addMergedRegion(cra);
            // 设置第一列内容
            HSSFCell cell = row.createCell(0);
            if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                cell.setCellValue("没有选择日期，请选择后重新下载！");
            } else {
                cell.setCellValue("暂无数据！");
            }
            cell.setCellStyle(style);
        }
        try {
            excelUtil.buildExcelDocument(fileName, workbook, response);
        } catch (Exception e) {
            logger.error("导出Excel错误：{}", e.getMessage());
        }
    }

}
