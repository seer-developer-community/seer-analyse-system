package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.DailyProfitPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.Opcode30Vo;
import com.seer.operation.rpcClient.operation.Opcode54Vo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.operation.TransferVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.DailyProfitService;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.seer.operation.request.Constants.*;

@Service
public class DailyProfitHandle {
    @Autowired
    private DailyProfitService dailyProfitService;
    @Autowired
    private DataCacheManager dataCacheManager;
    @Autowired
    private ClientFunService clientFunService;

    @Transactional
    public void updateBy50(PredictionVo predictionVo, String registrar, SeerRoom seerRoom, FeeBo feeBo,
                           BigInteger block, Long time, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String faucets = "";
        String houses = "";
        String bots = "";
        String assets = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getSeerBots())) {
            bots = configPo.getSeerBots();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        DailyProfitPo newPo = new DailyProfitPo();
        newPo.setTimestamp(dailyProfitPo.getTimestamp());
        if (bots.contains(predictionVo.getIssuer())) {
            //机器人总体支出
            newPo.setAccountBotprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dailyProfitPo.getAccountBotprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //累计机器人总体支出
            newPo.setTotalAccountBotprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dailyProfitPo.getTotalAccountBotprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
        }
        if (houses.contains(seerRoom.getOwner())) {
            //DAPP所有房间收入
            newPo.setRoomprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dailyProfitPo.getRoomprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //累计DAPP所有房间收入
            newPo.setTotalRoomprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dailyProfitPo.getTotalRoomprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
        }
        if (houses.contains(seerRoom.getOwner()) && bots.contains(predictionVo.getIssuer())) {
            if (SEER_ASSET.equals(predictionVo.getDeltasAsset())) {
                //dapp补贴
                newPo.setDailySubsidy(dailyProfitPo.getDailySubsidy().add(predictionVo.getDeltasAmount()));
                //累计dapp补贴
                newPo.setTotalSubsidy(dailyProfitPo.getTotalSubsidy().add(predictionVo.getDeltasAmount()));
            }
            if (dailyProfitPo.getFeesProfit().compareTo(fee) >= 0) {
                //手续费收入
                newPo.setFeesProfit(dailyProfitPo.getFeesProfit().subtract(fee));
                //Dapp收入
                newPo.setDappProfit(StaticFunHandle.subAssetAmount(SEER_ASSET, dailyProfitPo.getDappProfit(), fee));
            }
            if (dailyProfitPo.getFeesProfit().compareTo(fee) >= 0) {
                //累计手续费收入
                newPo.setFeesProfit(dailyProfitPo.getFeesProfit().subtract(fee));
                //累计dapp收入
                newPo.setTotalDappProfit(StaticFunHandle.subAssetAmount(SEER_ASSET, dailyProfitPo.getTotalDappProfit(), fee));
            }
        }
        if (faucets.equals(registrar) && bots.contains(predictionVo.getIssuer())) {
            //dapp补贴
            newPo.setDailySubsidy(dailyProfitPo.getDailySubsidy().add(fee));
            //累计dapp补贴
            newPo.setTotalSubsidy(dailyProfitPo.getTotalSubsidy().add(fee));
        }
        if (houses.contains(predictionVo.getIssuer()) || faucets.contains(predictionVo.getIssuer())) {
            //dapp补贴
            newPo.setDailySubsidy(dailyProfitPo.getDailySubsidy().add(fee));
            //累计dapp补贴
            newPo.setTotalSubsidy(dailyProfitPo.getTotalSubsidy().add(fee));
        }
        newPo.setLastBlock(block.intValue());
        dailyProfitService.updateById(newPo);
    }

    @Transactional
    public void updateBy49(String issuer, SeerRoom seerRoom, JSONObject result, FeeBo feeBo, BigInteger block, Long time,
                           boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String faucets = "";
        String houses = "";
        String bots = "";
        String assets = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getSeerBots())) {
            bots = configPo.getSeerBots();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        if (houses.contains(seerRoom.getOwner())) {
            String assetId = result.getString("asset_id");
            JSONArray deltas = result.getJSONArray("deltas");
            BigInteger allAmount = BigInteger.ZERO;
            BigInteger botAmount = BigInteger.ZERO;
            BigInteger ownerAmount = BigInteger.ZERO;
            for (int i = 0; i < deltas.size(); i++) {
                JSONArray array = deltas.getJSONArray(i);
                String userId = array.getString(0);
                BigInteger amount = array.getBigInteger(1);
                allAmount = allAmount.add(amount);
                if (bots.contains(userId)) {
                    botAmount = botAmount.add(amount);
                }
                if (seerRoom.getOwner().equals(userId)) {
                    ownerAmount = ownerAmount.add(amount);
                }
            }
            BigDecimal amountDec = new BigDecimal(allAmount.subtract(botAmount).subtract(ownerAmount))
                    .divide(SEER_DECIMALS).setScale(5);
            BigDecimal ownerAmountDec = new BigDecimal(ownerAmount).divide(SEER_DECIMALS).setScale(5);
            String oldSettle = dailyProfitPo.getRoomprtpSettle();
            String totalOldSettle = dailyProfitPo.getTotalRoomprtpSettle();
            String oldDappProfit = dailyProfitPo.getDappProfit();
            String oldTotalDappProfit = dailyProfitPo.getTotalDappProfit();
            DailyProfitPo newPo = new DailyProfitPo();
            newPo.setTimestamp(zeroTime);
            //DAPP所有房间支出
            oldSettle = StaticFunHandle.addAssetAmount(assetId, assets, oldSettle, amountDec).toJSONString();
            newPo.setRoomprtpSettle(oldSettle);
            //累计DAPP所有房间支出
            totalOldSettle = StaticFunHandle.addAssetAmount(assetId, assets, totalOldSettle, amountDec).toJSONString();
            newPo.setTotalRoomprtpSettle(totalOldSettle);
            //Dapp收入
            oldDappProfit = StaticFunHandle.addAssetAmount(assetId, assets, oldDappProfit, ownerAmountDec).toJSONString();
            newPo.setDappProfit(oldDappProfit);
            //累计dapp收入
            oldTotalDappProfit = StaticFunHandle.addAssetAmount(assetId, assets, oldTotalDappProfit, ownerAmountDec).toJSONString();
            newPo.setTotalDappProfit(oldTotalDappProfit);
            newPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(newPo);
        }
        if (houses.contains(issuer) || faucets.contains(issuer)) {
            BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
            BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
            DailyProfitPo newPo = new DailyProfitPo();
            newPo.setTimestamp(zeroTime);
            //dapp补贴
            newPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            newPo.setTotalSubsidy(oldTotalSub.add(fee));
            newPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(newPo);
        }
    }

    @Transactional
    public void updateBy54(Opcode54Vo opcode54Vo, FeeBo feeBo, BigInteger block,
                           Long time, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String assets = "";
        String houses = "";
        String faucets = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        if (houses.contains(opcode54Vo.getIssuer())) {
            DailyProfitPo newPo = new DailyProfitPo();
            newPo.setTimestamp(zeroTime);
            BigDecimal amount = opcode54Vo.getRealClaimFess();
            DailyProfitPo last = dailyProfitService.selectLastOne();
            Long collectedFees = clientFunService.getHouseByAccount(opcode54Vo.getIssuer());
            BigDecimal value = new BigDecimal(collectedFees).divide(SEER_DECIMALS).setScale(5);
            if (null != last) {
                Long lastAmount = StaticFunHandle.getHouseAmount(last.getCollectedFees(), opcode54Vo.getIssuer());
                if (collectedFees > lastAmount) {
                    value = new BigDecimal(collectedFees - lastAmount).divide(SEER_DECIMALS).setScale(5);
                    amount = amount.add(value);
                } else if (collectedFees < lastAmount) {
                    amount = amount.add(value);
                }
            } else {
                amount = amount.add(value);
            }
            //更新今日待领取手续费分成
            newPo.setCollectedFees(StaticFunHandle.updateHouseAmount(dailyProfitPo.getCollectedFees(), opcode54Vo.getIssuer(), collectedFees));
            //手续费收入
            newPo.setFeesProfit(dailyProfitPo.getFeesProfit().add(amount));
            //Dapp收入
            newPo.setDappProfit(StaticFunHandle.addAssetAmount(SEER_ASSET, assets, dailyProfitPo.getDappProfit(), amount).toJSONString());
            //累计手续费收入
            newPo.setTotalFeesProfit(dailyProfitPo.getTotalFeesProfit().add(amount));
            //累计dapp收入
            newPo.setTotalDappProfit(StaticFunHandle.addAssetAmount(SEER_ASSET, assets, dailyProfitPo.getTotalDappProfit(), amount).toJSONString());
            if (houses.contains(opcode54Vo.getIssuer()) || faucets.contains(opcode54Vo.getIssuer())) {
                //dapp补贴
                newPo.setDailySubsidy(dailyProfitPo.getDailySubsidy().add(fee));
                //累计dapp补贴
                newPo.setTotalSubsidy(dailyProfitPo.getTotalSubsidy().add(fee));
            }
            newPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(newPo);
        }
    }

    @Transactional
    public void updateBy30(Opcode30Vo opcode30Vo, FeeBo feeBo, BigInteger block, Long time, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String faucets = "";
        String houses = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
        BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
        if (faucets.contains(opcode30Vo.getOwner())) {
            Long allowedWithdraw = clientFunService.getAllowedWithdraw(opcode30Vo.getOwner());
            BigDecimal profit = dailyProfitPo.getFaucetProfit();
            BigDecimal totalProfit = dailyProfitPo.getTotalFaucetProfit();
            DailyProfitPo last = dailyProfitService.selectLastOne();
            BigDecimal allowedValue = new BigDecimal(allowedWithdraw).divide(SEER_DECIMALS).setScale(5);
            if (null != last) {
                Long lastAmount = StaticFunHandle.getHouseAmount(last.getAllowedWithdraw(), opcode30Vo.getOwner());
                if (allowedWithdraw >= lastAmount) {
                    allowedValue = new BigDecimal(allowedWithdraw - lastAmount).divide(SEER_DECIMALS).setScale(5);
                }
            }
            profit = profit.add(allowedValue);
            totalProfit = totalProfit.add(allowedValue);
            profit = profit.add(opcode30Vo.getAmountRealAmount());
            totalProfit = totalProfit.add(opcode30Vo.getAmountRealAmount());
            String oldDappProfit = dailyProfitPo.getDappProfit();
            String oldTotalDappProfit = dailyProfitPo.getTotalDappProfit();
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //DAPP水龙头收入
            dailyProfitPo.setFaucetProfit(profit);
            //累计DAPP水龙头收入
            dailyProfitPo.setTotalFaucetProfit(totalProfit);
            //Dapp收入
            oldDappProfit = StaticFunHandle.addSeerAsset(oldDappProfit, profit).toJSONString();
            dailyProfitPo.setDappProfit(oldDappProfit);
            //累计dapp收入
            oldTotalDappProfit = StaticFunHandle.addSeerAsset(oldTotalDappProfit, totalProfit).toJSONString();
            dailyProfitPo.setTotalDappProfit(oldTotalDappProfit);
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
            return;
        }
        if (houses.contains(opcode30Vo.getOwner())) {
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
        }
    }

    @Transactional
    public void updateBy4(String registrar, FeeBo feeBo, BigInteger block, Long time, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String faucets = "";
        String houses = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        if (faucets.contains(registrar)) {
            BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
            BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
            BigDecimal oldRFees = dailyProfitPo.getRegisteredFees();
            BigDecimal oldTotalRFees = dailyProfitPo.getTotalRegisteredFees();
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //DAPP注册用户支出
            dailyProfitPo.setRegisteredFees(oldRFees.add(fee));
            //累计DAPP注册用户支出
            dailyProfitPo.setTotalRegisteredFees(oldTotalRFees.add(fee));
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
            return;
        }
        if (houses.contains(registrar)) {
            BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
            BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
        }
    }

    @Transactional
    public void updateBy0(TransferVo transferVo, FeeBo feeBo, BigInteger block, Long time, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String faucets = "";
        String assets = "";
        String houses = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        if (faucets.contains(transferVo.getFrom())) {
            String oldTCFee = dailyProfitPo.getTransferCountFees();
            String oldTotalTCFee = dailyProfitPo.getTotalTransferCountFees();
            oldTCFee = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, oldTCFee,
                    transferVo.getAmountRealAmount()).toJSONString();
            oldTCFee = StaticFunHandle.addSeerAsset(oldTCFee, fee).toJSONString();
            oldTotalTCFee = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, oldTotalTCFee,
                    transferVo.getAmountRealAmount()).toJSONString();
            oldTotalTCFee = StaticFunHandle.addSeerAsset(oldTotalTCFee, fee).toJSONString();
            BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
            BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //DAPP新注册用户转账支出
            dailyProfitPo.setTransferCountFees(oldTCFee);
            //累计DAPP新注册用户转账支出
            dailyProfitPo.setTotalTransferCountFees(oldTotalTCFee);
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
            return;
        }
        if (houses.contains(transferVo.getFrom())) {
            BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
            BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
        }
    }

    @Transactional
    public void updateSubsidy(String issuer, FeeBo feeBo, BigInteger block, Long time, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTime = Times.getTimesDayZero(time);
        String faucets = "";
        String houses = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getFaucetAccounts())) {
            faucets = configPo.getFaucetAccounts();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null == dailyProfitPo) {
            initDailyProfit(zeroTime);
            dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        }
        if (checkBlock && isCheck) {
            if (dailyProfitPo.getLastBlock() >= block.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        if (houses.contains(issuer) || faucets.contains(issuer)) {
            BigDecimal oldSub = dailyProfitPo.getDailySubsidy();
            BigDecimal oldTotalSub = dailyProfitPo.getTotalSubsidy();
            dailyProfitPo = new DailyProfitPo();
            dailyProfitPo.setTimestamp(zeroTime);
            //dapp补贴
            dailyProfitPo.setDailySubsidy(oldSub.add(fee));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(oldTotalSub.add(fee));
            dailyProfitPo.setLastBlock(block.intValue());
            dailyProfitService.updateById(dailyProfitPo);
        }
    }

    @Transactional
    public void initDailyProfit(Long times) {
        Long zeroTime = Times.getTimesDayZero(times);
        DailyProfitPo dailyProfitPo = dailyProfitService.selectOneByTimestamp(zeroTime);
        if (null != dailyProfitPo) {
            return;
        }
        String assets = SEER_ASSET;
        UserConfigPo configPo = dataCacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        DailyProfitPo last = dailyProfitService.selectLastOne();
        String initAsset = StaticFunHandle.initAssetAmount(assets).toJSONString();
        BigDecimal initDec = BigDecimal.ZERO.setScale(5);
        dailyProfitPo = new DailyProfitPo();
        dailyProfitPo.setTimestamp(zeroTime);
        dailyProfitPo.setTime(Times.formatDateByTimes(zeroTime));
        dailyProfitPo.setDappProfit(initAsset);
        dailyProfitPo.setTotalDappProfit(initAsset);
        dailyProfitPo.setFaucetProfit(initDec);
        dailyProfitPo.setTotalFaucetProfit(initDec);
        dailyProfitPo.setFeesProfit(initDec);
        dailyProfitPo.setTotalFeesProfit(initDec);
        dailyProfitPo.setDailySubsidy(initDec);
        dailyProfitPo.setTotalSubsidy(initDec);
        dailyProfitPo.setAccountBotprtpAmount(initAsset);
        dailyProfitPo.setTotalAccountBotprtpAmount(initAsset);
        dailyProfitPo.setRoomprtpAmount(initAsset);
        dailyProfitPo.setTotalRoomprtpAmount(initAsset);
        dailyProfitPo.setRoomprtpSettle(initAsset);
        dailyProfitPo.setTotalRoomprtpSettle(initAsset);
        dailyProfitPo.setRegisteredFees(initDec);
        dailyProfitPo.setTotalRegisteredFees(initDec);
        dailyProfitPo.setTransferCountFees(initAsset);
        dailyProfitPo.setTotalTransferCountFees(initAsset);
        dailyProfitPo.setCollectedFees(StaticFunHandle.initHouseAmount(configPo.getHouses()));
        dailyProfitPo.setAllowedWithdraw(StaticFunHandle.initHouseAmount(configPo.getHouses()));
        dailyProfitPo.setLastBlock(0);
        if (null != last) {
            //累计dapp收入
            dailyProfitPo.setTotalDappProfit(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalDappProfit()
                    , last.getTotalDappProfit()));
            //累计DAPP水龙头收入
            dailyProfitPo.setTotalFaucetProfit(dailyProfitPo.getTotalFaucetProfit().add(last.getTotalFaucetProfit()));
            //累计手续费收入
            dailyProfitPo.setTotalFeesProfit(dailyProfitPo.getTotalFeesProfit().add(last.getTotalFeesProfit()));
            //累计dapp补贴
            dailyProfitPo.setTotalSubsidy(dailyProfitPo.getTotalSubsidy().add(last.getTotalSubsidy()));
            //累计机器人总体支出
            dailyProfitPo.setTotalAccountBotprtpAmount(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalAccountBotprtpAmount()
                    , last.getTotalAccountBotprtpAmount()));
            //累计DAPP所有房间收入
            dailyProfitPo.setTotalRoomprtpAmount(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalRoomprtpAmount()
                    , last.getTotalRoomprtpAmount()));
            //累计DAPP所有房间支出
            dailyProfitPo.setTotalRoomprtpSettle(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalRoomprtpSettle()
                    , last.getTotalRoomprtpSettle()));
            //累计DAPP注册用户支出
            dailyProfitPo.setTotalRegisteredFees(dailyProfitPo.getTotalRegisteredFees().add(last.getTotalRegisteredFees()));
            //累计DAPP新注册用户转账支出
            dailyProfitPo.setTotalTransferCountFees(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalTransferCountFees()
                    , last.getTotalTransferCountFees()));
        }
        if (null != last) {
            List<String> list = Arrays.asList(configPo.getHouses().split(CONFIG_HOUSES_SPLIT));
            for (String house : list) {
                Long value = clientFunService.getHouseByAccount(house);
                last.setCollectedFees(StaticFunHandle.updateHouseAmount(last.getCollectedFees(), house, value));
                Long allowed = clientFunService.getAllowedWithdraw(house);
                last.setAllowedWithdraw(StaticFunHandle.updateHouseAmount(last.getAllowedWithdraw(), house, allowed));
            }
            dailyProfitService.updateById(last);
        }
        dailyProfitService.insert(dailyProfitPo);
    }
}
