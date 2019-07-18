package com.seer.operation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyTypeRoomPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyTypeRoomService;
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
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

/**
 * 分类型房间每日指标
 * 根据需求修改后的接口
 * 保留原来的接口 seer/daily/data/total/page
 */
@RestController
@RequestMapping(value = "seer/typeRoom")
@Slf4j
public class TypeRoomController {
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private DailyTypeRoomService dailyTypeRoomService;
    @Autowired
    private ExcelUtil excelUtil;

    @RequestMapping(value = "page")
    public ResponseVo selectTypeRoomPage(Integer current, Integer size, Long beginTime, Long endTime, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyTypeRoomPo> page = dailyTypeRoomService.selectPage(current, size, beginTime, endTime);
        JSONObject result = new JSONObject();
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());
        result.put("pages", page.getPages());
        //返回显示的资产
        UserConfigPo configPo = userConfigService.selectOne(USER_CONFIG_ID);
        if (StringUtils.isBlank(asset)) {
            result.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            result.put("asset", asset);
        }
        //重构
        JSONArray newList = new JSONArray();
        List<DailyTypeRoomPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            DailyTypeRoomPo typeRoomPo = list.get(i);
            object.put("time", typeRoomPo.getTime());
            object.put("pvpProfit", StaticFunHandle.getAssetAmount(typeRoomPo.getPvpProfit(), asset));
            object.put("pvpProfitShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getPvpProfit(), asset)));
            object.put("totalPvpProfit", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpProfit(), asset));
            object.put("totalPvpProfitShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpProfit(), asset)));
            object.put("pvpPlayAmount", StaticFunHandle.getAssetAmount(typeRoomPo.getPvpPlayAmount(), asset));
            object.put("pvpPlayAmountShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getPvpPlayAmount(), asset)));
            object.put("totalPvpPlayAmount", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpPlayAmount(), asset));
            object.put("totalPvpPlayAmountShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpPlayAmount(), asset)));
            object.put("pvpSettle", StaticFunHandle.getAssetAmount(typeRoomPo.getPvpSettle(), asset));
            object.put("pvpSettleShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getPvpSettle(), asset)));
            object.put("totalPvpSettle", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpSettle(), asset));
            object.put("totalPvpSettleShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpSettle(), asset)));
            object.put("pvpFees", typeRoomPo.getPvpFees());
            object.put("pvpFeesShow", convertAmount(typeRoomPo.getPvpFees()));
            object.put("totalPvpFees", typeRoomPo.getTotalPvpFees());
            object.put("totalPvpFeesShow", convertAmount(typeRoomPo.getTotalPvpFees()));
            object.put("pvpPrtpFees", typeRoomPo.getPvpPrtpFees());
            object.put("pvpPrtpFeesShow", convertAmount(typeRoomPo.getPvpPrtpFees()));
            object.put("totalPvpPrtpFees", typeRoomPo.getTotalPvpPrtpFees());
            object.put("totalPvpPrtpFeesShow", convertAmount(typeRoomPo.getTotalPvpPrtpFees()));
            object.put("pvdPlayAmount", StaticFunHandle.getAssetAmount(typeRoomPo.getPvdPlayAmount(), asset));
            object.put("pvdPlayAmountShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getPvdPlayAmount(), asset)));
            object.put("pvdSettle", StaticFunHandle.getAssetAmount(typeRoomPo.getPvdSettle(), asset));
            object.put("pvdSettleShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getPvdSettle(), asset)));
            object.put("totalPvdPlayAmount", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvdPlayAmount(), asset));
            object.put("totalPvdPlayAmountShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvdPlayAmount(), asset)));
            object.put("totalPvdSettle", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvdSettle(), asset));
            object.put("totalPvdSettleShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvdSettle(), asset)));
            object.put("pvdFees", typeRoomPo.getPvdFees());
            object.put("pvdFeesShow", convertAmount(typeRoomPo.getPvdFees()));
            object.put("totalPvdFees", typeRoomPo.getTotalPvdFees());
            object.put("totalPvdFeesShow", convertAmount(typeRoomPo.getTotalPvdFees()));
            object.put("pvdPrtpFees", typeRoomPo.getPvdPrtpFees());
            object.put("pvdPrtpFeesShow", convertAmount(typeRoomPo.getPvdPrtpFees()));
            object.put("totalPvdPrtpFees", typeRoomPo.getTotalPvdPrtpFees());
            object.put("totalPvdPrtpFeesShow", convertAmount(typeRoomPo.getTotalPvdPrtpFees()));
            object.put("advPlayAmount", StaticFunHandle.getAssetAmount(typeRoomPo.getAdvPlayAmount(), asset));
            object.put("advPlayAmountShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getAdvPlayAmount(), asset)));
            object.put("totalAdvPlayAmount", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalAdvPlayAmount(), asset));
            object.put("totalAdvPlayAmountShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalAdvPlayAmount(), asset)));
            object.put("totalAdvSettle", StaticFunHandle.getAssetAmount(typeRoomPo.getTotalAdvSettle(), asset));
            object.put("totalAdvSettleShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalAdvSettle(), asset)));
            object.put("advSettle", StaticFunHandle.getAssetAmount(typeRoomPo.getAdvSettle(), asset));
            object.put("advSettleShow", convertAmount(StaticFunHandle.getAssetAmount(typeRoomPo.getAdvSettle(), asset)));
            object.put("advFees", typeRoomPo.getAdvFees());
            object.put("advFeesShow", convertAmount(typeRoomPo.getAdvFees()));
            object.put("totalAdvFees", typeRoomPo.getTotalAdvFees());
            object.put("totalAdvFeesShow", convertAmount(typeRoomPo.getTotalAdvFees()));
            object.put("advPrtpFees", typeRoomPo.getAdvPrtpFees());
            object.put("advPrtpFeesShow", convertAmount(typeRoomPo.getAdvPrtpFees()));
            object.put("totalAdvPrtpFees", typeRoomPo.getTotalAdvPrtpFees());
            object.put("totalAdvPrtpFeesShow", convertAmount(typeRoomPo.getTotalAdvPrtpFees()));
            object.put("updateTime", typeRoomPo.getUpdateTime());
            newList.add(object);
        }
        result.put("records", newList);
        responseVo.setData(result);
        return responseVo;
    }

    @RequestMapping(value = "downExcel")
    public void userTxDownExcel(HttpServletRequest request, HttpServletResponse response) {
        UserConfigPo configPo = userConfigService.selectOne(USER_CONFIG_ID);
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
        String fileName = date + "分类型房间每日指标.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("分类型房间每日指标");
        List<DailyTypeRoomPo> typeRoomPoList = dailyTypeRoomService.selectList(bZeroTime, eZeroTime);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("pvp抽成收入");
        list.add("累计pvp抽成收入");
        list.add("PVP投注总额");
        list.add("累计PVP投注总额");
        list.add("PVP派奖总额");
        list.add("累计PVP派奖总额");
        list.add("PVP房间手续费");
        list.add("累计PVP房间手续费");
        list.add("PVP房间手续费收入");
        list.add("累计PVP房间手续费收入");
        list.add("PVD投注总额");
        list.add("PVD派奖总额");
        list.add("累计PVD投注总额");
        list.add("累计PVD派奖总额");
        list.add("PVD房间手续费");
        list.add("累计PVD房间手续费");
        list.add("PVD房间手续费收入");
        list.add("累计PVD房间手续费收入");
        list.add("高级投注总额");
        list.add("累计高级投注总额");
        list.add("高级派奖总额");
        list.add("累计高级派奖总额");
        list.add("高级房间手续费");
        list.add("累计高级房间手续费");
        list.add("高级房间手续费收入");
        list.add("累计高级房间手续费收入");
        List<Integer> titleCol = new LinkedList<>();
        for (int i = 1; i <= 27; i++) {
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
        for (int i = 0; i < typeRoomPoList.size(); i++) {
            DailyTypeRoomPo typeRoomPo = typeRoomPoList.get(i);
            // 表示创建第一行
            row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            //日期
            cell = row.createCell(0);
            cell.setCellValue(typeRoomPo.getTime());
            cell.setCellStyle(style);
            //pvp抽成收入
            cell = row.createCell(1);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getPvpProfit(), asset).toString());
            cell.setCellStyle(style);
            //累计pvp抽成收入
            cell = row.createCell(2);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpProfit(), asset).toString());
            cell.setCellStyle(style);
            //PVP投注总额
            cell = row.createCell(3);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getPvpPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计PVP投注总额
            cell = row.createCell(4);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            //PVP派奖总额
            cell = row.createCell(5);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getPvpSettle(), asset).toString());
            cell.setCellStyle(style);
            //累计PVP派奖总额
            cell = row.createCell(6);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvpSettle(), asset).toString());
            cell.setCellStyle(style);
            //PVP房间手续费
            cell = row.createCell(7);
            cell.setCellValue(typeRoomPo.getPvpFees().toString());
            cell.setCellStyle(style);
            //累计PVP房间手续费
            cell = row.createCell(8);
            cell.setCellValue(typeRoomPo.getTotalPvpFees().toString());
            cell.setCellStyle(style);
            //PVP房间手续费收入
            cell = row.createCell(9);
            cell.setCellValue(typeRoomPo.getPvpPrtpFees().toString());
            cell.setCellStyle(style);
            //累计PVP房间手续费收入
            cell = row.createCell(10);
            cell.setCellValue(typeRoomPo.getTotalPvpPrtpFees().toString());
            cell.setCellStyle(style);
            //PVD投注总额
            cell = row.createCell(11);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getPvdPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            //PVD派奖总额
            cell = row.createCell(12);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getPvdSettle(), asset).toString());
            cell.setCellStyle(style);
            //累计PVD投注总额
            cell = row.createCell(13);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvdPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计PVD派奖总额
            cell = row.createCell(14);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalPvdSettle(), asset).toString());
            cell.setCellStyle(style);
            //PVD房间手续费
            cell = row.createCell(15);
            cell.setCellValue(typeRoomPo.getPvdFees().toString());
            cell.setCellStyle(style);
            //累计PVD房间手续费
            cell = row.createCell(16);
            cell.setCellValue(typeRoomPo.getTotalPvdFees().toString());
            cell.setCellStyle(style);
            //PVD房间手续费收入
            cell = row.createCell(17);
            cell.setCellValue(typeRoomPo.getPvdPrtpFees().toString());
            cell.setCellStyle(style);
            //累计PVD房间手续费收入
            cell = row.createCell(18);
            cell.setCellValue(typeRoomPo.getTotalPvdPrtpFees().toString());
            cell.setCellStyle(style);
            //高级投注总额
            cell = row.createCell(19);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getAdvPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            //累计高级投注总额
            cell = row.createCell(20);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalAdvPlayAmount(), asset).toString());
            cell.setCellStyle(style);
            //高级派奖总额
            cell = row.createCell(21);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getAdvSettle(), asset).toString());
            cell.setCellStyle(style);
            //累计高级派奖总额
            cell = row.createCell(22);
            cell.setCellValue(StaticFunHandle.getAssetAmount(typeRoomPo.getTotalAdvSettle(), asset).toString());
            cell.setCellStyle(style);
            //高级房间手续费
            cell = row.createCell(23);
            cell.setCellValue(typeRoomPo.getAdvFees().toString());
            cell.setCellStyle(style);
            //累计高级房间手续费
            cell = row.createCell(24);
            cell.setCellValue(typeRoomPo.getTotalAdvFees().toString());
            cell.setCellStyle(style);
            //高级房间手续费收入
            cell = row.createCell(25);
            cell.setCellValue(typeRoomPo.getAdvPrtpFees().toString());
            cell.setCellStyle(style);
            //累计高级房间手续费收入
            cell = row.createCell(26);
            cell.setCellValue(typeRoomPo.getTotalAdvPrtpFees().toString());
            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == typeRoomPoList.size()) {
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
