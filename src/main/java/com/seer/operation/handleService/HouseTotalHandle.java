package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.AccountHouseTotalPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.CreateAccountVo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.AccountHouseTotalService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.*;

@Service
public class HouseTotalHandle {
    @Autowired
    private AccountHouseTotalService houseTotalService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;

    @Transactional
    public void initHouseTotal(CreateAccountVo createAccountVo, BigInteger current) {
        AccountHouseTotalPo houseTotalPo = houseTotalService.selectById(createAccountVo.getId());
        if (null != houseTotalPo) {
            return;
        }
        houseTotalPo = new AccountHouseTotalPo();
        houseTotalPo.setId(createAccountVo.getId());
        houseTotalPo.setName(createAccountVo.getName());
        houseTotalPo.setHouseAdvprtpFees(BigDecimal.ZERO);
        houseTotalPo.setHouseBotadvprtpFees(BigDecimal.ZERO);
        houseTotalPo.setHouseSeerbotFees(BigDecimal.ZERO);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String initAmountStr = null;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            initAmountStr = StaticFunHandle.initAssetAmount(configPo.getAssets()).toJSONString();
        } else {
            initAmountStr = StaticFunHandle.initAssetAmount(SEER_ASSET).toJSONString();
        }
        houseTotalPo.setTotalPrtpProfit(initAmountStr);
        houseTotalPo.setTotalPlayAmount(initAmountStr);
        houseTotalPo.setTotalAdvplayAmount(initAmountStr);
        houseTotalPo.setTotalBotadvplayAmount(initAmountStr);
        houseTotalPo.setTotalBotplayAmount(initAmountStr);
        houseTotalPo.setHouseRooms(0);
        houseTotalPo.setHousePrtpCount(0);
        houseTotalPo.setHouseBotprtpCount(0);
        houseTotalPo.setHousePrtpTimes(0);
        houseTotalPo.setHouseBotprtpTimes(0);
        houseTotalPo.setHouseAdvSettle(initAmountStr);
        houseTotalPo.setHouseBotadvSettle(initAmountStr);
        houseTotalPo.setTotalAdvFees(BigDecimal.ZERO);
        houseTotalPo.setLastBlock(current);
        houseTotalService.insert(houseTotalPo);
    }

    @Transactional
    public void updateBy50(PredictionVo predictionVo, SeerRoom seerRoom, FeeBo feeBo, String asset, BigDecimal amount, BigInteger current) {
        //参与过的房间列表
        AccountHouseTotalPo totalPoIssuer = houseTotalService.selectById(predictionVo.getIssuer());
        boolean isInPrtpRooms = true;
        if (null != totalPoIssuer) {
            if (null == totalPoIssuer.getPrtpRooms()) {
                isInPrtpRooms = false;
                totalPoIssuer.setPrtpRooms(predictionVo.getRoom());
                totalPoIssuer.setLastBlock(current);
                houseTotalService.updateById(totalPoIssuer);
            } else if (!totalPoIssuer.getPrtpRooms().contains(predictionVo.getRoom())) {
                isInPrtpRooms = false;
                totalPoIssuer.setPrtpRooms(totalPoIssuer.getPrtpRooms() + STRING_SPLIT_CHAR + predictionVo.getRoom());
                totalPoIssuer.setLastBlock(current);
                houseTotalService.updateById(totalPoIssuer);
            }
        }
        AccountHouseTotalPo totalPoOwner = houseTotalService.selectById(seerRoom.getOwner());
        if (null == totalPoOwner || totalPoOwner.getLastBlock().compareTo(current) >= 0) {
            return;
        }
        String assets = SEER_ASSET;
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && !StringUtils.isBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        //房主对应房间的预测总额
        String oldTotalPlay = totalPoOwner.getTotalPlayAmount();
        oldTotalPlay = StaticFunHandle.addAssetAmount(asset, assets, oldTotalPlay, amount).toJSONString();
        totalPoOwner.setTotalPlayAmount(oldTotalPlay);
        if (ADV_ROOM == seerRoom.getRoomType()) {
            //高级手续费总额
            if (feeBo.getIsSeer()) {
                totalPoOwner.setHouseAdvprtpFees(totalPoOwner.getHouseAdvprtpFees().add(feeBo.getRealAmount()));
            } else {
                totalPoOwner.setHouseAdvprtpFees(totalPoOwner.getHouseAdvprtpFees().add(feeBo.getOtherAssetBaseSeer()));
            }
            if (null != configPo && configPo.getSeerBots().contains(predictionVo.getIssuer())) {
                //高级机器人手续费
                if (feeBo.getIsSeer()) {
                    totalPoOwner.setHouseBotadvprtpFees(totalPoOwner.getHouseBotadvprtpFees().add(feeBo.getRealAmount()));
                } else {
                    totalPoOwner.setHouseBotadvprtpFees(totalPoOwner.getHouseBotadvprtpFees().add(feeBo.getOtherAssetBaseSeer()));
                }
                //高级机器人投注总额
                String oldBotAdvPlay = totalPoOwner.getTotalBotadvplayAmount();
                oldBotAdvPlay = StaticFunHandle.addAssetAmount(asset, assets, oldBotAdvPlay, amount).toJSONString();
                totalPoOwner.setTotalBotadvplayAmount(oldBotAdvPlay);
            }
        }
        //房主房间总参与人数
        if (!isInPrtpRooms) {
            totalPoOwner.setHousePrtpCount(totalPoOwner.getHousePrtpCount() + 1);
        }
        if (null != configPo && configPo.getSeerBots().contains(predictionVo.getIssuer())) {
            //房主房间总参与机器人数
            if (!isInPrtpRooms) {
                totalPoOwner.setHouseBotprtpCount(totalPoOwner.getHouseBotprtpCount() + 1);
            }
            //机器人预测手续费
            if (feeBo.getIsSeer()) {
                totalPoOwner.setHouseSeerbotFees(totalPoOwner.getHouseSeerbotFees().add(feeBo.getRealAmount()));
            } else {
                totalPoOwner.setHouseSeerbotFees(totalPoOwner.getHouseSeerbotFees().add(feeBo.getOtherAssetBaseSeer()));
            }
            //房主机器人预测总额
            String oldBotPlay = totalPoOwner.getTotalBotplayAmount();
            oldBotPlay = StaticFunHandle.addAssetAmount(asset, assets, oldBotPlay, amount).toJSONString();
            totalPoOwner.setTotalBotplayAmount(oldBotPlay);
            //房间机器人总参与人次
            totalPoOwner.setHouseBotprtpTimes(totalPoOwner.getHouseBotprtpTimes() + 1);

        }
        if (null != configPo && !configPo.getSeerBots().contains(predictionVo.getIssuer())) {
            //有真人参与的房间列表
            String activeRooms = totalPoOwner.getActiveRooms();
            if (null == activeRooms) {
                activeRooms = predictionVo.getRoom();
                totalPoOwner.setActiveRooms(activeRooms);
            } else if (!activeRooms.contains(predictionVo.getRoom())) {
                activeRooms = activeRooms + STRING_SPLIT_CHAR + predictionVo.getRoom();
                totalPoOwner.setActiveRooms(activeRooms);
            }
        }
        //房间总参与人次
        totalPoOwner.setHousePrtpTimes(totalPoOwner.getHousePrtpTimes() + 1);
        totalPoOwner.setLastBlock(current);
        houseTotalService.updateById(totalPoOwner);
    }

    @Transactional
    public void updateBy49(SeerRoom seerRoom, JSONObject result, BigInteger current, boolean checkBlock, boolean isCheck) {
        String owner = seerRoom.getOwner();
        AccountHouseTotalPo totalPo = houseTotalService.selectById(owner);
        if (null == totalPo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (totalPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        String assetId = result.getString("asset_id");
        BigDecimal ownerAmount = BigDecimal.ZERO;
        JSONArray jsonArray = result.getJSONArray("deltas");
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String oldPrtpProfit = totalPo.getTotalPrtpProfit();
        boolean isExistOwner = false;
        BigInteger deltasAmounts = BigInteger.ZERO;
        BigInteger botAndOwnerAmount = BigInteger.ZERO;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray array = jsonArray.getJSONArray(i);
            String userId = array.getString(0);
            BigInteger amount = array.getBigInteger(1);
            deltasAmounts = deltasAmounts.add(amount);
            if (configPo.getSeerBots().contains(userId)) {
                botAndOwnerAmount = botAndOwnerAmount.add(amount);
            }
            if (userId.equals(owner)) {
                botAndOwnerAmount = botAndOwnerAmount.add(amount);
                ownerAmount = new BigDecimal(amount).divide(SEER_DECIMALS).setScale(5);
                isExistOwner = true;
            }
        }
        if (isExistOwner) {
            //房主总收入
            oldPrtpProfit = StaticFunHandle.addAssetAmount(assetId, assets, oldPrtpProfit, ownerAmount).toJSONString();
            totalPo.setTotalPrtpProfit(oldPrtpProfit);
        }
        if (ADV_ROOM == seerRoom.getRoomType()) {
            //房主高级总派奖
            String oldAdv = totalPo.getHouseAdvSettle();
            BigDecimal deltasAmountsDec = new BigDecimal(deltasAmounts).divide(SEER_DECIMALS).setScale(5);
            oldAdv = StaticFunHandle.addAssetAmount(assetId, assets, oldAdv, deltasAmountsDec).toJSONString();
            totalPo.setHouseAdvSettle(oldAdv);
            //房主高级机器人和房主总派奖
            String oldBotAdv = totalPo.getHouseBotadvSettle();
            BigDecimal botAndOwnerAmountDec = new BigDecimal(botAndOwnerAmount).divide(SEER_DECIMALS).setScale(5);
            oldBotAdv = StaticFunHandle.addAssetAmount(assetId, assets, oldBotAdv, botAndOwnerAmountDec).toJSONString();
            totalPo.setHouseBotadvSettle(oldBotAdv);
        }
        totalPo.setLastBlock(current);
        houseTotalService.updateById(totalPo);
    }

    @Transactional
    public void updateBy46(String issuer, BigInteger current, boolean isCheck) {
        AccountHouseTotalPo totalPo = houseTotalService.selectById(issuer);
        if (null == totalPo) {
            return;
        }
        if (isCheck) {
            if (totalPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        //房主总房间数
        totalPo.setHouseRooms(totalPo.getHouseRooms() + 1);
        totalPo.setLastBlock(current);
        houseTotalService.updateById(totalPo);
    }

    @Transactional
    public void updateAdvFees(SeerRoom seerRoom, FeeBo feeBo, BigInteger current, boolean checkBlock, boolean isCheck) {
        if (seerRoom.getRoomType() != ADV_ROOM) {
            return;
        }
        AccountHouseTotalPo totalPo = houseTotalService.selectById(seerRoom.getOwner());
        if (null == totalPo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (totalPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (feeBo.getIsSeer()) {
            totalPo.setTotalAdvFees(totalPo.getTotalAdvFees().add(feeBo.getRealAmount()));
        } else {
            totalPo.setTotalAdvFees(totalPo.getTotalAdvFees().add(feeBo.getOtherAssetBaseSeer()));
        }
        totalPo.setLastBlock(current);
        houseTotalService.updateById(totalPo);
    }

    @Transactional
    public void updateAdvPlayAmount(SeerRoom seerRoom, String assetId, BigDecimal amount, BigInteger current, boolean isCheck) {
        AccountHouseTotalPo totalPo = houseTotalService.selectById(seerRoom.getOwner());
        if (isCheck) {
            if (totalPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (null == totalPo) {
            return;
        }
        if (ADV_ROOM != seerRoom.getRoomType()) {
            return;
        }
        String oldAdvPlay = totalPo.getTotalAdvplayAmount();
        String assets = SEER_ASSET;
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        oldAdvPlay = StaticFunHandle.addAssetAmount(assetId, assets, oldAdvPlay, amount).toJSONString();
        //房主高级投注总额
        totalPo.setTotalAdvplayAmount(oldAdvPlay);
        totalPo.setLastBlock(current);
        houseTotalService.updateById(totalPo);
    }
}
