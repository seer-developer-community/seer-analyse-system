package com.seer.operation.handleService;

import com.seer.operation.entity.DailyHouseRoomPo;
import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.SeerRoom;
import com.seer.operation.service.DailyHouseRoomService;
import com.seer.operation.service.DailyRoomService;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

import static com.seer.operation.request.Constants.SEER_ASSET;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

@Service
public class DailyHouseRoomHandle {
    @Autowired
    private DataCacheManager cacheManager;
    @Autowired
    private DailyHouseRoomService houseRoomService;
    @Autowired
    private DailyRoomService dailyRoomService;
    @Autowired
    private ClientFunService clientFunService;

    @Transactional
    public void updateBy46(String issuer, SeerRoom seerRoom, Long time, BigInteger current, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String houses = "";
        if (null != configPo && StringUtils.isNotBlank(configPo.getHouses())) {
            houses = configPo.getHouses();
        }
        Long zeroTimes = Times.getTimesDayZero(time);
        DailyHouseRoomPo houseRoomPo = houseRoomService.selectOneByTimestamp(zeroTimes);
        if (null == houseRoomPo) {
            initHouseRoom(zeroTimes);
            houseRoomPo = houseRoomService.selectOneByTimestamp(zeroTimes);
        }
        if (checkBlock && isCheck) {
            if (houseRoomPo.getLastBlock() >= current.intValue()) {
                return;
            }
        }
        //只统计平台房主账户
        if (houses.contains(issuer)) {
            DailyHouseRoomPo dailyHouseRoomPo = new DailyHouseRoomPo();
            dailyHouseRoomPo.setTimestamp(houseRoomPo.getTimestamp());
            //新增房间数
            dailyHouseRoomPo.setDailyNewRooms(StaticFunHandle.addHouseCount(houses, houseRoomPo.getDailyNewRooms()
                    , issuer, 1L));
            //新增房间参与率 daily_prtp_rate
            String dailyRate = StaticFunHandle.updateHousesRate(houses, houseRoomPo.getNewActiveRooms(), houseRoomPo.getDailyNewRooms());
            dailyHouseRoomPo.setDailyPrtpRate(dailyRate);
            //累计房间数 total_rooms
            dailyHouseRoomPo.setTotalRooms(StaticFunHandle.addHouseCount(houses, houseRoomPo.getTotalRooms()
                    , issuer, 1L));
            //累计房间参与率 total_prtp_rate
            String totalRate = StaticFunHandle.updateHousesRate(houses, houseRoomPo.getTotalActiveRooms(), houseRoomPo.getTotalRooms());
            dailyHouseRoomPo.setTotalPrtpRate(totalRate);
            //可投注房间数
            dailyHouseRoomPo.setDailyOpeningRoom(StaticFunHandle.addHouseCount(houses, houseRoomPo.getDailyOpeningRoom()
                    , issuer, 1L));
            //每天新增房间列表
            String newRoomsList = houseRoomPo.getNewRoomsList();
            dailyHouseRoomPo.setNewRoomsList(StaticFunHandle.addActiveRoom(newRoomsList, issuer, seerRoom.getId()));
            dailyHouseRoomPo.setLastBlock(current.intValue());
            houseRoomService.updateById(dailyHouseRoomPo);
        }
    }

    @Transactional
    public void updateBy50(PredictionVo predictionVo, SeerRoom seerRoom, Long time, BigInteger current, boolean checkBlock, boolean isCheck) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        Long zeroTimes = Times.getTimesDayZero(time);
        DailyHouseRoomPo houseRoomPo = houseRoomService.selectOneByTimestamp(zeroTimes);
        if (null == houseRoomPo) {
            initHouseRoom(zeroTimes);
            houseRoomPo = houseRoomService.selectOneByTimestamp(zeroTimes);
        }
        String houses = "";
        String bots = "";
        String assets = "";
        if (null != configPo) {
            bots = configPo.getSeerBots();
            houses = configPo.getHouses();
            assets = configPo.getAssets();
        }
        if (isCheck && checkBlock) {
            if (houseRoomPo.getLastBlock() >= current.intValue()) {
                return;
            }
        }
        if (houses.contains(seerRoom.getOwner()) && !bots.contains(predictionVo.getIssuer())) {
            String room = seerRoom.getId();
            boolean isExistNewRoom = StaticFunHandle.isContainRoom(houseRoomPo.getNewRoomsList(), seerRoom.getOwner(), room);
            boolean isExistActiveRoom = StaticFunHandle.isContainRoom(houseRoomPo.getNewRoomsActiveList(), seerRoom.getOwner(), room);
            if (isExistNewRoom && !isExistActiveRoom) {
                //每天新增房间中且真人参与的房间数
                houseRoomPo.setNewActiveRooms(StaticFunHandle.addHouseCount(houses, houseRoomPo.getNewActiveRooms(),
                        seerRoom.getOwner(), 1L));
                //每天新增房间中且真人参与的房间列表
                houseRoomPo.setNewRoomsActiveList(StaticFunHandle.addRoomList(houses, houseRoomPo.getNewRoomsActiveList(),
                        room, seerRoom.getOwner()));
                //新增房间参与率
                String dailyRate = StaticFunHandle.updateHousesRate(houses, houseRoomPo.getNewActiveRooms(), houseRoomPo.getDailyNewRooms());
                houseRoomPo.setDailyPrtpRate(dailyRate);
            }
            if (!StaticFunHandle.isContainRoom(houseRoomPo.getTotalActiveRoomsList(), seerRoom.getOwner(), room)) {
                //累计真人参与房间列表
                houseRoomPo.setTotalActiveRoomsList(StaticFunHandle.addRoomList(houses, houseRoomPo.getTotalActiveRoomsList(),
                        room, seerRoom.getOwner()));
                //累计有真人参与的房间数
                houseRoomPo.setTotalActiveRooms(StaticFunHandle.addHouseCount(houses, houseRoomPo.getTotalActiveRooms(),
                        seerRoom.getOwner(), 1L));
                //累计房间参与率
                String totalRate = StaticFunHandle.updateHousesRate(houses, houseRoomPo.getTotalActiveRooms(), houseRoomPo.getTotalRooms());
                houseRoomPo.setTotalPrtpRate(totalRate);
            }
            //累计投注人次
            houseRoomPo.setTotalPrtpTimes(StaticFunHandle.addHouseCount(houses, houseRoomPo.getTotalPrtpTimes(),
                    seerRoom.getOwner(), 1L));
            //投注人次
            houseRoomPo.setPrtpTimes(StaticFunHandle.addHouseCount(houses, houseRoomPo.getPrtpTimes(),
                    seerRoom.getOwner(), 1L));
            //投注额
            houseRoomPo.setPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    houseRoomPo.getPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            //累计投注额
            houseRoomPo.setTotalPlayAmount(StaticFunHandle.addAssetAmount(predictionVo.getDeltasAsset(), assets,
                    houseRoomPo.getTotalPlayAmount(), predictionVo.getDeltasAmount()).toJSONString());
            houseRoomPo.setLastBlock(current.intValue());
            houseRoomService.updateById(houseRoomPo);
        }
    }

    @Transactional
    public void initHouseRoom(Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyHouseRoomPo houseRoomPo = houseRoomService.selectOneByTimestamp(time);
        if (null != houseRoomPo) {
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
        DailyHouseRoomPo last = houseRoomService.selectLastOne();
        houseRoomPo = new DailyHouseRoomPo();
        houseRoomPo.setTimestamp(time);
        houseRoomPo.setTime(Times.formatDateByTimes(time));
        houseRoomPo.setDailyNewRooms(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setDailyPrtpRate(StaticFunHandle.initHouseRate(houses));
        houseRoomPo.setTotalRooms(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setTotalPrtpRate(StaticFunHandle.initHouseRate(houses));
        houseRoomPo.setDailyOpeningRoom(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setPrtpTimes(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setTotalPrtpTimes(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setPlayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        houseRoomPo.setTotalPlayAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        houseRoomPo.setNewRoomsActiveList(StaticFunHandle.initRoomsList("", houses));
        houseRoomPo.setNewActiveRooms(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setNewRoomsList(StaticFunHandle.initRoomsList("", houses));
        houseRoomPo.setTotalActiveRoomsList(StaticFunHandle.initRoomsList("", houses));
        houseRoomPo.setTotalActiveRooms(StaticFunHandle.initHouseCount(houses));
        houseRoomPo.setLastBlock(0);
        houseRoomPo.setTotalActiveRooms(StaticFunHandle.initHouseCount(houses));
        //今日可参与房间数
        List<DailyRoomDetailsPo> list = dailyRoomService.selectListToday(time);
        String openRoom = houseRoomPo.getDailyOpeningRoom();
        for (DailyRoomDetailsPo detailsPo : list) {
            SeerRoom seerRoom = clientFunService.getSeerRoom(detailsPo.getRoom());
            if (houses.contains(seerRoom.getOwner())) {
                openRoom = StaticFunHandle.addHouseCount(houses, openRoom, seerRoom.getOwner(), 1L);
            }
        }
        houseRoomPo.setDailyOpeningRoom(openRoom);
        //初始化total系列 记得初始化今日可参与房间数
        if (null != last) {
            houseRoomPo.setTotalRooms(StaticFunHandle.addHousesCount(houses, last.getTotalRooms(),
                    houseRoomPo.getTotalRooms()));
            houseRoomPo.setTotalPrtpTimes(StaticFunHandle.addHousesCount(houses, last.getTotalPrtpTimes(),
                    houseRoomPo.getTotalPrtpTimes()));
            houseRoomPo.setTotalPlayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), houseRoomPo.getTotalPlayAmount(),
                    last.getTotalPlayAmount()));
            //累计真人参与房间列表
            String rooms = StaticFunHandle.addTotalRoomList(houses, last.getTotalActiveRoomsList(),
                    houseRoomPo.getTotalActiveRoomsList());
            houseRoomPo.setTotalActiveRoomsList(rooms);
            //累计有真人参与的房间数
            houseRoomPo.setTotalActiveRooms(StaticFunHandle.addHousesCount(houses, last.getTotalActiveRooms(),
                    houseRoomPo.getTotalActiveRooms()));
            //累计房间参与率
            String totalActiveRooms = houseRoomPo.getTotalActiveRooms();
            String totalRooms = houseRoomPo.getTotalRooms();
            String totalRate = StaticFunHandle.updateHousesRate(houses, totalActiveRooms, totalRooms);
            houseRoomPo.setTotalPrtpRate(totalRate);
        }
        houseRoomService.insert(houseRoomPo);
    }
}
