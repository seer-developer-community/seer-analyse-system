package com.seer.operation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyHouseRoomPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyHouseRoomService;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.seer.operation.handleService.StaticFunHandle.*;
import static com.seer.operation.request.Constants.STRING_SPLIT_CHAR;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

/**
 * 分平台房间每日指标
 * 根据需求修改后的接口
 * 保留原来的接口 seer/daily/data/house/page
 */
@RestController
@RequestMapping(value = "seer/houseRoom")
@Slf4j
public class HouseRoomController {
    @Autowired
    private DailyHouseRoomService houseRoomService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private ExcelUtil excelUtil;

    @RequestMapping(value = "page")
    public ResponseVo selectHouseRoomPage(Integer current, Integer size, Long beginTime, Long endTime, String asset, String house) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyHouseRoomPo> page = houseRoomService.selectPage(current, size, beginTime, endTime);
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
        List<String> houses = new LinkedList<>();
        if (StringUtils.isBlank(house)) {
            result.put("house", configPo.getDefaultOwner());
            house = configPo.getDefaultOwner();
            houses = Arrays.asList(house.split(STRING_SPLIT_CHAR));
        } else {
            result.put("house", house);
            houses = Arrays.asList(house.split(STRING_SPLIT_CHAR));
        }
        if (houses.size() > 1) {
            result.put("showHouseMore", true);
        } else {
            result.put("showHouseMore", false);
            result.put("house", houses.get(0));
        }
        //重构
        JSONArray newList = new JSONArray();
        List<DailyHouseRoomPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            DailyHouseRoomPo houseRoomPo = list.get(i);
            object.put("time", houseRoomPo.getTime());
            if (houses.size() > 1) {
                //新增房间数
                object.put("dailyNewRooms", getHousesCount(houseRoomPo.getDailyNewRooms(), houses));
                object.put("dailyNewRoomsSum", getHousesCountSum(houseRoomPo.getDailyNewRooms(), houses));
                //新增房间参与率
                object.put("dailyPrtpRate", getHousesRate(houseRoomPo.getDailyPrtpRate(), houses));
                object.put("dailyPrtpRateSum", getHousesRateSum(houseRoomPo.getDailyPrtpRate(), houses));
                //累计房间数
                object.put("totalRooms", getHousesCount(houseRoomPo.getTotalRooms(), houses));
                object.put("totalRoomsSum", getHousesCountSum(houseRoomPo.getTotalRooms(), houses));
                //累计房间参与率
                object.put("totalPrtpRate", getHousesRate(houseRoomPo.getTotalPrtpRate(), houses));
                object.put("totalPrtpRateSum", getHousesRateSum(houseRoomPo.getTotalPrtpRate(), houses));
                //可投注房间数
                object.put("dailyOpeningRoom", getHousesCount(houseRoomPo.getDailyOpeningRoom(), houses));
                object.put("dailyOpeningRoomSum", getHousesCountSum(houseRoomPo.getDailyOpeningRoom(), houses));
                //投注人次
                object.put("prtpTimes", getHousesCount(houseRoomPo.getPrtpTimes(), houses));
                object.put("prtpTimesSum", getHousesCountSum(houseRoomPo.getPrtpTimes(), houses));
                //累计投注人次
                object.put("totalPrtpTimes", getHousesCount(houseRoomPo.getTotalPrtpTimes(), houses));
                object.put("totalPrtpTimesSum", getHousesCountSum(houseRoomPo.getTotalPrtpTimes(), houses));
                //投注额
                object.put("playAmount", getAssetAmount(houseRoomPo.getPlayAmount(), asset));
                object.put("playAmountShow",convertAmount(getAssetAmount(houseRoomPo.getPlayAmount(), asset)));
                //累计投注额
                object.put("totalPlayAmount", getAssetAmount(houseRoomPo.getTotalPlayAmount(), asset));
                object.put("totalPlayAmountShow",convertAmount(getAssetAmount(houseRoomPo.getTotalPlayAmount(), asset)));
            } else {
                object.put("dailyNewRooms", getHouseCount(houseRoomPo.getDailyNewRooms(), houses.get(0)));
                object.put("dailyPrtpRate", getHouseRate(houseRoomPo.getDailyPrtpRate(), houses.get(0)));
                object.put("totalRooms", getHouseCount(houseRoomPo.getTotalRooms(), houses.get(0)));
                object.put("totalPrtpRate", getHouseRate(houseRoomPo.getTotalPrtpRate(), houses.get(0)));
                object.put("dailyOpeningRoom", getHouseCount(houseRoomPo.getDailyOpeningRoom(), houses.get(0)));
                object.put("prtpTimes", getHouseCount(houseRoomPo.getPrtpTimes(), houses.get(0)));
                object.put("totalPrtpTimes", getHouseCount(houseRoomPo.getTotalPrtpTimes(), houses.get(0)));
                object.put("playAmount", getAssetAmount(houseRoomPo.getPlayAmount(), asset));
                object.put("playAmountShow",convertAmount(getAssetAmount(houseRoomPo.getPlayAmount(), asset)));
                object.put("totalPlayAmount", getAssetAmount(houseRoomPo.getTotalPlayAmount(), asset));
                object.put("totalPlayAmountShow",convertAmount(getAssetAmount(houseRoomPo.getTotalPlayAmount(), asset)));
            }
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
        String house = request.getParameter("house");
        if (StringUtils.isBlank(asset)) {
            asset = configPo.getDefaultAsset();
        }
        List<String> houses = new LinkedList<>();
        if (StringUtils.isBlank(house)) {
            house = configPo.getDefaultOwner();
            houses = Arrays.asList(house.split(STRING_SPLIT_CHAR));
        } else {
            houses = Arrays.asList(house.split(STRING_SPLIT_CHAR));
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
        String fileName = date + "分平台房间每日指标.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("分平台房间每日指标");
        List<DailyHouseRoomPo> dailyHouseRoomPoList = houseRoomService.selectList(bZeroTime, eZeroTime);
        List<String> list = new LinkedList<>();
        list.add("日期");
        if (houses.size() > 1) {
            list.add("新增房间数:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("新增房间数:" + houses.get(i));
            }
            list.add("新增房间参与率:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("新增房间参与率:" + houses.get(i));
            }
            list.add("累计房间数:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("累计房间数:" + houses.get(i));
            }
            list.add("累计房间参与率:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("累计房间参与率:" + houses.get(i));
            }
            list.add("可投注房间数:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("可投注房间数:" + houses.get(i));
            }
            list.add("投注人次:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("投注人次:" + houses.get(i));
            }
            list.add("累计投注人次:总和");
            for (int i = 0; i < houses.size(); i++) {
                list.add("累计投注人次:" + houses.get(i));
            }
            list.add("投注额");
            list.add("累计投注额");
        } else {
            list.add("新增房间数");
            list.add("新增房间参与率");
            list.add("累计房间数");
            list.add("累计房间参与率");
            list.add("可投注房间数");
            list.add("投注人次");
            list.add("累计投注人次");
            list.add("投注额");
            list.add("累计投注额");
        }
        List<Integer> titleCol = new LinkedList<>();
        if (houses.size() > 1) {
            int sum = 10 + 7 * houses.size();
            for (int i = 1; i <= sum; i++) {
                titleCol.add(30);
            }
        } else {
            for (int i = 1; i <= 10; i++) {
                titleCol.add(30);
            }
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
        String showHouse = "";
        if (houses.size() > 1) {
            showHouse = houses.get(0);
            for (int i = 1; i < houses.size(); i++) {
                showHouse = showHouse + "," + houses.get(i);
            }
        } else {
            showHouse = houses.get(0);
        }
        cell.setCellValue("当前显示的资产：<" + asset + "> 当前显示的房主账号：<" + showHouse + ">");
        cell.setCellStyle(styleTip);
        int rowNum = 2;
        for (int i = 0; i < dailyHouseRoomPoList.size(); i++) {
            DailyHouseRoomPo roomPo = dailyHouseRoomPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //日期
            cell = row.createCell(0);
            cell.setCellValue(roomPo.getTime());
            cell.setCellStyle(style);
            if (houses.size() > 1) {
                int size = houses.size();//eg:2
                int curCell = 1;
                //新增房间数
                cell = row.createCell(curCell);//eg:1
                cell.setCellValue(StaticFunHandle.getHousesCountSum(roomPo.getDailyNewRooms(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:2
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getDailyNewRooms(), houses.get(k)));
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:2+2
                //新增房间参与率
                cell = row.createCell(curCell);//eg:4
                cell.setCellValue(StaticFunHandle.getHousesRateSum(roomPo.getDailyPrtpRate(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:5
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseRate(roomPo.getDailyPrtpRate(), houses.get(k)).toString());
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:5+2
                //累计房间数
                cell = row.createCell(curCell);//eg:7
                cell.setCellValue(StaticFunHandle.getHousesCountSum(roomPo.getTotalRooms(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:8
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getTotalRooms(), houses.get(k)).toString());
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:8+2
                //累计房间参与率
                cell = row.createCell(curCell);//eg:10
                cell.setCellValue(StaticFunHandle.getHousesRateSum(roomPo.getTotalPrtpRate(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:11
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseRate(roomPo.getTotalPrtpRate(), houses.get(k)).toString());
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:11+2
                //可投注房间数
                cell = row.createCell(curCell);//eg:13
                cell.setCellValue(StaticFunHandle.getHousesCountSum(roomPo.getDailyOpeningRoom(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:14
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getDailyOpeningRoom(), houses.get(k)).toString());
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:14+2
                //投注人次
                cell = row.createCell(curCell);//eg:16
                cell.setCellValue(StaticFunHandle.getHousesCountSum(roomPo.getPrtpTimes(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:17
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getPrtpTimes(), houses.get(k)).toString());
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:17+2
                //累计投注人次
                cell = row.createCell(curCell);//eg:19
                cell.setCellValue(StaticFunHandle.getHousesCountSum(roomPo.getTotalPrtpTimes(), houses));
                cell.setCellStyle(style);
                curCell++;//eg:20
                for (int k = 0; k < size; k++) {
                    cell = row.createCell(curCell + k);
                    cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getTotalPrtpTimes(), houses.get(k)).toString());
                    cell.setCellStyle(style);
                }
                curCell = curCell + size;//eg:20+2
                //投注额
                cell = row.createCell(curCell);
                cell.setCellValue(StaticFunHandle.getAssetAmount(roomPo.getPlayAmount(), asset).toString());
                cell.setCellStyle(style);
                curCell++;//eg:23
                //累计投注额
                cell = row.createCell(curCell);
                cell.setCellValue(StaticFunHandle.getAssetAmount(roomPo.getTotalPlayAmount(), asset).toString());
                cell.setCellStyle(style);
            } else {
                //新增房间数
                cell = row.createCell(1);
                cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getDailyNewRooms(), houses.get(0)));
                cell.setCellStyle(style);
                //新增房间参与率
                cell = row.createCell(2);
                cell.setCellValue(StaticFunHandle.getHouseRate(roomPo.getDailyPrtpRate(), houses.get(0)).toString());
                cell.setCellStyle(style);
                //累计房间数
                cell = row.createCell(3);
                cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getTotalRooms(), houses.get(0)));
                cell.setCellStyle(style);
                //累计房间参与率
                cell = row.createCell(4);
                cell.setCellValue(StaticFunHandle.getHouseRate(roomPo.getTotalPrtpRate(), houses.get(0)).toString());
                cell.setCellStyle(style);
                //可投注房间数
                cell = row.createCell(5);
                cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getDailyOpeningRoom(), houses.get(0)));
                cell.setCellStyle(style);
                //投注人次
                cell = row.createCell(6);
                cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getPrtpTimes(), houses.get(0)));
                cell.setCellStyle(style);
                //累计投注人次
                cell = row.createCell(7);
                cell.setCellValue(StaticFunHandle.getHouseCount(roomPo.getTotalPrtpTimes(), houses.get(0)));
                cell.setCellStyle(style);
                //投注额
                cell = row.createCell(8);
                cell.setCellValue(StaticFunHandle.getAssetAmount(roomPo.getPlayAmount(), asset).toString());
                cell.setCellStyle(style);
                //累计投注额
                cell = row.createCell(9);
                cell.setCellValue(StaticFunHandle.getAssetAmount(roomPo.getTotalPlayAmount(), asset).toString());
                cell.setCellStyle(style);
            }
            rowNum++;
        }
        if (0 == dailyHouseRoomPoList.size()) {
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
