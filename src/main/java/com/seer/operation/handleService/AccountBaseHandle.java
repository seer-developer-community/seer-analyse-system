package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.AccountBasePo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.*;
import com.seer.operation.service.AccountBaseService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.handleService.StaticFunHandle.initAssetAmount;
import static com.seer.operation.request.Constants.*;
import static com.seer.operation.request.STATUS.*;

@Service
public class AccountBaseHandle {
    @Autowired
    private AccountBaseService baseService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;

    @Transactional
    public void initBase(BigInteger current, CreateAccountVo createAccountVo, Long blockTime, FeeBo feeBo) {
        AccountBasePo basePo = baseService.selectById(createAccountVo.getId());
        if (null != basePo) {
            return;
        }
        basePo = new AccountBasePo();
        basePo.setId(createAccountVo.getId());
        basePo.setName(createAccountVo.getName());
        basePo.setRegistrar(createAccountVo.getRegistrar());
        basePo.setReferrer(createAccountVo.getReferrer());
        basePo.setReferrerPercent(createAccountVo.getReferrerPercent().intValue());
        //注册时间戳，东八区
        basePo.setRegTime(blockTime);
        basePo.setRegistered(0);
        basePo.setIsPlayer(0);
        basePo.setIsSeerbot(0);
        basePo.setAccountPrtpCount(0);
        basePo.setClaimedFaucetProfit(BigDecimal.ZERO);
        basePo.setClaimedFeesProfit(BigDecimal.ZERO);
        //手续费总消耗
        basePo.setTotalFees(BigDecimal.ZERO);
        //用户转账支出，初始化为config.assets
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        JSONArray array = new JSONArray();
        if (null == configPo || StringUtils.isBlank(configPo.getAssets())) {
            JSONObject object = new JSONObject();
            object.put("asset", SEER_ASSET);
            object.put("amount", "0.00000");
            array.add(object);
        } else {
            array = initAssetAmount(configPo.getAssets());
        }
        basePo.setTotalTransferCountFees(array.toJSONString());
        //初始化投注金额
        basePo.setAccountPrtpAmount(array.toJSONString());
        //最近投注时间
        basePo.setRecentPlayTime(0L);
        basePo.setLastBlock(current);
        baseService.insert(basePo);
    }

    @Transactional
    public void updateBy50(BigInteger current, PredictionVo predictionVo, FeeBo feeBo, JSONObject result, boolean isCheck) {
        AccountBasePo basePo = baseService.selectById(predictionVo.getIssuer());
        if (null == basePo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(basePo.getLastBlock()) <= 0) {
                return;
            }
        }
        //更新最近投注时间
        basePo.setRecentPlayTime(System.currentTimeMillis());
        //更改投注用户
        if (basePo.getIsPlayer() == USER_IS_NOT_PLAYER.getCode()) {
            basePo.setIsPlayer(USER_IS_PLAYER.getCode());
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        //更改是否为机器人
        if (basePo.getIsSeerbot() == USER_IS_NOT_BOT_PLAYER.getCode()) {
            if (null != configPo) {
                boolean isBot = configPo.getSeerBots().contains(predictionVo.getIssuer());
                if (isBot) {
                    basePo.setIsSeerbot(USER_IS_BOT_PLAYER.getCode());
                }
            }
        }
        //更改投注次数
        basePo.setAccountPrtpCount(basePo.getAccountPrtpCount() + 1);
        //更改玩家投注金额
        String asset = result.getString("asset_id");
        String amountStr = result.getJSONArray("deltas").getJSONArray(0).getString(1);
        if (amountStr.startsWith("-")) {
            amountStr = amountStr.split("-")[1];
        }
        BigDecimal amount = new BigDecimal(amountStr).divide(SEER_DECIMALS).setScale(5);
        if (null != configPo) {
            boolean isContainsAsset = configPo.getAssets().contains(asset);
            if (isContainsAsset) {
                JSONArray array = StaticFunHandle.addAssetAmount(asset, configPo.getAssets(), basePo.getAccountPrtpAmount(), amount);
                basePo.setAccountPrtpAmount(array.toJSONString());
            }
        }
        //更改手续费总耗
        if (feeBo.getIsSeer()) {
            basePo.setTotalFees(basePo.getTotalFees().add(feeBo.getRealAmount()));
        } else {
            basePo.setTotalFees(basePo.getTotalFees().add(feeBo.getOtherAssetBaseSeer()));
        }
        basePo.setLastBlock(current);
        baseService.updateById(basePo);
    }

    @Transactional
    public void updateBy4(BigInteger current, CreateAccountVo createAccountVo, boolean isCheck) {
        AccountBasePo basePo = baseService.selectById(createAccountVo.getRegistrar());
        if (null == basePo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(basePo.getLastBlock()) <= 0) {
                return;
            }
        }
        basePo.setRegistered(basePo.getRegistered() + 1);
        basePo.setLastBlock(current);
        baseService.updateById(basePo);

    }

    @Transactional
    public void updateBy0(BigInteger current, TransferVo transferVo, FeeBo feeBo, boolean isCheck) {
        AccountBasePo basePo = baseService.selectById(transferVo.getFrom());
        if (null == basePo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(basePo.getLastBlock()) <= 0) {
                return;
            }
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String totalTransferCountFees = basePo.getTotalTransferCountFees();
        BigDecimal amount = new BigDecimal(transferVo.getAmountAmount()).divide(SEER_DECIMALS).setScale(5);
        JSONArray array = new JSONArray();
        if (null != configPo) {
            if (configPo.getAssets().contains(transferVo.getAmountAssetId())) {
                array = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), configPo.getAssets()
                        , totalTransferCountFees, amount);
                totalTransferCountFees = array.toJSONString();
            }
        }
        //手续费计入1.3.0
        if (feeBo.getIsSeer()) {
            array = StaticFunHandle.addSeerAsset(totalTransferCountFees, feeBo.getRealAmount());
        } else {
            array = StaticFunHandle.addSeerAsset(totalTransferCountFees, feeBo.getOtherAssetBaseSeer());
        }
        totalTransferCountFees = array.toJSONString();
        basePo.setTotalTransferCountFees(totalTransferCountFees);
        //手续费总消耗
        if (feeBo.getIsSeer()) {
            basePo.setTotalFees(basePo.getTotalFees().add(feeBo.getRealAmount()));
        } else {
            basePo.setTotalFees(basePo.getTotalFees().add(feeBo.getOtherAssetBaseSeer()));
        }
        basePo.setLastBlock(current);
        baseService.updateById(basePo);
    }

    @Transactional
    public void updateBy30(Opcode30Vo opcode30Vo, BigInteger current, boolean isCheck) {
        AccountBasePo basePo = baseService.selectById(opcode30Vo.getOwner());
        if (null == basePo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(basePo.getLastBlock()) <= 0) {
                return;
            }
        }
        basePo.setClaimedFaucetProfit(basePo.getClaimedFaucetProfit().add(opcode30Vo.getAmountRealAmount()));
        basePo.setLastBlock(current);
        baseService.updateById(basePo);
    }

    @Transactional
    public void updateBy54(Opcode54Vo opcode54Vo, BigInteger current, boolean isCheck) {
        AccountBasePo basePo = baseService.selectById(opcode54Vo.getHouse());
        if (null == basePo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(basePo.getLastBlock()) <= 0) {
                return;
            }
        }
        basePo.setClaimedFeesProfit(basePo.getClaimedFeesProfit().add(opcode54Vo.getRealClaimFess()));
        basePo.setLastBlock(current);
        baseService.updateById(basePo);
    }

    @Transactional
    public void updateTotalFees(BigInteger current, String issuer, FeeBo feeBo, boolean isCheck) {
        AccountBasePo basePo = baseService.selectById(issuer);
        if (null == basePo) {
            return;
        }
        if (isCheck) {
            if (basePo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (feeBo.getIsSeer()) {
            basePo.setTotalFees(basePo.getTotalFees().add(feeBo.getRealAmount()));
        } else {
            basePo.setTotalFees(basePo.getTotalFees().add(feeBo.getOtherAssetBaseSeer()));
        }
        basePo.setLastBlock(current);
        baseService.updateById(basePo);
    }

}
