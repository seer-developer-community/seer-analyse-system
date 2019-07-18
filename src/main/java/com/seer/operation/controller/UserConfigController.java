package com.seer.operation.controller;

import com.seer.operation.entity.AccountBasePo;
import com.seer.operation.entity.DailyAccountDetailsPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.DataCacheManager;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.AccountBaseService;
import com.seer.operation.service.DailyAccountService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static com.seer.operation.request.Constants.CONFIG_SEER_BOT_SPLIT;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;
import static com.seer.operation.request.STATUS.USER_IS_BOT_PLAYER;
import static com.seer.operation.response.ResponseVo.ResultParamsNull;
import static com.seer.operation.response.ResponseVo.ResultSuccess;

@RestController
@RequestMapping(value = "seer/config")
public class UserConfigController {
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private AccountBaseService baseService;
    @Autowired
    private DailyAccountService accountService;
    @Autowired
    private DataCacheManager cacheManager;

    @RequestMapping(value = "insert")
    public ResponseVo insert(UserConfigPo userConfig) {
        if (StringUtils.isBlank(userConfig.getAssets())) {
            return ResultParamsNull();
        }
        userConfigService.insert(userConfig);
        return ResultSuccess();
    }

    @RequestMapping(value = "select")
    public ResponseVo<UserConfigPo> select() {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        UserConfigPo list = userConfigService.selectOne(USER_CONFIG_ID);
        responseVo.setData(list);
        return responseVo;
    }

    @RequestMapping(value = "update")
    public ResponseVo update(UserConfigPo configPo) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        UserConfigPo userConfigPo = userConfigService.selectOne(USER_CONFIG_ID);
        if (null == userConfigPo) {
            return ResponseVo.ResultFailed("配置不存在");
        }
        if (StringUtils.isBlank(configPo.getDefaultAsset())) {
            return ResponseVo.ResultFailed("默认显示资产不能为空");
        }
        if (StringUtils.isBlank(configPo.getDefaultOwner())) {
            return ResponseVo.ResultFailed("默认显示的房主账户不能为空");
        }
        String oldBots = userConfigPo.getSeerBots();
        String newBots = configPo.getSeerBots();
        if (!oldBots.equals(newBots)) {
            List<String> old = Arrays.asList(oldBots.split(CONFIG_SEER_BOT_SPLIT));
            List<String> cur = Arrays.asList(newBots.split(CONFIG_SEER_BOT_SPLIT));
            for (int i = 0; i < cur.size(); i++) {
                boolean isExist = false;
                for (String str : old) {
                    if (str.equals(cur.get(i))) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    AccountBasePo basePo = baseService.selectById(cur.get(i));
                    if (basePo != null) {
                        basePo = new AccountBasePo();
                        basePo.setId(cur.get(i));
                        basePo.setIsSeerbot(USER_IS_BOT_PLAYER.getCode());
                        baseService.updateById(basePo);
                    }
                    List<DailyAccountDetailsPo> list = accountService.selectListByIssuer(cur.get(i));
                    for (DailyAccountDetailsPo detailsPo : list) {
                        DailyAccountDetailsPo detailsPo1 = new DailyAccountDetailsPo();
                        detailsPo1.setIsBot(USER_IS_BOT_PLAYER.getCode());
                        detailsPo1.setId(detailsPo.getId());
                        accountService.updateById(detailsPo1);
                    }
                }
            }
        }
        userConfigPo.setFaucetAccounts(configPo.getFaucetAccounts().replaceAll("，", ","));
        userConfigPo.setGateways(configPo.getGateways().replaceAll("，", ","));
        userConfigPo.setAssets(configPo.getAssets().replaceAll("，", ","));
        userConfigPo.setHouses(configPo.getHouses().replaceAll("，", ","));
        userConfigPo.setSeerBots(configPo.getSeerBots().replaceAll("，", ","));
        userConfigPo.setDefaultAsset(configPo.getDefaultAsset());
        userConfigPo.setDefaultOwner(configPo.getDefaultOwner());
        userConfigPo.setWhiteAccounts(configPo.getWhiteAccounts());
        userConfigService.updateById(userConfigPo);
        //更新config缓存
        cacheManager.updateConfigCache(USER_CONFIG_ID);
        return responseVo;
    }
}
