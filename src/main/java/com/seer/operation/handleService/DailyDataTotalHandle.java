package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.DailyDataTotalPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.DailyDataTotalService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.*;

@Service
public class DailyDataTotalHandle {
    @Autowired
    private DailyDataTotalService dataTotalService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;

    @Transactional
    public void initDataTotal(Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataTotalPo dataTotalPo = cacheManager.getDataTotalClient(time);
        if (null != dataTotalPo) {
            return;
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String initAsset = StaticFunHandle.initAssetAmount(assets).toJSONString();
        BigDecimal zero = BigDecimal.ZERO.setScale(5);
        DailyDataTotalPo lastTotalPo = dataTotalService.selectLastOneWithThree();
        String totalRoomprtpAmount = initAsset;
        String totalBotprtpAmount = initAsset;
        String totalRoomSettle = initAsset;
        if (null != lastTotalPo) {
            if (StringUtils.isNotBlank(lastTotalPo.getTotalAccountBotprtpAmount())) {
                totalBotprtpAmount = lastTotalPo.getTotalAccountBotprtpAmount();
            }
            if (StringUtils.isNotBlank(lastTotalPo.getTotalRoomprtpAmount())) {
                totalRoomprtpAmount = lastTotalPo.getTotalRoomprtpAmount();
            }
            if (StringUtils.isNotBlank(lastTotalPo.getTotalRoomprtpSettle())) {
                totalRoomSettle = lastTotalPo.getTotalRoomprtpSettle();
            }
        }
        dataTotalPo = new DailyDataTotalPo();
        dataTotalPo.setTimestamp(time);
        dataTotalPo.setTime(Times.formatDateByTimes(time));
        dataTotalPo.setPvpplayAmount(initAsset);
        dataTotalPo.setPvpprtpFees(zero);
        dataTotalPo.setPvpSettle(initAsset);
        dataTotalPo.setPvpFees(zero);
        dataTotalPo.setPvdplayAmount(initAsset);
        dataTotalPo.setPvdprtpFees(zero);
        dataTotalPo.setPvdSettle(initAsset);
        dataTotalPo.setTotalRoomprtpAmount(totalRoomprtpAmount);
        dataTotalPo.setTotalAccountBotprtpAmount(totalBotprtpAmount);
        dataTotalPo.setPvdFees(zero);
        dataTotalPo.setAdvplayAmount(initAsset);
        dataTotalPo.setAdvprtpFees(zero);
        dataTotalPo.setAdvSettle(initAsset);
        dataTotalPo.setAdvFees(zero);
        dataTotalPo.setRoomprtpAmount(initAsset);
        dataTotalPo.setRoomprtpSettle(initAsset);
        dataTotalPo.setAccountBotprtpAmount(initAsset);
        dataTotalPo.setTotalRoomprtpSettle(totalRoomSettle);
        dataTotalPo.setLastBlock(BigInteger.ZERO);
        dataTotalService.insert(dataTotalPo);

    }

    @Transactional
    public void operate50(PredictionVo predictionVo, SeerRoom room, BigInteger current, boolean isCheck, Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataTotalPo dataTotalPo = dataTotalService.selectOneByTimestamp(time);
        if (null == dataTotalPo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(dataTotalPo.getLastBlock()) <= 0) {
                return;
            }
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo || StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        if (PVP_ROOM == room.getRoomType()) {
//            updatePvp(assets, dataTotalPo, predictionVo, current);
            DailyDataTotalPo totalPo = new DailyDataTotalPo();
            totalPo.setTimestamp(dataTotalPo.getTimestamp());
            totalPo.setLastBlock(current);
            //PVP投注总额
            totalPo.setPvpplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dataTotalPo.getPvpplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //PVP手续费收入总额
            if (predictionVo.getFeeBo().getIsSeer()) {
                totalPo.setPvpprtpFees(dataTotalPo.getPvpprtpFees().add(predictionVo.getFeeBo().getRealAmount()));
            } else {
                totalPo.setPvpprtpFees(dataTotalPo.getPvpprtpFees().add(predictionVo.getFeeBo().getOtherAssetBaseSeer()));
            }
            dataTotalService.updateById(totalPo);
        } else if (PVD_ROOM == room.getRoomType()) {
//            updatePvd(assets, dataTotalPo, predictionVo, current);
            DailyDataTotalPo totalPo = new DailyDataTotalPo();
            totalPo.setTimestamp(dataTotalPo.getTimestamp());
            totalPo.setLastBlock(current);
            //PVD投注总额
            totalPo.setPvdplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dataTotalPo.getPvdplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //PVD手续费收入总额
            if (predictionVo.getFeeBo().getIsSeer()) {
                totalPo.setPvdprtpFees(dataTotalPo.getPvdprtpFees().add(predictionVo.getFeeBo().getRealAmount()));
            } else {
                totalPo.setPvdprtpFees(dataTotalPo.getPvdprtpFees().add(predictionVo.getFeeBo().getOtherAssetBaseSeer()));
            }
            dataTotalService.updateById(totalPo);
        } else if (ADV_ROOM == room.getRoomType()) {
//            updateAdv(assets, dataTotalPo, predictionVo, current);
            DailyDataTotalPo totalPo = new DailyDataTotalPo();
            totalPo.setTimestamp(dataTotalPo.getTimestamp());
            totalPo.setLastBlock(current);
            //高级投注总额
            totalPo.setAdvplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dataTotalPo.getAdvplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //高级房间手续费收入
            if (predictionVo.getFeeBo().getIsSeer()) {
                totalPo.setAdvprtpFees(dataTotalPo.getAdvprtpFees().add(predictionVo.getFeeBo().getRealAmount()));
            } else {
                totalPo.setAdvprtpFees(dataTotalPo.getAdvprtpFees().add(predictionVo.getFeeBo().getOtherAssetBaseSeer()));
            }
            dataTotalService.updateById(totalPo);
        }
        DailyDataTotalPo dailyDataTotalPo = new DailyDataTotalPo();
        dailyDataTotalPo.setTimestamp(dataTotalPo.getTimestamp());
        dailyDataTotalPo.setLastBlock(current);
        //累计DAPP所有房间收入
        dailyDataTotalPo.setTotalRoomprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                dataTotalPo.getTotalRoomprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
        //DAPP所有房间收入(今日)
        dailyDataTotalPo.setRoomprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                dataTotalPo.getRoomprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
        //累计机器人总体支出
        if (configPo.getSeerBots().contains(predictionVo.getIssuer())) {
            dailyDataTotalPo.setTotalAccountBotprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets
                    , dataTotalPo.getTotalAccountBotprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //机器人总体支出(今日)
            dailyDataTotalPo.setAccountBotprtpAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    dataTotalPo.getAccountBotprtpAmount(), predictionVo.getDeltasAmount()).toJSONString());
        }
        dataTotalService.updateById(dailyDataTotalPo);
    }

    @Transactional
    public void updateSettle(SeerRoom seerRoom, JSONObject result, BigInteger current, boolean checkBlock, boolean isCheck, Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataTotalPo dataTotalPo = dataTotalService.selectOneByTimestamp(time);
        if (null == dataTotalPo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (current.compareTo(dataTotalPo.getLastBlock()) <= 0) {
                return;
            }
        }
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo || StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        DailyDataTotalPo totalPo = new DailyDataTotalPo();
        totalPo.setTimestamp(dataTotalPo.getTimestamp());
        totalPo.setLastBlock(current);
        String assetId = result.getString("asset_id");
        JSONArray deltas = result.getJSONArray("deltas");
        BigInteger allAmount = BigInteger.ZERO;
        for (int i = 0; i < deltas.size(); i++) {
            JSONArray array = deltas.getJSONArray(i);
            String userId = array.getString(0);
            BigInteger amount = array.getBigInteger(1);
            allAmount = allAmount.add(amount);
        }
        BigDecimal amountDec = new BigDecimal(allAmount).divide(SEER_DECIMALS).setScale(5);
        if (PVP_ROOM == seerRoom.getRoomType()) {
            totalPo.setPvpSettle(StaticFunHandle.addAssetAmount(assetId, assets, dataTotalPo.getPvpSettle(), amountDec)
                    .toJSONString());
        } else if (PVD_ROOM == seerRoom.getRoomType()) {
            totalPo.setPvdSettle(StaticFunHandle.addAssetAmount(assetId, assets, dataTotalPo.getPvdSettle(), amountDec)
                    .toJSONString());
        } else if (ADV_ROOM == seerRoom.getRoomType()) {
            totalPo.setAdvSettle(StaticFunHandle.addAssetAmount(assetId, assets, dataTotalPo.getAdvSettle(), amountDec)
                    .toJSONString());
        }
        totalPo.setTotalRoomprtpSettle(StaticFunHandle.addAssetAmount(assetId, assets, dataTotalPo.getTotalRoomprtpSettle(),
                amountDec).toJSONString());
        totalPo.setRoomprtpSettle(StaticFunHandle.addAssetAmount(assetId, assets, dataTotalPo.getRoomprtpSettle(),
                amountDec).toJSONString());
        dataTotalService.updateById(totalPo);
    }

    @Transactional
    public void updateFee(SeerRoom seerRoom, FeeBo feeBo, BigInteger current, boolean checkBlock, boolean isCheck, Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataTotalPo dataTotalPo = dataTotalService.selectOneByTimestamp(time);
        if (null == dataTotalPo) {
            return;
        }
        if (checkBlock && isCheck) {
            if (current.compareTo(dataTotalPo.getLastBlock()) <= 0) {
                return;
            }
        }
        BigDecimal pvp = dataTotalPo.getPvpFees();
        BigDecimal pvd = dataTotalPo.getPvdFees();
        BigDecimal adv = dataTotalPo.getAdvFees();
        dataTotalPo = new DailyDataTotalPo();
        dataTotalPo.setTimestamp(time);
        dataTotalPo.setLastBlock(current);
        if (PVP_ROOM == seerRoom.getRoomType()) {
            if (feeBo.getIsSeer()) {
                dataTotalPo.setPvpFees(pvp.add(feeBo.getRealAmount()));
            } else {
                dataTotalPo.setPvpFees(pvp.add(feeBo.getOtherAssetBaseSeer()));
            }
        } else if (PVD_ROOM == seerRoom.getRoomType()) {
            if (feeBo.getIsSeer()) {
                dataTotalPo.setPvdFees(pvd.add(feeBo.getRealAmount()));
            } else {
                dataTotalPo.setPvdFees(pvd.add(feeBo.getOtherAssetBaseSeer()));
            }
        } else if (ADV_ROOM == seerRoom.getRoomType()) {
            if (feeBo.getIsSeer()) {
                dataTotalPo.setAdvFees(adv.add(feeBo.getRealAmount()));
            } else {
                dataTotalPo.setAdvFees(adv.add(feeBo.getOtherAssetBaseSeer()));
            }
        }
        dataTotalService.updateById(dataTotalPo);
    }

    public void updatePvp(String assets, DailyDataTotalPo dataTotalPo, PredictionVo predictionVo, BigInteger current) {
        DailyDataTotalPo totalPo = new DailyDataTotalPo();
        totalPo.setTimestamp(dataTotalPo.getTimestamp());
        totalPo.setLastBlock(current);
        //PVP投注总额
        totalPo.setPvpplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                dataTotalPo.getPvpplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
        //PVP手续费收入总额
        if (predictionVo.getFeeBo().getIsSeer()) {
            totalPo.setPvpprtpFees(dataTotalPo.getPvpprtpFees().add(predictionVo.getFeeBo().getRealAmount()));
        } else {
            totalPo.setPvpprtpFees(dataTotalPo.getPvpprtpFees().add(predictionVo.getFeeBo().getOtherAssetBaseSeer()));
        }
        dataTotalService.updateById(totalPo);
    }

    public void updatePvd(String assets, DailyDataTotalPo dataTotalPo, PredictionVo predictionVo, BigInteger current) {
        DailyDataTotalPo totalPo = new DailyDataTotalPo();
        totalPo.setTimestamp(dataTotalPo.getTimestamp());
        totalPo.setLastBlock(current);
        //PVD投注总额
        totalPo.setPvdplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                dataTotalPo.getPvdplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
        //PVD手续费收入总额
        if (predictionVo.getFeeBo().getIsSeer()) {
            totalPo.setPvdprtpFees(dataTotalPo.getPvdprtpFees().add(predictionVo.getFeeBo().getRealAmount()));
        } else {
            totalPo.setPvdprtpFees(dataTotalPo.getPvdprtpFees().add(predictionVo.getFeeBo().getOtherAssetBaseSeer()));
        }
        dataTotalService.updateById(totalPo);
    }

    public void updateAdv(String assets, DailyDataTotalPo dataTotalPo, PredictionVo predictionVo, BigInteger current) {
        DailyDataTotalPo totalPo = new DailyDataTotalPo();
        totalPo.setTimestamp(dataTotalPo.getTimestamp());
        totalPo.setLastBlock(current);
        //高级投注总额
        totalPo.setAdvplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                dataTotalPo.getAdvplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
        //高级房间手续费收入
        if (predictionVo.getFeeBo().getIsSeer()) {
            totalPo.setAdvprtpFees(dataTotalPo.getAdvprtpFees().add(predictionVo.getFeeBo().getRealAmount()));
        } else {
            totalPo.setAdvprtpFees(dataTotalPo.getAdvprtpFees().add(predictionVo.getFeeBo().getOtherAssetBaseSeer()));
        }
        dataTotalService.updateById(totalPo);
    }
}

