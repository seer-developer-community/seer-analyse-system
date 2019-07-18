package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.DailyTypeRoomPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.DailyTypeRoomService;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.*;

@Service
public class DailyTypeRoomHandle {
    @Autowired
    private DataCacheManager cacheManager;
    @Autowired
    private DailyTypeRoomService typeRoomService;
    @Autowired
    private ClientFunService clientFunService;

    @Transactional
    public void updateFees(SeerRoom seerRoom, FeeBo feeBo, BigInteger current, Long time, boolean checkBlock, boolean isCheck) {
        Long zeroTimes = Times.getTimesDayZero(time);
        DailyTypeRoomPo typeRoomPo = typeRoomService.selectOneByTimestamp(zeroTimes);
        if (null == typeRoomPo) {
            initTypeRoom(zeroTimes);
            typeRoomPo = typeRoomService.selectOneByTimestamp(zeroTimes);
        }
        if (isCheck && checkBlock) {
            if (typeRoomPo.getLastBlock() >= current.intValue()) {
                return;
            }
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        DailyTypeRoomPo updatePo = new DailyTypeRoomPo();
        updatePo.setTimestamp(typeRoomPo.getTimestamp());
        if (PVP_ROOM == seerRoom.getRoomType()) {
            //PVP房间手续费
            updatePo.setPvpFees(typeRoomPo.getPvpFees().add(fee));
            //累计PVP房间手续费
            updatePo.setTotalPvpFees(typeRoomPo.getTotalPvpFees().add(fee));
        } else if (PVD_ROOM == seerRoom.getRoomType()) {
            //PVD房间手续费
            updatePo.setPvdFees(typeRoomPo.getPvdFees().add(fee));
            //累计PVD房间手续费
            updatePo.setTotalPvdFees(typeRoomPo.getTotalPvdFees().add(fee));
        } else if (ADV_ROOM == seerRoom.getRoomType()) {
            //高级房间手续费
            updatePo.setAdvFees(typeRoomPo.getAdvFees().add(fee));
            //累计高级房间手续费
            updatePo.setTotalAdvFees(typeRoomPo.getTotalAdvFees().add(fee));
        }
        updatePo.setLastBlock(current.intValue());
        typeRoomService.updateById(updatePo);
    }

    @Transactional
    public void updateBy49(SeerRoom seerRoom, FeeBo feeBo, JSONObject result, BigInteger current, boolean checkBlock, boolean isCheck, Long time) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTimes = Times.getTimesDayZero(time);
        String assets = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String bots = "";
        if (null != configPo) {
            bots = configPo.getSeerBots();
        }
        DailyTypeRoomPo typeRoomPo = typeRoomService.selectOneByTimestamp(zeroTimes);
        if (null == typeRoomPo) {
            initTypeRoom(zeroTimes);
            typeRoomPo = typeRoomService.selectOneByTimestamp(zeroTimes);
        }
        if (isCheck && checkBlock) {
            if (typeRoomPo.getLastBlock() >= current.intValue()) {
                return;
            }
        }
        String assetId = result.getString("asset_id");
        JSONArray deltas = result.getJSONArray("deltas");
        BigInteger allAmount = BigInteger.ZERO;
        BigInteger botAndOwner = BigInteger.ZERO;
        BigDecimal fee = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount();
        }
        for (int i = 0; i < deltas.size(); i++) {
            JSONArray array = deltas.getJSONArray(i);
            String userId = array.getString(0);
            BigInteger amount = array.getBigInteger(1);
            allAmount = allAmount.add(amount);
            if (bots.contains(userId) || seerRoom.getOwner().equals(userId)) {
                botAndOwner = botAndOwner.add(amount);
            }
        }
        BigDecimal amountDec = new BigDecimal(allAmount.subtract(botAndOwner)).divide(SEER_DECIMALS).setScale(5);
        DailyTypeRoomPo updatePo = new DailyTypeRoomPo();
        updatePo.setTimestamp(typeRoomPo.getTimestamp());
        if (PVP_ROOM == seerRoom.getRoomType()) {
            //PVP派奖总额
            updatePo.setPvpSettle(StaticFunHandle.addAssetAmount(assetId, assets, typeRoomPo.getPvpSettle()
                    , amountDec).toJSONString());
            //累计PVP派奖总额
            updatePo.setTotalPvpSettle(StaticFunHandle.addAssetAmount(assetId, assets, typeRoomPo.getTotalPvpSettle()
                    , amountDec).toJSONString());
            //PVP房间手续费
            updatePo.setPvpFees(typeRoomPo.getPvpFees().add(fee));
            //累计PVP房间手续费
            updatePo.setTotalPvpFees(typeRoomPo.getTotalPvpFees().add(fee));
        } else if (PVD_ROOM == seerRoom.getRoomType()) {
            //PVD派奖总额
            updatePo.setPvdSettle(StaticFunHandle.addAssetAmount(assetId, assets, typeRoomPo.getPvdSettle()
                    , amountDec).toJSONString());
            //累计PVD派奖总额
            updatePo.setTotalPvdSettle(StaticFunHandle.addAssetAmount(assetId, assets, typeRoomPo.getTotalPvdSettle()
                    , amountDec).toJSONString());
            //PVD房间手续费
            updatePo.setPvdFees(typeRoomPo.getPvdFees().add(fee));
            //累计PVD房间手续费
            updatePo.setTotalPvdFees(typeRoomPo.getTotalPvdFees().add(fee));
        } else if (ADV_ROOM == seerRoom.getRoomType()) {
            //高级派奖总额
            updatePo.setAdvSettle(StaticFunHandle.addAssetAmount(assetId, assets, typeRoomPo.getAdvSettle()
                    , amountDec).toJSONString());
            //累计高级派奖总额
            updatePo.setTotalAdvSettle(StaticFunHandle.addAssetAmount(assetId, assets, typeRoomPo.getTotalAdvSettle()
                    , amountDec).toJSONString());
            //高级房间手续费
            updatePo.setAdvFees(typeRoomPo.getAdvFees().add(fee));
            //累计高级房间手续费
            updatePo.setTotalAdvFees(typeRoomPo.getTotalAdvFees().add(fee));
        }
        updatePo.setLastBlock(current.intValue());
        typeRoomService.updateById(updatePo);
    }

    @Transactional
    public void updateBy50(PredictionVo predictionVo, FeeBo feeBo, SeerRoom seerRoom, Long time,
                           BigInteger current, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTimes = Times.getTimesDayZero(time);
        String assets = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String bots = "";
        if (null != configPo) {
            bots = configPo.getSeerBots();
        }
        DailyTypeRoomPo typeRoomPo = typeRoomService.selectOneByTimestamp(zeroTimes);
        if (null == typeRoomPo) {
            initTypeRoom(zeroTimes);
            typeRoomPo = typeRoomService.selectOneByTimestamp(zeroTimes);
        }
        if (isCheck && checkBlock) {
            if (typeRoomPo.getLastBlock() >= current.intValue()) {
                return;
            }
        }
        if (bots.contains(predictionVo.getIssuer())) {
            return;
        }
        BigDecimal fee = feeBo.getOtherAssetBaseSeer().divide(new BigDecimal(2), 5, BigDecimal.ROUND_HALF_DOWN);
        if (feeBo.getIsSeer()) {
            fee = feeBo.getRealAmount().divide(new BigDecimal(2), 5, BigDecimal.ROUND_HALF_DOWN);
        }
        DailyTypeRoomPo updatePo = new DailyTypeRoomPo();
        updatePo.setTimestamp(typeRoomPo.getTimestamp());
        if (PVD_ROOM == seerRoom.getRoomType()) {
            //PVD投注总额
            updatePo.setPvdPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getPvdPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //累计PVD投注总额
            updatePo.setTotalPvdPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getTotalPvdPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //PVD房间手续费收入
            updatePo.setPvdPrtpFees(typeRoomPo.getPvdPrtpFees().add(fee));
            //累计PVD房间手续费收入
            updatePo.setTotalPvdPrtpFees(typeRoomPo.getTotalPvdPrtpFees().add(fee));
        } else if (PVP_ROOM == seerRoom.getRoomType()) {
            BigDecimal pvpPercent = new BigDecimal(seerRoom.getRunningOption().getPvpOwnerPercent())
                    .divide(SEER_DECIMALS).setScale(5);
            BigDecimal pvpProfitAmount = predictionVo.getDeltasAmount().multiply(pvpPercent)
                    .setScale(5, BigDecimal.ROUND_HALF_DOWN);
            //pvp抽成收入
            updatePo.setPvpProfit(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getPvpProfit(), pvpProfitAmount).toJSONString());
            //累计pvp抽成收入
            updatePo.setTotalPvpProfit(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getTotalPvpProfit(), pvpProfitAmount).toJSONString());
            //PVP投注总额
            updatePo.setPvpPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getPvpPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //累计PVP投注总额
            updatePo.setTotalPvpPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getTotalPvpPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //PVP房间手续费收入
            updatePo.setPvpPrtpFees(typeRoomPo.getPvpPrtpFees().add(fee));
            //累计PVP房间手续费收入
            updatePo.setTotalPvpPrtpFees(typeRoomPo.getTotalPvpPrtpFees().add(fee));
        } else if (ADV_ROOM == seerRoom.getRoomType()) {
            //高级投注总额
            updatePo.setAdvPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getAdvPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //累计高级投注总额
            updatePo.setTotalAdvPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    typeRoomPo.getTotalAdvPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //高级房间手续费收入
            updatePo.setAdvPrtpFees(typeRoomPo.getAdvPrtpFees().add(fee));
            //累计高级房间手续费收入
            updatePo.setTotalAdvPrtpFees(typeRoomPo.getTotalAdvPrtpFees().add(fee));
        }
        updatePo.setLastBlock(current.intValue());
        typeRoomService.updateById(updatePo);
    }

    @Transactional
    public void initTypeRoom(Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyTypeRoomPo typeRoomPo = typeRoomService.selectOneByTimestamp(time);
        if (null != typeRoomPo) {
            return;
        }
        String assets = SEER_ASSET;
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        DailyTypeRoomPo last = typeRoomService.selectLastOne();
        String initAsset = StaticFunHandle.initAssetAmount(assets).toJSONString();
        BigDecimal initDec = BigDecimal.ZERO.setScale(5);
        typeRoomPo = new DailyTypeRoomPo();
        typeRoomPo.setTimestamp(time);
        typeRoomPo.setTime(Times.formatDateByTimes(time));
        typeRoomPo.setPvpProfit(initAsset);
        typeRoomPo.setTotalPvpProfit(initAsset);
        typeRoomPo.setPvpPlayAmount(initAsset);
        typeRoomPo.setTotalPvpPlayAmount(initAsset);
        typeRoomPo.setPvpSettle(initAsset);
        typeRoomPo.setTotalPvpSettle(initAsset);
        typeRoomPo.setPvpFees(initDec);
        typeRoomPo.setTotalPvpFees(initDec);
        typeRoomPo.setPvpPrtpFees(initDec);
        typeRoomPo.setTotalPvpPrtpFees(initDec);
        typeRoomPo.setPvdPlayAmount(initAsset);
        typeRoomPo.setPvdSettle(initAsset);
        typeRoomPo.setTotalPvdPlayAmount(initAsset);
        typeRoomPo.setTotalPvdSettle(initAsset);
        typeRoomPo.setPvdFees(initDec);
        typeRoomPo.setTotalPvdFees(initDec);
        typeRoomPo.setPvdPrtpFees(initDec);
        typeRoomPo.setTotalPvdPrtpFees(initDec);
        typeRoomPo.setAdvPlayAmount(initAsset);
        typeRoomPo.setTotalAdvPlayAmount(initAsset);
        typeRoomPo.setAdvSettle(initAsset);
        typeRoomPo.setTotalAdvSettle(initAsset);
        typeRoomPo.setAdvFees(initDec);
        typeRoomPo.setTotalAdvFees(initDec);
        typeRoomPo.setAdvPrtpFees(initDec);
        typeRoomPo.setTotalAdvPrtpFees(initDec);
        typeRoomPo.setLastBlock(0);
        if (null != last) {
            //累计pvp抽成收入
            typeRoomPo.setTotalPvpProfit(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalPvpProfit(),
                    last.getTotalPvpProfit()));
            //累计PVP投注总额
            typeRoomPo.setTotalPvpPlayAmount(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalPvpPlayAmount(),
                    last.getTotalPvpPlayAmount()));
            //累计PVP派奖总额
            typeRoomPo.setTotalPvpSettle(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalPvpSettle(),
                    last.getTotalPvpSettle()));
            //累计PVP房间手续费
            typeRoomPo.setTotalPvpFees(typeRoomPo.getTotalPvpFees().add(last.getTotalPvpFees()));
            //累计PVP房间手续费收入
            typeRoomPo.setTotalPvpPrtpFees(typeRoomPo.getTotalPvpPrtpFees().add(last.getTotalPvpPrtpFees()));
            //累计PVD投注总额
            typeRoomPo.setTotalPvdPlayAmount(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalPvdPlayAmount(),
                    last.getTotalPvdPlayAmount()));
            //累计PVD派奖总额
            typeRoomPo.setTotalPvdSettle(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalPvdSettle(),
                    last.getTotalPvdSettle()));
            //累计PVD房间手续费
            typeRoomPo.setTotalPvdFees(typeRoomPo.getTotalPvdFees().add(last.getTotalPvdFees()));
            //累计PVD房间手续费收入
            typeRoomPo.setTotalPvdPrtpFees(typeRoomPo.getTotalPvdPrtpFees().add(last.getTotalPvdPrtpFees()));
            //累计高级投注总额
            typeRoomPo.setTotalAdvPlayAmount(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalAdvPlayAmount(),
                    last.getTotalAdvPlayAmount()));
            //累计高级派奖总额
            typeRoomPo.setTotalAdvSettle(StaticFunHandle.addAssetsAmount(assets, typeRoomPo.getTotalAdvSettle(),
                    last.getTotalAdvSettle()));
            //累计高级房间手续费
            typeRoomPo.setTotalAdvFees(typeRoomPo.getTotalAdvFees().add(last.getTotalAdvFees()));
            //累计高级房间手续费收入
            typeRoomPo.setTotalAdvPrtpFees(typeRoomPo.getTotalAdvPrtpFees().add(last.getTotalAdvPrtpFees()));
        }
        typeRoomService.insert(typeRoomPo);
    }
}
