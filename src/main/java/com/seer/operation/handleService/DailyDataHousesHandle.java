package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.DailyDataHousesPo;
import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.AccountHouseTotalService;
import com.seer.operation.service.DailyDataHousesService;
import com.seer.operation.service.DailyRoomService;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.seer.operation.request.Constants.*;

@Service
public class DailyDataHousesHandle {

    @Autowired
    private DailyDataHousesService dataHousesService;
    @Autowired
    private DailyRoomService dailyRoomService;
    @Autowired
    private AccountHouseTotalService houseTotalService;
    @Autowired
    private DataCacheManager cacheManager;
    @Autowired
    private ClientFunService clientFunService;

    //更新dapp补贴和累积补贴
    @Transactional
    public void updateSubsidy(FeeBo feeBo, String owner, String registrar, PredictionVo predictionVo, BigInteger current, boolean checkBlock, Long time, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String houses = "";
        String bots = "";
        if (null != configPo) {
            houses = configPo.getHouses();
            bots = configPo.getSeerBots();
        }
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == dataHousesPo) {
            initDateHouses(times);
            dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (checkBlock && isCheck) {
            if (dataHousesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        BigDecimal dailySubsidy = dataHousesPo.getDailySubsidy();
        BigDecimal totalSubsidy = dataHousesPo.getTotalSubsidy();
        if (null != predictionVo) {
            //botplay_amount
            if (houses.contains(owner) &&
                    predictionVo.getDeltasAsset().equals(SEER_ASSET) && bots.contains(predictionVo.getIssuer())) {
                dailySubsidy = dailySubsidy.add(predictionVo.getDeltasAmount());
                totalSubsidy = totalSubsidy.add(predictionVo.getDeltasAmount());
            }
            //seerbot_fees
            if (houses.contains(registrar) && bots.contains(predictionVo.getIssuer())) {
                if (feeBo.getIsSeer()) {
                    dailySubsidy = dailySubsidy.add(feeBo.getRealAmount());
                    totalSubsidy = totalSubsidy.add(feeBo.getRealAmount());
                } else {
                    dailySubsidy = dailySubsidy.add(feeBo.getOtherAssetBaseSeer());
                    totalSubsidy = totalSubsidy.add(feeBo.getOtherAssetBaseSeer());
                }
            }
            //total_fees
            if (houses.contains(predictionVo.getIssuer())) {
                if (feeBo.getIsSeer()) {
                    dailySubsidy = dailySubsidy.add(feeBo.getRealAmount());
                    totalSubsidy = totalSubsidy.add(feeBo.getRealAmount());
                } else {
                    dailySubsidy = dailySubsidy.add(feeBo.getOtherAssetBaseSeer());
                    totalSubsidy = totalSubsidy.add(feeBo.getOtherAssetBaseSeer());
                }
            }
        } else {
            if (houses.contains(owner)) {
                if (feeBo.getIsSeer()) {
                    dailySubsidy = dailySubsidy.add(feeBo.getRealAmount());
                    totalSubsidy = totalSubsidy.add(feeBo.getRealAmount());
                } else {
                    dailySubsidy = dailySubsidy.add(feeBo.getOtherAssetBaseSeer());
                    totalSubsidy = totalSubsidy.add(feeBo.getOtherAssetBaseSeer());
                }
            }
        }
        dataHousesPo = new DailyDataHousesPo();
        dataHousesPo.setTimestamp(times);
        dataHousesPo.setLastBlock(current);
        dataHousesPo.setTotalSubsidy(totalSubsidy);
        dataHousesPo.setDailySubsidy(dailySubsidy);
        dataHousesService.updateById(dataHousesPo);
    }

    //真人参与房间数和新增房间参与率
    @Transactional
    public void updateActiveRooms(String issuer, SeerRoom seerRoom, BigInteger current, boolean checkBlock, Long time, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String houses = "";
        String bots = "";
        if (null != configPo) {
            houses = configPo.getHouses();
            bots = configPo.getSeerBots();
        }
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == dataHousesPo) {
            initDateHouses(times);
            dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (checkBlock && isCheck) {
            if (dataHousesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (bots.contains(issuer) || !houses.contains(seerRoom.getOwner())) {
            return;
        }
        String activeRooms = dataHousesPo.getDailyActiveRooms();
        String totalActiveRooms = dataHousesPo.getTotalActiveRooms();
        String newRooms = dataHousesPo.getDailyNewRooms();
        String activeList = dataHousesPo.getDailyActiveRoomsList();
        String totalActiveList = dataHousesPo.getTotalActiveRoomsList();
        if (!bots.contains(issuer)) {
            if (StringUtils.isBlank(activeList)) {
                JSONArray array = new JSONArray();
                activeList = seerRoom.getId();
                JSONObject object = new JSONObject();
                object.put("house", seerRoom.getOwner());
                object.put("list", activeList);
                array.add(object);
                activeList = array.toJSONString();
                activeRooms = StaticFunHandle.addHouseCount(houses, activeRooms, seerRoom.getOwner(), 1L);
            } else {
                if (!StaticFunHandle.isContainRoom(activeList, seerRoom.getOwner(), seerRoom.getId())) {
                    activeList = StaticFunHandle.addActiveRoom(activeList, seerRoom.getOwner(), seerRoom.getId());
                    activeRooms = StaticFunHandle.addHouseCount(houses, activeRooms, seerRoom.getOwner(), 1L);
                }
            }
            if (StringUtils.isBlank(totalActiveList)) {
                JSONArray array = new JSONArray();
                totalActiveList = seerRoom.getId();
                JSONObject object = new JSONObject();
                object.put("house", seerRoom.getOwner());
                object.put("list", totalActiveList);
                array.add(object);
                totalActiveList = array.toJSONString();
                totalActiveRooms = StaticFunHandle.addHouseCount(houses, totalActiveRooms, seerRoom.getOwner(), 1L);
            } else {
                if (!StaticFunHandle.isContainRoom(totalActiveList, seerRoom.getOwner(), seerRoom.getId())) {
                    totalActiveList = StaticFunHandle.addActiveRoom(totalActiveList, seerRoom.getOwner(), seerRoom.getId());
                    totalActiveRooms = StaticFunHandle.addHouseCount(houses, totalActiveRooms, seerRoom.getOwner(), 1L);
                }
            }
        }
        String totalRooms = dataHousesPo.getTotalRooms();
        dataHousesPo = new DailyDataHousesPo();
        dataHousesPo.setTimestamp(times);
        dataHousesPo.setLastBlock(current);
        String rate = StaticFunHandle.updateHousesRate(houses, activeRooms, newRooms);
        dataHousesPo.setDailyActiveRooms(activeRooms);
        dataHousesPo.setTotalActiveRooms(totalActiveRooms);
        dataHousesPo.setDailyActiveRoomsList(activeList);
        dataHousesPo.setTotalActiveRoomsList(totalActiveList);
        dataHousesPo.setDailyPrtpRate(rate);
        //总参与率
        String totalRate = StaticFunHandle.updateHousesRate(houses, totalActiveRooms, totalRooms);
        dataHousesPo.setTotalPrtpRate(totalRate);
        dataHousesService.updateById(dataHousesPo);
    }

    //新增房间数和累积房间数和新增房间参与率
    @Transactional
    public void updateNewRooms(String issuer, SeerRoom seerRoom, Long time, BigInteger current, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String houses = "";
        if (null != configPo) {
            houses = configPo.getHouses();
        }
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == dataHousesPo) {
            initDateHouses(times);
            dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (checkBlock && isCheck) {
            if (dataHousesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (houses.contains(issuer)) {
            String newRooms = dataHousesPo.getDailyNewRooms();
            newRooms = StaticFunHandle.addHouseCount(houses, newRooms, issuer, 1L);
            String totalRooms = dataHousesPo.getTotalRooms();
            totalRooms = StaticFunHandle.addHouseCount(houses, totalRooms, issuer, 1L);
            String active = dataHousesPo.getDailyActiveRooms();
            String opening = dataHousesPo.getDailyOpeningRoom();
            String totalActiveRooms = dataHousesPo.getTotalActiveRooms();
            dataHousesPo = new DailyDataHousesPo();
            dataHousesPo.setTimestamp(times);
            dataHousesPo.setLastBlock(current);
            dataHousesPo.setDailyNewRooms(newRooms);
            dataHousesPo.setTotalRooms(totalRooms);
            String rate = StaticFunHandle.updateHousesRate(issuer, active, newRooms);
            dataHousesPo.setDailyPrtpRate(rate);
            //今日可投注房间数
            dataHousesPo.setDailyOpeningRoom(StaticFunHandle.addHouseCount(houses, opening, issuer, 1L));
            //总参与率
            String totalRate = StaticFunHandle.updateHousesRate(houses, totalActiveRooms, totalRooms);
            dataHousesPo.setTotalPrtpRate(totalRate);
            dataHousesService.updateById(dataHousesPo);
        }
    }

    @Transactional
    public void updateAdvPlayAmount(SeerRoom seerRoom, String assetId, BigDecimal amount, BigInteger current, Long time, boolean checkBlock, boolean isCheck) {
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == dataHousesPo) {
            initDateHouses(times);
            dataHousesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (checkBlock && isCheck) {
            if (dataHousesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (ADV_ROOM != seerRoom.getRoomType()) {
            return;
        }
        String oldAdvPlay = dataHousesPo.getTotalAdvplayAmount();
        dataHousesPo = new DailyDataHousesPo();
        dataHousesPo.setTimestamp(times);
        String assets = SEER_ASSET;
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        oldAdvPlay = StaticFunHandle.addAssetAmount(assetId, assets, oldAdvPlay, amount).toJSONString();
        //房主高级投注总额
        dataHousesPo.setTotalAdvplayAmount(oldAdvPlay);
        dataHousesPo.setLastBlock(current);
        dataHousesService.updateById(dataHousesPo);
    }

    @Transactional
    public void updateFees(SeerRoom seerRoom, FeeBo feeBo, BigInteger current, boolean checkBlock, Long time, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo housesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == housesPo) {
            initDateHouses(times);
            housesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (null == configPo || StringUtils.isBlank(configPo.getHouses())) {
            return;
        }
        String houses = configPo.getHouses();
        if (isCheck && checkBlock) {
            if (housesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (houses.contains(seerRoom.getOwner())) {
            DailyDataHousesPo dataHousesPo = new DailyDataHousesPo();
            dataHousesPo.setTimestamp(times);
            dataHousesPo.setLastBlock(current);
            BigDecimal fee = feeBo.getOtherAssetBaseSeer();
            if (feeBo.getIsSeer()) {
                fee = feeBo.getRealAmount();
            }
            if (ADV_ROOM == seerRoom.getRoomType()) {
                //累计高级房间手续费
                dataHousesPo.setTotalAdvFees(housesPo.getTotalAdvFees().add(fee));
            } else if (PVP_ROOM == seerRoom.getRoomType()) {
                //累计PVP房间手续费
                dataHousesPo.setTotalPvpFees(housesPo.getTotalPvpFees().add(fee));
            } else if (PVD_ROOM == seerRoom.getRoomType()) {
                //累计PVD房间手续费
                dataHousesPo.setTotalPvdFees(housesPo.getTotalPvdFees().add(fee));
            }
            dataHousesService.updateById(dataHousesPo);
        }
    }

    @Transactional
    public void updateSettle(SeerRoom seerRoom, FeeBo feeBo, JSONObject result, BigInteger current, boolean checkBlock, boolean isCheck, Long time) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo housesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == housesPo) {
            initDateHouses(times);
            housesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (null == configPo || StringUtils.isBlank(configPo.getHouses())) {
            return;
        }
        String houses = configPo.getHouses();
        String assets = configPo.getAssets();
        String bots = configPo.getSeerBots();
        if (isCheck && checkBlock) {
            if (housesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        if (houses.contains(seerRoom.getOwner())) {
            String assetId = result.getString("asset_id");
            JSONArray deltas = result.getJSONArray("deltas");
            BigInteger allAmount = BigInteger.ZERO;
            BigInteger botAndOwner = BigInteger.ZERO;
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
            BigDecimal fee = feeBo.getOtherAssetBaseSeer();
            if (feeBo.getIsSeer()) {
                fee = feeBo.getRealAmount();
            }
            DailyDataHousesPo dataHousesPo = new DailyDataHousesPo();
            dataHousesPo.setTimestamp(times);
            dataHousesPo.setLastBlock(current);
            if (ADV_ROOM == seerRoom.getRoomType()) {
                //累计高级派奖总额
                dataHousesPo.setTotalAdvSettle(StaticFunHandle.addAssetAmount(assetId, assets,
                        housesPo.getTotalAdvSettle(), amountDec).toJSONString());
                //累计高级房间手续费
                dataHousesPo.setTotalAdvFees(housesPo.getTotalAdvFees().add(fee));
            } else if (PVP_ROOM == seerRoom.getRoomType()) {
                //累计PVP派奖总额
                dataHousesPo.setTotalPvpSettle(StaticFunHandle.addAssetAmount(assetId, assets,
                        housesPo.getTotalPvpSettle(), amountDec).toJSONString());
                //累计PVP房间手续费
                dataHousesPo.setTotalPvpFees(housesPo.getTotalPvpFees().add(fee));
            } else if (PVD_ROOM == seerRoom.getRoomType()) {
                //累计PVD房间手续费
                dataHousesPo.setTotalPvdFees(housesPo.getTotalPvdFees().add(fee));
            }
            dataHousesService.updateById(dataHousesPo);
        }
    }

    @Transactional
    public void updateHouseBattle(SeerRoom seerRoom, FeeBo feeBo, PredictionVo predictionVo, Long time,
                                  Boolean isCheck, Boolean checkBlock, BigInteger current) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        Long times = Times.getTimesDayZero(time);
        DailyDataHousesPo housesPo = dataHousesService.selectOneByTimestamp(times);
        if (null == housesPo) {
            initDateHouses(times);
            housesPo = dataHousesService.selectOneByTimestamp(times);
        }
        if (null == configPo || StringUtils.isBlank(configPo.getHouses())) {
            return;
        }
        String houses = configPo.getHouses();
        String assets = configPo.getAssets();
        String bots = configPo.getSeerBots();
        if (isCheck && checkBlock) {
            if (housesPo.getLastBlock().compareTo(current) >= 0) {
                return;
            }
        }
        DailyDataHousesPo dataHousesPo = new DailyDataHousesPo();
        dataHousesPo.setTimestamp(times);
        dataHousesPo.setTime(housesPo.getTime());
        dataHousesPo.setLastBlock(current);
        //adv
        if (ADV_ROOM == seerRoom.getRoomType()) {
            if (houses.contains(seerRoom.getOwner()) && !bots.contains(predictionVo.getIssuer())) {
                //累计高级房间手续费收入
                BigDecimal fee = feeBo.getOtherAssetBaseSeer();
                if (feeBo.getIsSeer()) {
                    fee = feeBo.getRealAmount();
                }
                dataHousesPo.setTotalAdvprtpFees(housesPo.getTotalAdvprtpFees().add(fee));
                //累计高级投注总额
                dataHousesPo.setTotalAdvplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                        housesPo.getTotalAdvplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            }
        }
        //pvp
        else if (PVP_ROOM == seerRoom.getRoomType()) {
            if (houses.contains(seerRoom.getOwner()) && !bots.contains(predictionVo.getIssuer())) {
                //累计PVP投注总额
                dataHousesPo.setTotalPvpplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                        housesPo.getTotalPvpplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
                //累计PVP房间手续费收入
                BigDecimal fee = feeBo.getOtherAssetBaseSeer();
                if (feeBo.getIsSeer()) {
                    fee = feeBo.getRealAmount();
                }
                dataHousesPo.setTotalPvpprtpFees(housesPo.getTotalPvpprtpFees().add(fee));
                //累计pvp抽成收入
                BigDecimal pvpPercent = new BigDecimal(seerRoom.getRunningOption().getPvpOwnerPercent())
                        .divide(SEER_DECIMALS).setScale(5);
                BigDecimal pvpProfitAmount = predictionVo.getDeltasAmount().multiply(pvpPercent)
                        .setScale(5, BigDecimal.ROUND_HALF_DOWN);
                dataHousesPo.setTotalPvpProfit(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(),
                        assets, housesPo.getTotalPvpProfit(), pvpProfitAmount).toJSONString());
            }

        }
        //pvd
        else if (PVD_ROOM == seerRoom.getRoomType()) {
            if (houses.contains(seerRoom.getOwner()) && !bots.contains(predictionVo.getIssuer())) {
                //累计PVD投注总额
                dataHousesPo.setTotalPvdplayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                        housesPo.getTotalPvdplayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            }
        }
        if (houses.contains(seerRoom.getOwner()) && !bots.contains(predictionVo.getIssuer())) {
            //累计投注人次
            dataHousesPo.setTotalPrtpTimes(StaticFunHandle.addHouseCount(houses, housesPo.getTotalPrtpTimes(),
                    seerRoom.getOwner(), 1L));
            //投注人次
            dataHousesPo.setPrtpTimes(StaticFunHandle.addHouseCount(houses, housesPo.getPrtpTimes(),
                    seerRoom.getOwner(), 1L));
            //累计投注额
            dataHousesPo.setTotalPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    housesPo.getTotalPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
        }
        dataHousesService.updateById(dataHousesPo);
    }

    @Transactional
    public void initDateHouses(Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataHousesPo housesPo = cacheManager.getDataHousesClient(time);
        if (null != housesPo) {
            return;
        }
        String assets = SEER_ASSET;
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        String houses = "";
        if (null != configPo) {
            houses = configPo.getHouses();
        }
        DailyDataHousesPo last = dataHousesService.selectLastOne();
        housesPo = new DailyDataHousesPo();
        housesPo.setTimestamp(time);
        housesPo.setTime(Times.formatDateByTimes(time));
        housesPo.setTotalPvpProfit(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setTotalSubsidy(BigDecimal.ZERO);
        housesPo.setDailySubsidy(BigDecimal.ZERO);
        housesPo.setDailyNewRooms(StaticFunHandle.initHouseCount(houses));
        housesPo.setDailyActiveRooms(StaticFunHandle.initHouseCount(houses));
        housesPo.setDailyPrtpRate(StaticFunHandle.initHouseRate(houses));
        housesPo.setTotalRooms(StaticFunHandle.initHouseCount(houses));
        housesPo.setTotalPrtpRate(StaticFunHandle.initHouseRate(houses));
        housesPo.setTotalAdvplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setTotalAdvprtpFees(BigDecimal.ZERO);
        housesPo.setTotalAdvSettle(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setDailyOpeningRoom(StaticFunHandle.initHouseCount(houses));
        housesPo.setPrtpTimes(StaticFunHandle.initHouseCount(houses));
        housesPo.setTotalPrtpTimes(StaticFunHandle.initHouseCount(houses));
        housesPo.setTotalPvpplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setTotalPlayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setTotalPvpprtpFees(BigDecimal.ZERO);
        housesPo.setTotalPvpSettle(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setTotalPvpFees(BigDecimal.ZERO);
        housesPo.setTotalPvdplayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        housesPo.setTotalPvdFees(BigDecimal.ZERO);
        housesPo.setTotalAdvFees(BigDecimal.ZERO);
        housesPo.setLastBlock(BigInteger.ZERO);
        housesPo.setTotalActiveRooms(StaticFunHandle.initHouseCount(houses));
        dataHousesService.insert(housesPo);
        //初始化total系列 记得初始化今日可参与房间数
        if (null != last) {
            housesPo.setTotalPvpProfit(StaticFunHandle.addAssetsAmount(configPo.getAssets(), last.getTotalPvpProfit(),
                    housesPo.getTotalPvpProfit()));
            housesPo.setTotalSubsidy(housesPo.getTotalSubsidy().add(last.getTotalSubsidy()));
            housesPo.setTotalAdvplayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), housesPo.getTotalAdvplayAmount(),
                    last.getTotalAdvplayAmount()));
            housesPo.setTotalAdvprtpFees(housesPo.getTotalAdvprtpFees().add(last.getTotalAdvprtpFees()));
            housesPo.setTotalAdvSettle(StaticFunHandle.addAssetsAmount(configPo.getAssets(), housesPo.getTotalAdvSettle(),
                    last.getTotalAdvSettle()));
            housesPo.setTotalPrtpTimes(StaticFunHandle.addHousesCount(houses, last.getTotalPrtpTimes(),
                    housesPo.getTotalPrtpTimes()));
            housesPo.setTotalPvpplayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), housesPo.getTotalPvpplayAmount(),
                    last.getTotalPvpplayAmount()));
            housesPo.setTotalPlayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), housesPo.getTotalPlayAmount(),
                    last.getTotalPlayAmount()));
            housesPo.setTotalPvpprtpFees(housesPo.getTotalPvpprtpFees().add(last.getTotalPvpprtpFees()));
            housesPo.setTotalPvpSettle(StaticFunHandle.addAssetsAmount(configPo.getAssets(), housesPo.getTotalPvpSettle(),
                    last.getTotalPvpSettle()));
            housesPo.setTotalPvpFees(housesPo.getTotalPvpFees().add(last.getTotalPvpFees()));
            housesPo.setTotalPvdplayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), housesPo.getTotalPvdplayAmount(),
                    last.getTotalPvdplayAmount()));
            housesPo.setTotalAdvFees(housesPo.getTotalAdvFees().add(last.getTotalAdvFees()));
            housesPo.setTotalPvdFees(housesPo.getTotalPvdFees().add(last.getTotalPvdFees()));
            String rooms = StaticFunHandle.addTotalRoomList(houses, last.getTotalActiveRoomsList(),
                    housesPo.getTotalActiveRoomsList());
            housesPo.setTotalActiveRoomsList(rooms);
            housesPo.setTotalRooms(StaticFunHandle.addHousesCount(houses, last.getTotalRooms(),
                    housesPo.getTotalRooms()));
            housesPo.setTotalActiveRooms(StaticFunHandle.addHousesCount(houses, housesPo.getTotalActiveRooms(),
                    last.getTotalActiveRooms()));
            //今日可参与房间数
            List<DailyRoomDetailsPo> list = dailyRoomService.selectListToday(time);
            String openRoom = housesPo.getDailyOpeningRoom();
            for (DailyRoomDetailsPo detailsPo : list) {
                SeerRoom seerRoom = clientFunService.getSeerRoom(detailsPo.getRoom());
                if (houses.contains(seerRoom.getOwner())) {
                    openRoom = StaticFunHandle.addHouseCount(houses, openRoom, seerRoom.getOwner(), 1L);
                }
            }
            housesPo.setDailyOpeningRoom(openRoom);
            dataHousesService.updateById(housesPo);
        }
    }
}
