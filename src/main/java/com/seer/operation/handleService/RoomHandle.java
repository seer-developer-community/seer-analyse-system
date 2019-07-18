package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.entity.TotalRoomPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.request.STATUS;
import com.seer.operation.rpcClient.operation.OpenRoomVo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.DailyRoomService;
import com.seer.operation.service.TotalRoomService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.seer.operation.handleService.StaticFunHandle.initAssetAmount;
import static com.seer.operation.request.Constants.*;
import static com.seer.operation.utils.Times.*;

@Service
public class RoomHandle {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ClientFunService clientFunService;
    @Autowired
    private DailyRoomService dailyRoomService;
    @Autowired
    private TotalRoomService totalRoomService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;

    @Transactional
    public void openRoom(BigInteger block, SeerRoom seerRoom, FeeBo feeBo, Long time, OpenRoomVo openRoomVo) {
        Long zero = getTimesDayZero(time);
        String date = formatDateByTimes(zero);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
//        JSONArray jsonArray = initAssetAmount(SEER_ASSET);
//        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
//            //初始化dapp补贴：注意 创建房间的手续费 + 开启房间的手续费
//            List<String> list = Arrays.asList(configPo.getAssets().split(CONFIG_ASSET_SPLIT));
//            for (String str : list) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("asset", str);
//                if (str.equals(SEER_ASSET)) {
//                    BigDecimal amount = new BigDecimal(openRoomVo.getFeeAmount())
//                            .divide(SEER_DECIMALS);
//                    amount = amount.add(clientFunService.getCreateRoomFee());
//                    jsonObject.put("amount", amount);
//                } else {
//                    jsonObject.put("amount", "0.00000");
//                }
//                jsonArray.add(jsonObject);
//            }
//        }
        JSONArray array = StaticFunHandle.initAssetAmount(assets);
        BigDecimal amount = feeBo.getOtherAssetBaseSeer();
        if (feeBo.getIsSeer()) {
            amount = feeBo.getRealAmount();
        }
        amount = amount.add(clientFunService.getCreateRoomFee());
        array = StaticFunHandle.addAssetAmount(SEER_ASSET, assets, array.toJSONString(), amount);
        DailyRoomDetailsPo dailyRoomDetailsPo = dailyRoomService.selectOneByRoomToday(seerRoom.getId(), zero);
        if (null == dailyRoomDetailsPo) {
            //插入每日房间表
            saveDailyRoom(zero, date, block, seerRoom, array.toJSONString());
        }
        //操作房间累计表
        totalRoom(block, seerRoom, openRoomVo, array.toJSONString());
    }

    @Transactional
    public void stopRoom(String room) {
        TotalRoomPo roomPo = totalRoomService.selectOneByRoom(room);
        if (null == roomPo) {
            return;
        }
        if (roomPo.getRoomStatus() == 1) {
            return;
        }
        TotalRoomPo roomPo1 = new TotalRoomPo();
        roomPo1.setId(roomPo.getId());
        roomPo1.setRoomStatus(1);
        totalRoomService.updateById(roomPo1);
    }

    /**
     * 统计每日房间信息
     * 投注人次，投注额，dapp收入。
     *
     * @param predictionVo
     * @param times
     */
    @Transactional
    public void updateDailyRoom(PredictionVo predictionVo, BigInteger current, Long times, SeerRoom seerRoom, boolean isCheck) {
        Long time = Times.getTimesDayZero(times);
        String date = Times.formatDateByTimes(time);
        DailyRoomDetailsPo roomDetailsPo = dailyRoomService.selectOneByRoomToday(predictionVo.getRoom(), time);
        if (null == roomDetailsPo) {
            //插入初始化dapp补贴
            UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
            JSONArray array = initAssetAmount(SEER_ASSET);
            if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
                array = initAssetAmount(configPo.getAssets());
            }
            saveDailyRoom(time, date, current, seerRoom, array.toJSONString());
            roomDetailsPo = dailyRoomService.selectOneByRoomToday(predictionVo.getRoom(), time);
        }
        if (isCheck) {
            if (roomDetailsPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        DailyRoomDetailsPo detailsPo = new DailyRoomDetailsPo();
        detailsPo.setId(roomDetailsPo.getId());
        Long oldPlay = roomDetailsPo.getDailyPlayerCount();
        oldPlay = oldPlay + 1;
        BigDecimal profit = new BigDecimal("2.5").multiply(new BigDecimal(oldPlay)).setScale(5);
        detailsPo.setDailyPlayerCount(oldPlay);
        detailsPo.setDappProfit(profit);
        BigDecimal shares = roomDetailsPo.getDailyShares();
        shares = shares.add(predictionVo.getDeltasAmount());
        detailsPo.setDailyShares(shares);
        detailsPo.setLastBlock(current);
        dailyRoomService.updateById(detailsPo);
    }

    @Transactional
    public void totalRoom(BigInteger block, SeerRoom seerRoom, OpenRoomVo openRoomVo, String initAsset) {
        TotalRoomPo totalRoomPo = totalRoomService.selectOneByRoom(seerRoom.getId());
        if (null == totalRoomPo) {
            TotalRoomPo roomPo = new TotalRoomPo();
            roomPo.setRoom(seerRoom.getId());
            roomPo.setTotalBotPlayAmount(initAsset);
            roomPo.setRoomStatus(STATUS.ROOM_OPENING.getCode());
            roomPo.setRoomStart(Times.formatTDateToEastTimes(openRoomVo.getStart()));
            roomPo.setRoomStop(Times.formatTDateToEastTimes(openRoomVo.getStop()));
            roomPo.setLastBlock(block);
            totalRoomService.insert(roomPo);
        }
    }

    //只更新更新房间累计表的dapp补贴和每日的补贴
    @Transactional
    public void updateTotalRoomDapp(Integer type, JSONObject object, BigInteger current, Long times, JSONObject result, boolean checkBlock, boolean isCheck) {
        String room = object.getString("room");
        Long time = Times.getTimesDayZero(times);
        String feeAsset = object.getJSONObject("fee").getString("asset_id");
        BigDecimal feeAmount = object.getJSONObject("fee").getBigDecimal("amount")
                .divide(SEER_DECIMALS).setScale(5);
        TotalRoomPo roomPo = totalRoomService.selectOneByRoom(room);
        DailyRoomDetailsPo detailsPo = dailyRoomService.selectOneByRoomToday(room, time);
        if (null == roomPo || null == detailsPo) {
            return;
        }
        if (isCheck && checkBlock) {
            if (roomPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        String bots = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        if (null != configPo && StringUtils.isNotBlank(configPo.getSeerBots())) {
            bots = configPo.getSeerBots();
        }
        if (type >= 44 && type <= 49) {
            //房间操作的手续费
            if (null != roomPo) {
                JSONArray a = StaticFunHandle.addAssetAmount(feeAsset, assets, roomPo.getTotalBotPlayAmount(), feeAmount);
//                JSONArray a = addAsset(feeAsset, roomPo.getTotalBotPlayAmount(), feeAmount);
                TotalRoomPo totalRoomPo = new TotalRoomPo();
                totalRoomPo.setId(roomPo.getId());
                totalRoomPo.setTotalBotPlayAmount(a.toJSONString());
                totalRoomService.updateById(totalRoomPo);
            }
            if (null != detailsPo) {
                JSONArray a = StaticFunHandle.addAssetAmount(feeAsset, assets, detailsPo.getDappTotalAmount(), feeAmount);
//                JSONArray a = addAsset(feeAsset, detailsPo.getDappTotalAmount(), feeAmount);
                DailyRoomDetailsPo detailsPo1 = new DailyRoomDetailsPo();
                detailsPo1.setId(detailsPo.getId());
                detailsPo1.setDappTotalAmount(a.toJSONString());
                dailyRoomService.updateById(detailsPo1);
            }
        } else if (50 == type) {
            //机器人手续费，预测金额
            String issuer = object.getString("issuer");
            if (bots.contains(issuer)) {
                String asset = result.getString("asset_id");
                String amountStr = result.getJSONArray("deltas").getJSONArray(0).getString(1);
                if (amountStr.startsWith("-")) {
                    amountStr = amountStr.split("-")[1];
                }
                BigDecimal amount = new BigDecimal(amountStr).divide(SEER_DECIMALS).setScale(5);
                if (null != roomPo) {
                    //机器人手续费
                    String totalOld = roomPo.getTotalBotPlayAmount();
                    totalOld = StaticFunHandle.addAssetAmount(SEER_ASSET, configPo.getAssets(), totalOld, feeAmount).toJSONString();
                    //机器人预测金额
                    totalOld = StaticFunHandle.addAssetAmount(asset, configPo.getAssets(), totalOld, amount).toJSONString();
                    TotalRoomPo totalRoomPo = new TotalRoomPo();
                    totalRoomPo.setId(roomPo.getId());
                    totalRoomPo.setTotalBotPlayAmount(totalOld);
                    totalRoomService.updateById(totalRoomPo);
                }
                if (null != detailsPo) {
                    //机器人手续费
                    String totalOld = detailsPo.getDappTotalAmount();
                    totalOld = StaticFunHandle.addAssetAmount(SEER_ASSET, configPo.getAssets(), totalOld, feeAmount).toJSONString();
                    //机器人预测金额
                    totalOld = StaticFunHandle.addAssetAmount(asset, configPo.getAssets(), totalOld, amount).toJSONString();
                    DailyRoomDetailsPo roomDetailsPo = new DailyRoomDetailsPo();
                    roomDetailsPo.setId(detailsPo.getId());
                    roomDetailsPo.setDappTotalAmount(totalOld);
                    dailyRoomService.updateById(roomDetailsPo);
                }
            }
        }
    }

    /**
     * 每天0点定时执行
     */
    @Transactional
    public void zeroTotalHandle(Long times) {
        Long zero = getTimesDayZero(times);
        String date = formatDateByTimes(zero);
        List<TotalRoomPo> list = totalRoomService.selectAllListForUpdate(zero);
        int count = 0;
        for (TotalRoomPo roomPo : list) {
            Long last_day_zero = zero - DAY_TIMESTAMPS;
            SeerRoom seerRoom = clientFunService.getSeerRoom(roomPo.getRoom());
            //如果房间还是开启状态，插入一条新记录到每日表中
            boolean a = STATUS.ROOM_OPENING.getCode().equals(roomPo.getRoomStatus());
            boolean b = zero.compareTo(roomPo.getRoomStart()) > 0;
            boolean c = zero.compareTo(roomPo.getRoomStop()) < 0;
            boolean isOpen = a && (b && c);
            DailyRoomDetailsPo dailyRoomDetailsPo = dailyRoomService.selectOneByRoomToday(roomPo.getRoom(), zero);
            if (isOpen && null == dailyRoomDetailsPo) {
                BigInteger block = clientFunService.getBlockHeight();
                if (null == block) {
                    block = BigInteger.ZERO;
                }
                //插入初始化dapp补贴
                UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
                JSONArray array = initAssetAmount(SEER_ASSET);
                if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
                    array = initAssetAmount(configPo.getAssets());
                }
                saveDailyRoom(zero, date, block, seerRoom, array.toJSONString());
                count++;
            }
        }
        logger.info("zero-time-task select room num:{} but operate room num:{}", list.size(), count);
    }

    @Transactional
    public void saveDailyRoom(Long zero, String date, BigInteger block, SeerRoom seerRoom, String initAsset) {
        DailyRoomDetailsPo detailsPo = new DailyRoomDetailsPo();
        detailsPo.setZeroTimestamp(zero);
        detailsPo.setTime(date);
        detailsPo.setRoom(seerRoom.getId());
        detailsPo.setType(seerRoom.getRoomType());
        detailsPo.setHouse(seerRoom.getHouseId());
        detailsPo.setDescription(seerRoom.getDescription());
        detailsPo.setDailyPlayerCount(0L);
        detailsPo.setDailyShares(new BigDecimal("0.00000").setScale(5));
        detailsPo.setDappProfit(new BigDecimal("0.00000").setScale(5));
        detailsPo.setDappTotalAmount(initAsset);
        detailsPo.setLastBlock(block);
        dailyRoomService.insert(detailsPo);
    }

//    @Transactional
//    public JSONArray addAsset(String asset, String oldTotal, BigDecimal amount) {
//        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
//        JSONArray array = JSONArray.parseArray(oldTotal);
//        if (null != configPo) {
//            array = addAssetAmount(asset, configPo.getAssets(), oldTotal, amount);
//        } else {
//            for (int i = 0; i < array.size(); i++) {
//                JSONObject object = array.getJSONObject(i);
//                if (asset.equals(object.getString("asset"))) {
//                    BigDecimal oldAmount = object.getBigDecimal("amount");
//                    object.replace("amount", oldAmount.add(amount));
//                    array.set(i, object);
//                    break;
//                }
//            }
//        }
//        return array;
//    }
}
