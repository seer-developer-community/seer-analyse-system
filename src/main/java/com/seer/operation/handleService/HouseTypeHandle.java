package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.AccountHouseTypePo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.CreateAccountVo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.AccountHouseTypeService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.*;

@Service
public class HouseTypeHandle {
    @Autowired
    private AccountHouseTypeService houseTypeService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;

    @Transactional
    public void initHouseType(CreateAccountVo createAccountVo, BigInteger current) {
        AccountHouseTypePo typePo = houseTypeService.selectById(createAccountVo.getId());
        if (null != typePo) {
            return;
        }
        typePo = new AccountHouseTypePo();
        typePo.setId(createAccountVo.getId());
        typePo.setName(createAccountVo.getName());
        typePo.setHousePvdprtpFees(BigDecimal.ZERO);
        typePo.setHousePvpprtpFees(BigDecimal.ZERO);
        typePo.setHouseBotpvdprtpFees(BigDecimal.ZERO);
        typePo.setHouseBotpvpprtpFees(BigDecimal.ZERO);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        typePo.setTotalPvpProfit(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalBotpvpProfit(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalPvdplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalPvpplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalBotpvdplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalBotpvpplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setHousePvdSettle(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setHousePvpSettle(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalPvdFees(BigDecimal.ZERO);
        typePo.setHouseBotpvdSettle(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setHouseBotpvpSettle(StaticFunHandle.initAssetAmount(assets).toJSONString());
        typePo.setTotalPvpFees(BigDecimal.ZERO);
        typePo.setLastBlock(current);
        houseTypeService.insert(typePo);
    }

    @Transactional
    public void updateBy49(SeerRoom seerRoom, JSONObject result, BigInteger current, boolean isCheck) {
        AccountHouseTypePo typePo = houseTypeService.selectById(seerRoom.getOwner());
        if (null == typePo) {
            return;
        }
        if (isCheck) {
            if (typePo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String assetId = result.getString("asset_id");
        JSONArray deltas = result.getJSONArray("deltas");
        BigInteger allAmount = BigInteger.ZERO;
        BigInteger botAndOwner = BigInteger.ZERO;
        for (int i = 0; i < deltas.size(); i++) {
            JSONArray array = deltas.getJSONArray(i);
            String userId = array.getString(0);
            BigInteger amount = array.getBigInteger(1);
            allAmount = allAmount.add(amount);
            if (configPo.getSeerBots().contains(userId) || seerRoom.getOwner().equals(userId)) {
                botAndOwner = botAndOwner.add(amount);
            }
        }
        BigDecimal allAmountDec = new BigDecimal(allAmount).divide(SEER_DECIMALS).setScale(5);
        BigDecimal botAndOwnerDec = new BigDecimal(botAndOwner).divide(SEER_DECIMALS).setScale(5);
        if (PVD_ROOM == seerRoom.getRoomType()) {
            //pvd总派奖
            String oldPvdSettle = typePo.getHousePvdSettle();
            oldPvdSettle = StaticFunHandle.addAssetAmount(assetId, assets, oldPvdSettle, allAmountDec).toJSONString();
            typePo.setHousePvdSettle(oldPvdSettle);
            //房主PVD机器人和房主总派奖
            String oldBotPvdSettle = typePo.getHouseBotpvdSettle();
            oldBotPvdSettle = StaticFunHandle.addAssetAmount(assetId, assets, oldBotPvdSettle, botAndOwnerDec).toJSONString();
            typePo.setHouseBotpvdSettle(oldBotPvdSettle);
        } else if (PVP_ROOM == seerRoom.getRoomType()) {
            //pvp总派奖
            String oldPvpSettle = typePo.getHousePvpSettle();
            oldPvpSettle = StaticFunHandle.addAssetAmount(assetId, assets, oldPvpSettle, allAmountDec).toJSONString();
            typePo.setHousePvpSettle(oldPvpSettle);
            //房主PVP机器人和房主总派奖
            String oldBotPvpSettle = typePo.getHouseBotpvpSettle();
            oldBotPvpSettle = StaticFunHandle.addAssetAmount(assetId, assets, oldBotPvpSettle, botAndOwnerDec).toJSONString();
            typePo.setHouseBotpvpSettle(oldBotPvpSettle);
        }
        typePo.setLastBlock(current);
        houseTypeService.updateById(typePo);
    }

    @Transactional
    public void updateBy50(PredictionVo predictionVo, SeerRoom seerRoom, FeeBo feeBo, String deltasAsset, BigDecimal deltasAmount, BigInteger current, boolean checkBlock, boolean isCheck) {
        AccountHouseTypePo typePo = houseTypeService.selectById(seerRoom.getOwner());
        if (null == typePo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (typePo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String seerBots = "";
        String assets = SEER_ASSET;
        if (null != configPo) {
            seerBots = configPo.getSeerBots();
            if (StringUtils.isNotBlank(configPo.getAssets())) {
                assets = configPo.getAssets();
            }
        }
        boolean isBot = seerBots.contains(predictionVo.getIssuer());
        if (PVD_ROOM == seerRoom.getRoomType()) {
            //PVD手续费
            if (feeBo.getIsSeer()) {
                typePo.setHousePvdprtpFees(typePo.getHousePvdprtpFees().add(feeBo.getRealAmount()));
            } else {
                typePo.setHousePvdprtpFees(typePo.getHousePvdprtpFees().add(feeBo.getOtherAssetBaseSeer()));
            }
            if (isBot) {
                //PVD 机器人手续费
                if (feeBo.getIsSeer()) {
                    typePo.setHouseBotpvdprtpFees(typePo.getHouseBotpvdprtpFees().add(feeBo.getRealAmount()));
                } else {
                    typePo.setHouseBotpvdprtpFees(typePo.getHouseBotpvdprtpFees().add(feeBo.getOtherAssetBaseSeer()));
                }
                //PVD 机器人投注总额
                String oldBotPvdPlay = typePo.getTotalBotpvdplayAmount();
                oldBotPvdPlay = StaticFunHandle.addAssetAmount(deltasAsset, assets, oldBotPvdPlay, deltasAmount).toJSONString();
                typePo.setTotalBotpvdplayAmount(oldBotPvdPlay);
            }

            typePo.setLastBlock(current);
            houseTypeService.updateById(typePo);
        } else if (PVP_ROOM == seerRoom.getRoomType()) {
            BigDecimal pvpPercent = new BigDecimal(seerRoom.getRunningOption().getPvpOwnerPercent()).divide(SEER_DECIMALS).setScale(5);
            BigDecimal pvpProfitAmount = deltasAmount.multiply(pvpPercent).setScale(5, BigDecimal.ROUND_HALF_DOWN);
            //PVP手续费
            if (feeBo.getIsSeer()) {
                typePo.setHousePvpprtpFees(typePo.getHousePvpprtpFees().add(feeBo.getRealAmount()));
            } else {
                typePo.setHousePvpprtpFees(typePo.getHousePvpprtpFees().add(feeBo.getOtherAssetBaseSeer()));
            }
            if (isBot) {
                //PVP 机器人手续费
                if (feeBo.getIsSeer()) {
                    typePo.setHouseBotpvpprtpFees(typePo.getHouseBotpvpprtpFees().add(feeBo.getRealAmount()));
                } else {
                    typePo.setHouseBotpvpprtpFees(typePo.getHouseBotpvpprtpFees().add(feeBo.getOtherAssetBaseSeer()));
                }
                //PVP 机器人投注总额
                String oldBotPvpPlay = typePo.getTotalBotpvpplayAmount();
                oldBotPvpPlay = StaticFunHandle.addAssetAmount(deltasAsset, assets, oldBotPvpPlay, deltasAmount).toJSONString();
                typePo.setTotalBotpvpplayAmount(oldBotPvpPlay);
                //PVP 机器人总抽成
                String oldBotPvpProfit = typePo.getTotalBotpvpProfit();
                oldBotPvpProfit = StaticFunHandle.addAssetAmount(deltasAsset, assets, oldBotPvpProfit, pvpProfitAmount).toJSONString();
                typePo.setTotalBotpvpProfit(oldBotPvpProfit);
            }
            //PVP投注总额
            String oldPvpPlay = typePo.getTotalPvpplayAmount();
            oldPvpPlay = StaticFunHandle.addAssetAmount(deltasAsset, assets, oldPvpPlay, deltasAmount).toJSONString();
            typePo.setTotalPvpplayAmount(oldPvpPlay);
            //PVP 总抽成
            String oldPvpProfit = typePo.getTotalPvpProfit();
            oldPvpProfit = StaticFunHandle.addAssetAmount(deltasAsset, assets, oldPvpProfit, pvpProfitAmount).toJSONString();
            typePo.setTotalPvpProfit(oldPvpProfit);

            typePo.setLastBlock(current);
            houseTypeService.updateById(typePo);
        }
    }

    @Transactional
    public void updatePvdPlayAmount(SeerRoom seerRoom, String asset, BigDecimal amount, BigInteger current, boolean checkBlock, boolean isCheck) {
        if (PVD_ROOM != seerRoom.getRoomType()) {
            return;
        }
        AccountHouseTypePo typePo = houseTypeService.selectById(seerRoom.getOwner());
        if (null == typePo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (typePo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String oldPvdPlay = typePo.getTotalPvdplayAmount();
        oldPvdPlay = StaticFunHandle.addAssetAmount(asset, assets, oldPvdPlay, amount).toJSONString();
        typePo.setTotalPvdplayAmount(oldPvdPlay);
        typePo.setLastBlock(current);
        houseTypeService.updateById(typePo);
    }

    @Transactional
    public void updateFees(FeeBo feeBo, SeerRoom seerRoom, BigInteger current, boolean checkBlock, boolean isCheck) {
        AccountHouseTypePo typePo = houseTypeService.selectById(seerRoom.getOwner());
        if (null == typePo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (typePo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (PVD_ROOM == seerRoom.getRoomType()) {
            if (feeBo.getIsSeer()) {
                typePo.setTotalPvdFees(typePo.getTotalPvdFees().add(feeBo.getRealAmount()));
            } else {
                typePo.setTotalPvdFees(typePo.getTotalPvdFees().add(feeBo.getOtherAssetBaseSeer()));
            }
            typePo.setLastBlock(current);
            houseTypeService.updateById(typePo);
        } else if (PVP_ROOM == seerRoom.getRoomType()) {
            if (feeBo.getIsSeer()) {
                typePo.setTotalPvpFees(typePo.getTotalPvpFees().add(feeBo.getRealAmount()));
            } else {
                typePo.setTotalPvpFees(typePo.getTotalPvpFees().add(feeBo.getOtherAssetBaseSeer()));
            }
            typePo.setLastBlock(current);
            houseTypeService.updateById(typePo);
        }
    }
}
