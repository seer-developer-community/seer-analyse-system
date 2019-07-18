package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.AccountRegisterPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.CreateAccountVo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.operation.TransferVo;
import com.seer.operation.service.AccountRegisterService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.handleService.StaticFunHandle.*;
import static com.seer.operation.request.Constants.SEER_ASSET;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;
import static com.seer.operation.utils.Times.DAY_TIMESTAMPS;
import static com.seer.operation.utils.Times.getTimesDayZero;

@Service
public class AccountRegisterHandle {
    @Autowired
    private AccountRegisterService registerService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;

    @Transactional
    public void initRegister(CreateAccountVo createAccountVo, BigInteger current, Long time) {
        AccountRegisterPo registerPo = registerService.selectById(createAccountVo.getId());
        if (null != registerPo) {
            return;
        }
        registerPo = new AccountRegisterPo();
        registerPo.setId(createAccountVo.getId());
        registerPo.setName(createAccountVo.getName());
        registerPo.setRegistrar(createAccountVo.getRegistrar());
        registerPo.setRegTime(time);
        registerPo.setPlayers(0);
        registerPo.setBotPlayers(0);
        registerPo.setTruePlayers(0);
        registerPo.setBotTruePlayers(0);
        //初始化八个longtext字段
        JSONArray arrayAmount = new JSONArray();
        JSONArray arrayCount = new JSONArray();
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            arrayAmount = initAssetAmount(configPo.getAssets());
            arrayCount = initAssetCount(configPo.getAssets());
        } else {
            JSONObject object = new JSONObject();
            object.put("asset", SEER_ASSET);
            object.put("amount", "0.00000");
            arrayAmount.add(object);
            object = new JSONObject();
            object.put("asset", SEER_ASSET);
            object.put("count", 0);
            arrayCount.add(object);
        }
        registerPo.setDepositAmount(arrayAmount.toJSONString());
        registerPo.setBotDepositAmount(arrayAmount.toJSONString());
        registerPo.setDepositCount(arrayCount.toJSONString());
        registerPo.setBotDepositCount(arrayCount.toJSONString());
        registerPo.setTransferAmount(arrayAmount.toJSONString());
        registerPo.setBotTransferAmount(arrayAmount.toJSONString());
        registerPo.setTransferCount(arrayCount.toJSONString());
        registerPo.setBotTransferCount(arrayCount.toJSONString());
        registerPo.setSeerbotFees(BigDecimal.ZERO);
        registerPo.setTotalRegisteredFees(BigDecimal.ZERO);
        registerPo.setLastBlock(current);
        registerService.insert(registerPo);
    }

    /**
     * 更新注册用户支出
     *
     * @param createAccountVo
     */
    @Transactional
    public void updateRegisteredFees(BigInteger current, CreateAccountVo createAccountVo, FeeBo feeBo, boolean checkBlock, boolean isCheck) {
        AccountRegisterPo registerPo = registerService.selectById(createAccountVo.getRegistrar());
        if (null == registerPo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (registerPo.getLastBlock().compareTo(current) > 0) {
                return;
            }
        }
        if (feeBo.getIsSeer()) {
            registerPo.setTotalRegisteredFees(registerPo.getTotalRegisteredFees().add(feeBo.getRealAmount()));
        } else {
            registerPo.setTotalRegisteredFees(registerPo.getTotalRegisteredFees().add(feeBo.getOtherAssetBaseSeer()));
        }
        registerPo.setLastBlock(current);
        registerService.updateById(registerPo);
    }

    @Transactional
    public void updateBy0(TransferVo transferVo, BigInteger current, boolean isCheck) {
        AccountRegisterPo registerPo = registerService.selectById(transferVo.getFrom());
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null == configPo || null == registerPo) {
            return;
        }
        if (isCheck) {
            if (registerPo.getLastBlock().compareTo(current) > 0) {
                return;
            }
        }
        if (configPo.getAssets().contains(transferVo.getAmountAssetId())) {
            //处理转账金额和笔数
            AccountRegisterPo po = registerService.selectById(registerPo.getRegistrar());
            if (null != po) {
                po.setTransferAmount(addAssetAmount(transferVo.getAmountAssetId(), configPo.getAssets(),
                        po.getTransferAmount(), transferVo.getAmountRealAmount()).toJSONString());
                po.setTransferCount(addAssetCount(po.getTransferCount(), transferVo.getAmountAssetId(),
                        configPo.getAssets()).toJSONString());
                //处理机器人转账
                if (configPo.getSeerBots().contains(transferVo.getFrom())) {
                    po.setBotTransferAmount(addAssetAmount(transferVo.getAmountAssetId(), configPo.getAssets(),
                            po.getBotTransferAmount(), transferVo.getAmountRealAmount()).toJSONString());
                    po.setBotTransferCount(addAssetCount(po.getBotTransferCount(), transferVo.getAmountAssetId(),
                            configPo.getAssets()).toJSONString());
                }
                po.setLastBlock(current);
                registerService.updateById(po);
            }
        }
        //处理充值
        if (configPo.getGateways().contains(transferVo.getFrom())) {
            dealDeposit(transferVo, current, configPo.getAssets(), configPo.getSeerBots());
        }
    }

    @Transactional
    public void dealDeposit(TransferVo transferVo, BigInteger current, String assets, String seerBots) {
        AccountRegisterPo registerPo = registerService.selectById(transferVo.getTo());
        if (null == registerPo) {
            return;
        }
        if (assets.contains(transferVo.getAmountAssetId())) {
            AccountRegisterPo po = registerService.selectById(registerPo.getRegistrar());
            if (null == po) {
                return;
            }
            //充值金额
            po.setDepositAmount(addAssetAmount(transferVo.getAmountAssetId(), assets,
                    po.getDepositAmount(), transferVo.getAmountRealAmount()).toJSONString());
            //充值笔数
            po.setDepositCount(addAssetCount(po.getDepositCount(), transferVo.getAmountAssetId(), assets).toJSONString());
            if (seerBots.contains(transferVo.getFrom())) {
                //机器人充值金额
                po.setBotDepositAmount(addAssetAmount(transferVo.getAmountAssetId(), assets,
                        po.getBotDepositAmount(), transferVo.getAmountRealAmount()).toJSONString());
                //机器人充值笔数
                po.setBotDepositCount(addAssetCount(po.getBotDepositCount(), transferVo.getAmountAssetId(), assets).toJSONString());
            }
            po.setLastBlock(current);
            registerService.updateById(po);
        }
    }

    @Transactional
    public void updateBy50(PredictionVo predictionVo, FeeBo feeBo, Long time, BigInteger current, boolean isCheck) {
        AccountRegisterPo registerPo = registerService.selectById(predictionVo.getIssuer());
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null == registerPo || null == configPo) {
            return;
        }
        AccountRegisterPo po = registerService.selectById(registerPo.getRegistrar());
        if (null == po) {
            return;
        }
        if (isCheck) {
            if (po.getLastBlock().compareTo(current) > 0) {
                return;
            }
        }
        //update 累积投注用户数
        po.setPlayers(po.getPlayers() + 1);
        if (configPo.getSeerBots().contains(predictionVo.getIssuer())) {
            po.setBotPlayers(po.getBotPlayers() + 1);
            //机器人预测手续费
            if (feeBo.getIsSeer()) {
                po.setSeerbotFees(po.getSeerbotFees().add(feeBo.getRealAmount()));
            } else {
                po.setSeerbotFees(po.getSeerbotFees().add(feeBo.getOtherAssetBaseSeer()));
            }
        }
        //24小时内都投注用户数
        Long nowTime = getTimesDayZero(time);
        if (nowTime - registerPo.getRegTime() <= DAY_TIMESTAMPS) {
            po.setTruePlayers(po.getTruePlayers() + 1);
            //机器人24内投注用户数
            if (configPo.getSeerBots().contains(predictionVo.getIssuer())) {
                po.setBotTruePlayers(po.getBotTruePlayers() + 1);
            }
        }
        po.setLastBlock(current);
        registerService.updateById(po);
    }
}
