package com.seer.operation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.DailyAccountDetailsPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.DailyAccountService;
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

import static com.seer.operation.request.Constants.CONFIG_ASSET_SPLIT;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;
import static com.seer.operation.request.STATUS.USER_IS_BOT_PLAYER;

@RestController
@RequestMapping(value = "seer/daily/account")
public class DailyAccountController {
    @Autowired
    private DailyAccountService dailyAccountService;
    @Autowired
    private UserConfigService userConfigService;

    @Autowired
    private ExcelUtil excelUtil;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "page")
    public ResponseVo selectPage(Integer current, Integer size, Long beginTime, Long endTime, String grepBot,
                                 String name, String issuer, String asset) {
        Long beginTme = null;
        Long endTme = null;
        if (null != beginTime) {
            beginTime = Times.getTimesDayZero(beginTime);
        }
        if (null != endTime) {
            endTime = Times.getTimesDayZero(endTime);
        }
        IPage<DailyAccountDetailsPo> page = dailyAccountService.selectPage(current, size, grepBot,
                issuer, name, beginTime, endTime);
        //处理动态表头显示
        UserConfigPo configPo = userConfigService.selectOne(USER_CONFIG_ID);
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            List<String> strings = Arrays.asList(configPo.getAssets().split(CONFIG_ASSET_SPLIT));
            for (String str : strings) {
                JSONObject object1 = new JSONObject();
                object1.put("name", str);
                array.add(object1);
            }
        }
        object.put("cols", array);
        //返回显示的资产
        if (StringUtils.isBlank(asset)) {
            object.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            object.put("asset", asset);
        }
        //重构返回对象
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        List<DailyAccountDetailsPo> list = page.getRecords();
        JSONArray newList = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            DailyAccountDetailsPo detailsPo = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("issuer", detailsPo.getIssuer());
            if (detailsPo.getIsBot().equals(USER_IS_BOT_PLAYER.getCode())) {
                jsonObject.put("bot", "是");
            } else {
                jsonObject.put("bot", "不是");
            }
//            if (null == configPo) {
//                if (detailsPo.getIsBot().equals(USER_IS_BOT_PLAYER.getCode())) {
//                    jsonObject.put("bot", "是");
//                } else {
//                    jsonObject.put("bot", "不是");
//                }
//            } else {
//                if (configPo.getSeerBots().contains(detailsPo.getIssuer())) {
//                    jsonObject.put("bot", "是");
//                } else {
//                    jsonObject.put("bot", "不是");
//                }
//            }
            jsonObject.put("name", detailsPo.getName());
            jsonObject.put("time", detailsPo.getTime());
            jsonObject.put("dailyPrtpCount", detailsPo.getDailyPrtpCount());
            jsonObject.put("dailyPrtpAmount", detailsPo.getDailyPrtpAmount());
            JSONArray jsonArray = JSON.parseArray(detailsPo.getDailyPrtpAmount());
            //处理投注额分列显示
            for (int k = 0; k < array.size(); k++) {
                boolean isNotExist = true;
                boolean isNotExistAsset = true;
                for (int j = 0; j < jsonArray.size(); j++) {
                    //返回当前指定显示资产
                    if (asset.equals(jsonArray.getJSONObject(j).getString("asset"))) {
                        jsonObject.put("showAsset", jsonArray.getJSONObject(j).getBigDecimal("amount"));
                        isNotExistAsset = false;
                    }
                    if (array.getJSONObject(k).getString("name").equals(
                            jsonArray.getJSONObject(j).getString("asset")
                    )) {
                        isNotExist = false;
                    }
                    jsonObject.put(jsonArray.getJSONObject(j).getString("asset"),
                            jsonArray.getJSONObject(j).getBigDecimal("amount"));
                }
                if (isNotExist) {
                    jsonObject.put(array.getJSONObject(k).getString("name"), 0);
                }
                if (isNotExistAsset) {
                    jsonObject.put("showAsset", 0);
                }
            }
            jsonObject.put("dailyFee", detailsPo.getDailyFee());
            jsonObject.put("dailyProfit", detailsPo.getDailyProfit());
            jsonObject.put("updateTime", detailsPo.getUpdateTime());
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
        String[] assets = configPo.getAssets().split(CONFIG_ASSET_SPLIT);
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");
        String asset = request.getParameter("asset");
        String grepBot = request.getParameter("grepBot");
        String name = request.getParameter("name");
        String issuer = request.getParameter("issuer");
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
        String fileName = date + "用户统计表.xls";
        if (StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
            fileName = "请选择日期后重新下载.xls";
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("用户每日统计表");
        Long bZeroTime = Times.getTimesDayZero(beginTimes);
        Long eZeroTime = Times.getTimesDayZero(endTimes);
        List<DailyAccountDetailsPo> detailsPoList = dailyAccountService.selectList(bZeroTime, eZeroTime, grepBot, name, issuer);
        List<String> list = new LinkedList<>();
        list.add("日期");
        list.add("用户名");
        list.add("用户ID");
        list.add("投注次数");
        list.add("资产 " + asset + " 投注额");
        //将资产分列展示
//        for (String str : assets) {
//            list.add("资产" + str + "投注额");
//        }
        list.add("手续费");
        list.add("dapp收入");
        list.add("更新时间");
        List<Integer> titleCol = new LinkedList<>();
        titleCol.add(20);
        titleCol.add(20);
        titleCol.add(20);
//        for (int i = 0; i < assets.length; i++) {
//            titleCol.add(30);
//        }
        titleCol.add(20);
        titleCol.add(30);
        titleCol.add(20);
        titleCol.add(20);
        titleCol.add(40);
        excelUtil.createTitle(workbook, sheet, list, titleCol);
        // 设置内容 列样式
        HSSFCellStyle style = workbook.createCellStyle();
        //自动换行
        style.setWrapText(true);
        // 设置字体
        HSSFFont font = workbook.createFont();
        font.setFontName("微软雅黑");
        // 相当于 12px，1 px = 20 short
        font.setFontHeight((short) 240);
        style.setFont(font);
        // 设置 水平、垂直居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        int rowNum = 1;
        for (int i = 0; i < detailsPoList.size(); i++) {
            DailyAccountDetailsPo po = detailsPoList.get(i);
            // 表示创建第一行
            HSSFRow row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
            // 设置第一列内容
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(po.getTime());//日期
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue(po.getName());//用户名
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue(po.getIssuer());//用户id
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue(po.getDailyPrtpCount());//投注次数
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue(StaticFunHandle.getAssetAmount(po.getDailyPrtpAmount(), asset).toString());//投注额度
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue(po.getDailyFee().toString());//手续费
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue(po.getDailyProfit().toString());//dapp收入
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue(Times.formatToDate(po.getUpdateTime()));//更新时间
            cell.setCellStyle(style);
            //将投注额的资产分列显示
//            JSONArray array = JSONArray.parseArray(po.getDailyPrtpAmount());
//            for (int j = 0; j < assets.length; j++) {
//                cell = row.createCell(3 + j);
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
//            cell = row.createCell(2 + assets.length + 1);
//            cell.setCellValue(po.getDailyFee().toString());
//            cell.setCellStyle(style);
//            cell = row.createCell(2 + assets.length + 2);
//            cell.setCellValue(po.getDailyProfit().toString());
//            cell.setCellStyle(style);
//            cell = row.createCell(2 + assets.length + 3);
//            cell.setCellValue(po.getTime());
//            cell.setCellStyle(style);
            rowNum++;
        }
        if (0 == detailsPoList.size()) {
            CellRangeAddress cra = new CellRangeAddress(1, 1, 0, titleCol.size() - 1);
            sheet.addMergedRegion(cra);
            HSSFRow row = sheet.createRow(rowNum);
            row.setHeight((short) 960);//设置行高48px
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
