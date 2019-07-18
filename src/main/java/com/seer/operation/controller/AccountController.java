package com.seer.operation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.*;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static com.seer.operation.handleService.StaticFunHandle.getAssetAmount;
import static com.seer.operation.handleService.StaticFunHandle.getAssetCount;
import static com.seer.operation.request.Constants.CONFIG_ASSET_SPLIT;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

@RestController
@RequestMapping(value = "seer/account")
public class AccountController {
    @Autowired
    private AccountAuthService authService;
    @Autowired
    private AccountBaseService baseService;
    @Autowired
    private AccountHouseTotalService houseTotalService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private AccountRegisterService registerService;
    @Autowired
    private AccountHouseTypeService houseTypeService;

    @RequestMapping(value = "auth/page")
    public ResponseVo selectAuthPage(Integer current, Integer size, String issuer, String name) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<AccountAuthPo> page = authService.selectPage(current, size, issuer, name);
        responseVo.setData(page);
        return responseVo;
    }

    @RequestMapping(value = "register/page")
    public ResponseVo selectRegisterPage(Integer current, Integer size, String issuer, String name, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<AccountRegisterPo> page = registerService.selectPage(current, size, issuer, name);
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        //返回显示的资产
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        if (StringUtils.isBlank(asset)) {
            object.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            object.put("asset", asset);
        }
        //重构返回对象
        JSONArray newList = new JSONArray();
        List<AccountRegisterPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            AccountRegisterPo registerPo = list.get(i);
            jsonObject.put("id", registerPo.getId());
            jsonObject.put("name", registerPo.getName());
            jsonObject.put("registrar", registerPo.getRegistrar());
            jsonObject.put("players", registerPo.getPlayers());
            jsonObject.put("botPlayers", registerPo.getBotPlayers());
            jsonObject.put("truePlayers", registerPo.getTruePlayers());
            jsonObject.put("botTruePlayers", registerPo.getBotTruePlayers());
            jsonObject.put("depositAmount", getAssetAmount(registerPo.getDepositAmount(), asset));
            jsonObject.put("depositAmountList", JSONArray.parseArray(registerPo.getDepositAmount()));
            jsonObject.put("botDepositAmount", getAssetAmount(registerPo.getBotDepositAmount(), asset));
            jsonObject.put("botDepositAmountList", JSONArray.parseArray(registerPo.getBotDepositAmount()));
            jsonObject.put("depositCount", getAssetCount(registerPo.getDepositCount(), asset));
            jsonObject.put("depositCountList", JSONArray.parseArray(registerPo.getDepositCount()));
            jsonObject.put("botDepositCount", getAssetCount(registerPo.getBotDepositCount(), asset));
            jsonObject.put("botDepositCountList", JSONArray.parseArray(registerPo.getBotDepositCount()));
            jsonObject.put("transferAmount", getAssetAmount(registerPo.getTransferAmount(), asset));
            jsonObject.put("transferAmountList", JSONArray.parseArray(registerPo.getTransferAmount()));
            jsonObject.put("botTransferAmount", getAssetAmount(registerPo.getBotTransferAmount(), asset));
            jsonObject.put("botTransferAmountList", JSONArray.parseArray(registerPo.getBotTransferAmount()));
            jsonObject.put("transferCount", getAssetCount(registerPo.getTransferCount(), asset));
            jsonObject.put("transferCountList", JSONArray.parseArray(registerPo.getTransferCount()));
            jsonObject.put("botTransferCount", getAssetCount(registerPo.getBotTransferCount(), asset));
            jsonObject.put("botTransferCountList", JSONArray.parseArray(registerPo.getBotTransferCount()));
            jsonObject.put("seerbotFees", String.valueOf(registerPo.getSeerbotFees()));
            jsonObject.put("totalRegisteredFees", String.valueOf(registerPo.getTotalRegisteredFees()));
            jsonObject.put("createTime", registerPo.getCreateTime());
            jsonObject.put("updateTime", registerPo.getUpdateTime());
            newList.add(jsonObject);
        }
        object.put("records", newList);
        responseVo.setData(object);
        return responseVo;
    }

    @RequestMapping(value = "base/page")
    public ResponseVo selectBasePage(Integer current, Integer size, String issuer, String name, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<AccountBasePo> page = baseService.selectPage(current, size, issuer, name);
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        //前端动态加载表头
        JSONArray colsArray = new JSONArray();
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
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
        //重构返回对象
        JSONArray newList = new JSONArray();
        List<AccountBasePo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            AccountBasePo basePo = list.get(i);
            jsonObject.put("id", basePo.getId());
            jsonObject.put("name", basePo.getName());
            jsonObject.put("referrer", basePo.getReferrer());
            jsonObject.put("registrar", basePo.getRegistrar());
            jsonObject.put("referrerPercent", basePo.getReferrerPercent());
            jsonObject.put("regTime", basePo.getRegTime());
            jsonObject.put("recentPlayTime", basePo.getRecentPlayTime());
            jsonObject.put("registered", basePo.getRegistered());
            jsonObject.put("isPlayer", basePo.getIsPlayer());
            jsonObject.put("isSeerbot", basePo.getIsSeerbot());
            jsonObject.put("accountPrtpCount", basePo.getAccountPrtpCount());
            jsonObject.put("claimedFaucetProfit", String.valueOf(basePo.getClaimedFaucetProfit()));
            jsonObject.put("claimedFeesProfit", String.valueOf(basePo.getClaimedFeesProfit()));
            jsonObject.put("totalFees", String.valueOf(basePo.getTotalFees()));
            jsonObject.put("updateTime", basePo.getUpdateTime());
            JSONArray prtpAmount = JSON.parseArray(basePo.getAccountPrtpAmount());
            JSONArray transferCountFees = JSON.parseArray(basePo.getTotalTransferCountFees());
            //处理投注额分列显示
            for (int k = 0; k < colsArray.size(); k++) {
                boolean isNotExist = true;
                boolean isNotExistPrtpAmount = true;
                for (int j = 0; j < prtpAmount.size(); j++) {
                    //返回当前指定显示资产
                    if (asset.equals(prtpAmount.getJSONObject(j).getString("asset"))) {
                        jsonObject.put("showPrtpAmount", prtpAmount.getJSONObject(j).getBigDecimal("amount"));
                        isNotExistPrtpAmount = false;
                    }
                    if (colsArray.getJSONObject(k).getString("name").equals(
                            prtpAmount.getJSONObject(j).getString("asset")
                    )) {
                        isNotExist = false;
                    }
                    jsonObject.put("prtp." + prtpAmount.getJSONObject(j).getString("asset"),
                            prtpAmount.getJSONObject(j).getString("amount"));
                }
                if (isNotExist) {
                    jsonObject.put("prtp." + colsArray.getJSONObject(k).getString("name"), "0.00000");
                }
                if (isNotExistPrtpAmount) {
                    jsonObject.put("showPrtpAmount", 0);
                }
            }
            //处理转账支持分列显示
            for (int k = 0; k < colsArray.size(); k++) {
                boolean isNotExist = true;
                boolean isNotExistShow = true;
                for (int j = 0; j < transferCountFees.size(); j++) {
                    //返回当前指定显示资产
                    if (asset.equals(prtpAmount.getJSONObject(j).getString("asset"))) {
                        jsonObject.put("showTransfer", prtpAmount.getJSONObject(j).getBigDecimal("amount"));
                        isNotExistShow = false;
                    }
                    if (colsArray.getJSONObject(k).getString("name").equals(
                            transferCountFees.getJSONObject(j).getString("asset")
                    )) {
                        isNotExist = false;
                    }
                    jsonObject.put("transfer." + transferCountFees.getJSONObject(j).getString("asset"),
                            transferCountFees.getJSONObject(j).getString("amount"));
                }
                if (isNotExist) {
                    jsonObject.put("transfer." + colsArray.getJSONObject(k).getString("name"), "0.00000");
                }
                if (isNotExistShow) {
                    jsonObject.put("showTransfer", 0);
                }
            }
            newList.add(jsonObject);
        }
        object.put("records", newList);
        responseVo.setData(object);
        return responseVo;
    }

    @RequestMapping(value = "house/total/page")
    public ResponseVo selectHouseTotalPage(Integer current, Integer size, String issuer, String name, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<AccountHouseTotalPo> page = houseTotalService.selectPage(current, size, issuer, name);
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        //返回显示的资产
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        if (StringUtils.isBlank(asset)) {
            object.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            object.put("asset", asset);
        }
        //重构返回对象
        JSONArray newList = new JSONArray();
        List<AccountHouseTotalPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            AccountHouseTotalPo houseTotalPo = list.get(i);
            jsonObject.put("id", houseTotalPo.getId());
            jsonObject.put("name", houseTotalPo.getName());
            jsonObject.put("houseAdvprtpFees", String.valueOf(houseTotalPo.getHouseAdvprtpFees()));
            jsonObject.put("houseBotadvprtpFees", String.valueOf(houseTotalPo.getHouseBotadvprtpFees()));
            jsonObject.put("houseSeerbotFees", String.valueOf(houseTotalPo.getHouseSeerbotFees()));
            jsonObject.put("totalPrtpProfit", getAssetAmount(houseTotalPo.getTotalPrtpProfit(), asset));
            jsonObject.put("totalPrtpProfitList", JSONArray.parseArray(houseTotalPo.getTotalPrtpProfit()));
            jsonObject.put("totalPlayAmount", getAssetAmount(houseTotalPo.getTotalPlayAmount(), asset));
            jsonObject.put("totalPlayAmountList", JSONArray.parseArray(houseTotalPo.getTotalPlayAmount()));
            jsonObject.put("totalAdvplayAmount", getAssetAmount(houseTotalPo.getTotalAdvplayAmount(), asset));
            jsonObject.put("totalAdvplayAmountList", JSONArray.parseArray(houseTotalPo.getTotalAdvplayAmount()));
            jsonObject.put("totalBotadvplayAmount", getAssetAmount(houseTotalPo.getTotalBotadvplayAmount(), asset));
            jsonObject.put("totalBotadvplayAmountList", JSONArray.parseArray(houseTotalPo.getTotalBotadvplayAmount()));
            jsonObject.put("totalBotplayAmount", getAssetAmount(houseTotalPo.getTotalBotplayAmount(), asset));
            jsonObject.put("totalBotplayAmountList", JSONArray.parseArray(houseTotalPo.getTotalBotplayAmount()));
            jsonObject.put("houseRooms", houseTotalPo.getHouseRooms());
            jsonObject.put("prtpRooms", houseTotalPo.getPrtpRooms());
            jsonObject.put("activeRooms", houseTotalPo.getActiveRooms());
            jsonObject.put("housePrtpCount", houseTotalPo.getHousePrtpCount());
            jsonObject.put("houseBotprtpCount", houseTotalPo.getHouseBotprtpCount());
            jsonObject.put("housePrtpTimes", houseTotalPo.getHousePrtpTimes());
            jsonObject.put("houseBotprtpTimes", houseTotalPo.getHouseBotprtpTimes());
            jsonObject.put("houseAdvSettle", getAssetAmount(houseTotalPo.getHouseAdvSettle(), asset));
            jsonObject.put("houseAdvSettleList", JSONArray.parseArray(houseTotalPo.getHouseAdvSettle()));
            jsonObject.put("houseBotadvSettle", getAssetAmount(houseTotalPo.getHouseBotadvSettle(), asset));
            jsonObject.put("houseBotadvSettleList", JSONArray.parseArray(houseTotalPo.getHouseBotadvSettle()));
            jsonObject.put("totalAdvFees", String.valueOf(houseTotalPo.getTotalAdvFees()));
            jsonObject.put("createTime", houseTotalPo.getCreateTime());
            jsonObject.put("updateTime", houseTotalPo.getUpdateTime());
            newList.add(jsonObject);
        }
        object.put("records", newList);
        responseVo.setData(object);
        return responseVo;
    }

    @RequestMapping(value = "house/type/page")
    public ResponseVo selectHouseTypePage(Integer current, Integer size, String issuer, String name, String asset) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<AccountHouseTypePo> page = houseTypeService.selectPage(current, size, issuer, name);
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        //返回显示的资产
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        if (StringUtils.isBlank(asset)) {
            object.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            object.put("asset", asset);
        }
        //重构返回对象
        JSONArray newList = new JSONArray();
        List<AccountHouseTypePo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            AccountHouseTypePo typePo = list.get(i);
            jsonObject.put("id", typePo.getId());
            jsonObject.put("name", typePo.getName());
            jsonObject.put("housePvdprtpFees", String.valueOf(typePo.getHousePvdprtpFees()));
            jsonObject.put("housePvpprtpFees", String.valueOf(typePo.getHousePvpprtpFees()));
            jsonObject.put("houseBotpvdprtpFees", String.valueOf(typePo.getHouseBotpvdprtpFees()));
            jsonObject.put("houseBotpvpprtpFees", String.valueOf(typePo.getHouseBotpvpprtpFees()));
            jsonObject.put("totalPvpProfit", getAssetAmount(typePo.getTotalPvpProfit(), asset));
            jsonObject.put("totalPvpProfitList", JSONArray.parseArray(typePo.getTotalPvpProfit()));
            jsonObject.put("totalBotpvpProfit", getAssetAmount(typePo.getTotalBotpvpProfit(), asset));
            jsonObject.put("totalBotpvpProfitList", JSONArray.parseArray(typePo.getTotalBotpvpProfit()));
            jsonObject.put("totalPvdplayAmount", getAssetAmount(typePo.getTotalPvdplayAmount(), asset));
            jsonObject.put("totalPvdplayAmountList", JSONArray.parseArray(typePo.getTotalPvdplayAmount()));
            jsonObject.put("totalPvpplayAmount", getAssetAmount(typePo.getTotalPvpplayAmount(), asset));
            jsonObject.put("totalPvpplayAmountList", JSONArray.parseArray(typePo.getTotalPvpplayAmount()));
            jsonObject.put("totalBotpvdplayAmount", getAssetAmount(typePo.getTotalBotpvdplayAmount(), asset));
            jsonObject.put("totalBotpvdplayAmountList", JSONArray.parseArray(typePo.getTotalBotpvdplayAmount()));
            jsonObject.put("totalBotpvpplayAmount", getAssetAmount(typePo.getTotalBotpvpplayAmount(), asset));
            jsonObject.put("totalBotpvpplayAmountList", JSONArray.parseArray(typePo.getTotalBotpvpplayAmount()));
            jsonObject.put("housePvdSettle", getAssetAmount(typePo.getHousePvdSettle(), asset));
            jsonObject.put("housePvdSettleList", JSONArray.parseArray(typePo.getHousePvdSettle()));
            jsonObject.put("housePvpSettle", getAssetAmount(typePo.getHousePvpSettle(), asset));
            jsonObject.put("housePvpSettleList", JSONArray.parseArray(typePo.getHousePvpSettle()));
            jsonObject.put("totalPvdFees", String.valueOf(typePo.getTotalPvdFees()));
            jsonObject.put("houseBotpvdSettle", getAssetAmount(typePo.getHouseBotpvdSettle(), asset));
            jsonObject.put("houseBotpvdSettleList", JSONArray.parseArray(typePo.getHouseBotpvdSettle()));
            jsonObject.put("houseBotpvpSettle", getAssetAmount(typePo.getHouseBotpvpSettle(), asset));
            jsonObject.put("houseBotpvpSettleList", JSONArray.parseArray(typePo.getHouseBotpvpSettle()));
            jsonObject.put("totalPvpFees", String.valueOf(typePo.getTotalPvdFees()));
            jsonObject.put("createTime", typePo.getCreateTime());
            jsonObject.put("updateTime", typePo.getUpdateTime());
            newList.add(jsonObject);
        }
        object.put("records", newList);
        responseVo.setData(object);
        return responseVo;
    }
}
