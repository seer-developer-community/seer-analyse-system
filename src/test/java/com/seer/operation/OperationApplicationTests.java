package com.seer.operation;

import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.handleService.*;
import com.seer.operation.rpcClient.SeerJsonRpcClient;
import com.seer.operation.rpcClient.response.HouseByAccount;
import com.seer.operation.service.*;
import com.seer.operation.threadPool.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class OperationApplicationTests {
    @Autowired
    private AsyncTaskService taskService;
    @Autowired
    private BlockSyncService blockSyncService;
    @Autowired
    private DailyDataFaucetHandle dataFaucetHandle;
    @Autowired
    private DailyDataHousesHandle dataHousesHandle;
    @Autowired
    private DailyDataTotalHandle dataTotalHandle;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private RoomHandle roomHandle;
    @Autowired
    private Scan scan;
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private DailyDataFaucetService dataFaucetService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private FixDataService fixDataService;
    @Autowired
    private DailyDataHousesService dataHousesService;
    @Autowired
    private DailyDataTotalService dataTotalService;
    @Autowired
    private DailyRoomService dailyRoomService;
    @Autowired
    private ClientFunService clientFunService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void fixDailyHouse() throws InterruptedException {
//        Long bT = System.currentTimeMillis();
//        List<TransactionPo> list = transactionService.selectList(1, 11352058, 50);
//        List<TransactionPo> list1_0 = new LinkedList<>();
//        List<TransactionPo> list1_1 = new LinkedList<>();
//        List<TransactionPo> list1 = new LinkedList<>();
//        List<TransactionPo> list2 = new LinkedList<>();
//        List<TransactionPo> list2_1 = new LinkedList<>();
//        List<TransactionPo> list2_2 = new LinkedList<>();
//        List<TransactionPo> list2_3 = new LinkedList<>();
//        List<TransactionPo> list2_4 = new LinkedList<>();
//        List<TransactionPo> list2_5 = new LinkedList<>();
//        List<TransactionPo> list3 = new LinkedList<>();
//        List<TransactionPo> list4 = new LinkedList<>();
//        List<TransactionPo> list5 = new LinkedList<>();
//        List<TransactionPo> list6 = new LinkedList<>();
//        List<TransactionPo> list7 = new LinkedList<>();
//        List<TransactionPo> list8 = new LinkedList<>();
//        List<TransactionPo> list9 = new LinkedList<>();
//        List<TransactionPo> list10 = new LinkedList<>();
//        List<TransactionPo> list11 = new LinkedList<>();
//        List<TransactionPo> list12 = new LinkedList<>();
//        List<TransactionPo> list13 = new LinkedList<>();
//        for (int i = 0; i < list.size(); i++) {//适应于50操作的线程分配
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1526659200000L) {//2018.5.19
//                list1_0.add(po);
//            }
//            if (time <= 1526745600000L) {//2018.5.20
//                list1_1.add(po);
//            }
//            else if (time <= 1527004800000L) {//2018.5.23
//                list1.add(po);
//            }
//            if (time <= 1527436800000L) {//5.28
//                list2_1.add(po);
//            }
//            else if (time <= 1527955200000L) {//6.3
//                list2_2.add(po);
//            }
//            else if (time <= 1529337600000L) {//6.19
//                list2_3.add(po);
//            } else if (time <= 1529856000000L) {//6.25
//                list2_4.add(po);
//            } else if (time <= 1530374400000L) {//7.1
//                list2_5.add(po);
//            } else if (time <= 1531843200000L) {//7.18
//                list2.add(po);
//            } else if (time <= 1534521600000L) {//8.18
//                list3.add(po);
//            } else if (time <= 1537200000000L) {//9.18
//                list4.add(po);
//            } else if (time <= 1539792000000L) {//10.18
//                list5.add(po);
//            } else if (time <= 1541001600000L) {//11.1
//                list6.add(po);
//            } else if (time <= 1543593600000L) {//12.1
//                list7.add(po);
//            }
//            else if (time <= 1547740800000L) {//2019.1.18
//                list8.add(po);
//            }
//            else if (time <= 1550419200000L) {//2.18
//                list9.add(po);
//            }
//            else if (time <= 1552838400000L) {//3.18
//                list10.add(po);
//            }
//            else if (time <= 1555516800000L) {//4.18
//                list11.add(po);
//            }
//            else if (time <= 1558108800000L) {//5.18
//                list12.add(po);
//            }
//            else {
//                list13.add(po);
//            }
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(12);
//        asyncTaskService.fixList(countDownLatch,list1_0,99);
//        asyncTaskService.fixList(countDownLatch, list1_1, 99);
//        asyncTaskService.fixList(countDownLatch, list1, 99);
//        asyncTaskService.fixList(countDownLatch, list2, 99);
//        asyncTaskService.fixList(countDownLatch, list2_1, 99);
//        asyncTaskService.fixList(countDownLatch, list2_2, 99);
//        asyncTaskService.fixList(countDownLatch, list2_3, 99);
//        asyncTaskService.fixList(countDownLatch, list2_4, 99);
//        asyncTaskService.fixList(countDownLatch, list2_5, 99);
//        asyncTaskService.fixList(countDownLatch, list3, 99);
//        asyncTaskService.fixList(countDownLatch, list4, 99);
//        asyncTaskService.fixList(countDownLatch, list5, 99);
//        asyncTaskService.fixList(countDownLatch, list6, 99);
//        asyncTaskService.fixList(countDownLatch, list7, 99);
//        asyncTaskService.fixList(countDownLatch, list8, 99);
//        asyncTaskService.fixList(countDownLatch, list9, 99);
//        asyncTaskService.fixList(countDownLatch, list10, 99);
//        asyncTaskService.fixList(countDownLatch, list11, 99);
//        asyncTaskService.fixList(countDownLatch, list12, 99);
//        asyncTaskService.fixList(countDownLatch, list13, 99);
//        countDownLatch.await();
//        log.info("交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - bT);
    }

    @Test
    public void fixFaucetTotalData() {
//        List<DailyDataFaucetPo> list = dataFaucetService.selectList();
//        UserConfigPo configPo = configService.selectOne(1);
//        for (int i = 1; i < list.size(); i++) {
//            DailyDataFaucetPo faucetPo1 = list.get(i - 1);
//            DailyDataFaucetPo faucetPo2 = list.get(i);
//            faucetPo2.setTotalRegistered(faucetPo2.getTotalRegistered() + faucetPo1.getTotalRegistered());
//            faucetPo2.setTotalPlayers(faucetPo2.getTotalPlayers() + faucetPo1.getTotalPlayers());
//            faucetPo2.setTotalTransferCountFees(StaticFunHandle.addAssetsAmount(configPo.getAssets(), faucetPo2.getTotalTransferCountFees(), faucetPo1.getTotalTransferCountFees()));
//            faucetPo2.setTotalDepositCount(StaticFunHandle.addAssetsCount(configPo.getAssets(), faucetPo2.getTotalDepositCount(), faucetPo1.getTotalDepositCount()));
//            faucetPo2.setTotalDepositAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), faucetPo2.getTotalDepositAmount(), faucetPo1.getTotalDepositAmount()));
//            faucetPo2.setTotalTransferCount(StaticFunHandle.addAssetsCount(configPo.getAssets(), faucetPo2.getTotalTransferCount(), faucetPo1.getTotalTransferCount()));
//            faucetPo2.setTotalTransferAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), faucetPo2.getTotalTransferAmount(), faucetPo1.getTotalTransferAmount()));
//            faucetPo2.setTotalRegisteredFees(faucetPo2.getTotalRegisteredFees().add(faucetPo1.getTotalRegisteredFees()));
//            dataFaucetService.updateById(faucetPo2);
//        }
    }

    @Test
    public void fixDailyHousesTotal() throws InterruptedException {
//        Long bT = System.currentTimeMillis();
//        修复46的新增房间
//        List<TransactionPo> list = transactionService.selectList(1, 11352058, 46);
//        fixDataService.dealHandle(list);
        //修复total叠加
//        List<DailyDataHousesPo> list = dataHousesService.selectList();
//        UserConfigPo configPo = configService.selectOne(1);
//        for (int i = 0; i < list.size(); i++) {
//            DailyDataHousesPo dataHousesPo1 = list.get(i - 1);
//            DailyDataHousesPo dataHousesPo2 = list.get(i);
        //修复total_rooms
//            dataHousesPo2.setTotalRooms(StaticFunHandle.addHousesCount(configPo.getHouses(), dataHousesPo1.getTotalRooms(), dataHousesPo2.getTotalRooms()));
        //修复total_active_rooms
//            if (StringUtils.isBlank(dataHousesPo2.getTotalActiveRoomsList())) {
//                continue;
//            }
//            JSONArray array = JSON.parseArray(dataHousesPo2.getTotalActiveRoomsList());
//            for (int k = 0; k < array.size(); k++) {
//                String house = array.getJSONObject(k).getString("house");
//                int len = array.getJSONObject(k).getString("list").split(",").length;
//                dataHousesPo2.setTotalActiveRooms(StaticFunHandle.addHouseCount(configPo.getHouses(), dataHousesPo2.getTotalActiveRooms(),
//                        house, Long.valueOf(len)));
//            }
        //修复total_active_rooms_list
//            if (StringUtils.isBlank(dataHousesPo1.getTotalActiveRoomsList())) {
//                continue;
//            }
//            String rooms = StaticFunHandle.addTotalRoomList(configPo.getHouses(), dataHousesPo1.getTotalActiveRoomsList(),
//                    dataHousesPo2.getTotalActiveRoomsList());
//            dataHousesPo2.setTotalActiveRoomsList(rooms);
        //修复total_prtp_rate,注意i从0开始
//            String rate = StaticFunHandle.updateHousesRate(configPo.getHouses(),
//                    dataHousesPo2.getTotalActiveRooms(), dataHousesPo2.getTotalRooms());
//            dataHousesPo2.setTotalPrtpRate(rate);
        //其他累计叠加修复
//            dataHousesPo2.setTotalPvpProfit(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalPvpProfit(),
//                    dataHousesPo1.getTotalPvpProfit()));
//            dataHousesPo2.setTotalSubsidy(dataHousesPo2.getTotalSubsidy().add(dataHousesPo1.getTotalSubsidy()));
//            dataHousesPo2.setTotalAdvplayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalAdvplayAmount(),
//                    dataHousesPo1.getTotalAdvplayAmount()));
//            dataHousesPo2.setTotalAdvprtpFees(dataHousesPo2.getTotalAdvprtpFees().add(dataHousesPo1.getTotalAdvprtpFees()));
//            dataHousesPo2.setTotalAdvSettle(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalAdvSettle(),
//                    dataHousesPo1.getTotalAdvSettle()));
//            dataHousesPo2.setTotalPrtpTimes(StaticFunHandle.addHousesCount(configPo.getHouses(), dataHousesPo1.getTotalPrtpTimes(), dataHousesPo2.getTotalPrtpTimes()));
//            dataHousesPo2.setTotalPvpplayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalPvpplayAmount(),
//                    dataHousesPo1.getTotalPvpplayAmount()));
//            dataHousesPo2.setTotalPlayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalPlayAmount(),
//                    dataHousesPo1.getTotalPlayAmount()));
//            dataHousesPo2.setTotalPvpprtpFees(dataHousesPo2.getTotalPvpprtpFees().add(dataHousesPo1.getTotalPvpprtpFees()));
//            dataHousesPo2.setTotalPvpSettle(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalPvpSettle(),
//                    dataHousesPo1.getTotalPvpSettle()));
//            dataHousesPo2.setTotalPvpFees(dataHousesPo2.getTotalPvpFees().add(dataHousesPo1.getTotalPvpFees()));
//            dataHousesPo2.setTotalPvdplayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), dataHousesPo2.getTotalPvdplayAmount(),
//                    dataHousesPo1.getTotalPvdplayAmount()));
//            dataHousesPo2.setTotalAdvFees(dataHousesPo2.getTotalAdvFees().add(dataHousesPo1.getTotalAdvFees()));
//            dataHousesPo2.setTotalPvdFees(dataHousesPo2.getTotalPvdFees().add(dataHousesPo1.getTotalPvdFees()));
//            dataHousesService.updateById(dataHousesPo2);
//        }
//        log.info("交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - bT);
    }

    @Test
    public void fixDailyFaucet() throws InterruptedException {
//        Long bT = System.currentTimeMillis();
//        List<TransactionPo> list = transactionService.selectList(1, 11352058, 46);
//        List<TransactionPo> list1_0 = new LinkedList<>();
//        List<TransactionPo> list1_1 = new LinkedList<>();
//        List<TransactionPo> list1 = new LinkedList<>();
//        List<TransactionPo> list2 = new LinkedList<>();
//        List<TransactionPo> list2_1 = new LinkedList<>();
//        List<TransactionPo> list2_2 = new LinkedList<>();
//        List<TransactionPo> list2_3 = new LinkedList<>();
//        List<TransactionPo> list2_4 = new LinkedList<>();
//        List<TransactionPo> list2_5 = new LinkedList<>();
//        List<TransactionPo> list3 = new LinkedList<>();
//        List<TransactionPo> list4 = new LinkedList<>();
//        List<TransactionPo> list5 = new LinkedList<>();
//        List<TransactionPo> list6 = new LinkedList<>();
//        List<TransactionPo> list7 = new LinkedList<>();
//        List<TransactionPo> list8 = new LinkedList<>();
//        List<TransactionPo> list9 = new LinkedList<>();
//        List<TransactionPo> list10 = new LinkedList<>();
//        List<TransactionPo> list11 = new LinkedList<>();
//        List<TransactionPo> list12 = new LinkedList<>();
//        List<TransactionPo> list13 = new LinkedList<>();
//        for (int i = 0; i < list.size(); i++) {//适应于0操作的线程分配，注意修改countDown和未分配的线程注释
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1529251200000L) {//2018.6.18
//                list1.add(po);
//            }
//            else if (time <= 1529683200000L) {//6.23
//                list2_1.add(po);
//            } else if (time <= 1530115200000L) {//6.28
//                list2_2.add(po);
//            } else if (time <= 1530547200000L) {//7.3
//                list2_3.add(po);
//            } else if (time <= 1530979200000L) {//7.8
//                list2_4.add(po);
//            }
//            else if (time <= 1531411200000L) {//7.13
//                list2_5.add(po);
//            }
//            else if (time <= 1531843200000L) {//7.18
//                list2.add(po);
//            } else if (time <= 1534521600000L) {//8.18
//                list3.add(po);
//            }
////            else if (time <= 1537200000000L) {//9.18
////                list4.add(po);
////            }
//            else if (time <= 1539792000000L) {//10.18
//                list5.add(po);
//            }
////            else if (time <= 1542470400000L) {//11.18
////                list6.add(po);
////            }
////            else if (time <= 1545062400000L) {//12.18
////                list7.add(po);
////            }
////            else if (time <= 1547740800000L) {//2019.1.18
////                list8.add(po);
////            }
//            else if (time <= 1550419200000L) {//2.18
//                list9.add(po);
//            }
////            else if (time <= 1552838400000L) {//3.18
////                list10.add(po);
////            }
////            else if (time <= 1555516800000L) {//4.18
////                list11.add(po);
////            }
////            else if (time <= 1558108800000L) {//5.18
////                list12.add(po);
////            }
//            else {
//                list13.add(po);
//            }
//        }
//        for (int i = 0; i < list.size(); i++) {//适应于4操作的线程分配
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1526659200000L) {//2018.5.19
//                list1_0.add(po);
//            }
//            else if (time <= 1526745600000L) {//2018.5.20
//                list1_1.add(po);
//            } else if (time <= 1527004800000L) {//2018.5.23
//                list1.add(po);
//            } else if (time <= 1527436800000L) {//5.28
//                list2_1.add(po);
//            }
////            else if (time <= 1527955200000L) {//6.3
////                list2_2.add(po);
////            }
////            else if (time <= 1528387200000L) {//6.8
////                list2_3.add(po);
////            }
//            else if (time <= 1528819200000L) {//6.13
//                list2_4.add(po);
//            }
////            else if (time <= 1529251200000L) {//6.18
////                list2_5.add(po);
////            }
//            else if (time <= 1531843200000L) {//7.18
//                list2.add(po);
//            }
//            else if (time <= 1534521600000L) {//8.18
//                list3.add(po);
//            }
////            else if (time <= 1537200000000L) {//9.18
////                list4.add(po);
////            }
////            else if (time <= 1539792000000L) {//10.18
////                list5.add(po);
////            }
//            else if (time <= 1542470400000L) {//11.18
//                list6.add(po);
//            }
//            else if (time <= 1545062400000L) {//12.18
//                list7.add(po);
//            }
//            else if (time <= 1547740800000L) {//2019.1.18
//                list8.add(po);
//            }
////            else if (time <= 1550419200000L) {//2.18
////                list9.add(po);
////            }
//            else if (time <= 1552838400000L) {//3.18
//                list10.add(po);
//            }
////            else if (time <= 1555516800000L) {//4.18
////                list11.add(po);
////            }
////            else if (time <= 1558108800000L) {//5.18
////                list12.add(po);
////            }
//            else {
//                list13.add(po);
//            }
//        }
//        for (int i = 0; i < list.size(); i++) {//适应于50操作的线程分配
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1526659200000L) {//2018.5.19
//                list1_0.add(po);
//            }
//            if (time <= 1526745600000L) {//2018.5.20
//                list1_1.add(po);
//            }
//            else if (time <= 1527004800000L) {//2018.5.23
//                list1.add(po);
//            }
//            else if (time <= 1527436800000L) {//5.28
//                list2_1.add(po);
//            }
//            else if (time <= 1527955200000L) {//6.3
//                list2_2.add(po);
//            }
//            if (time <= 1528646400000L) {//6.11
//                list2_3.add(po);
//            }
//            if (time <= 1529856000000L) {//6.25
//                list2_4.add(po);
//            } else if (time <= 1530374400000L) {//7.1
//                list2_5.add(po);
//            } else if (time <= 1531843200000L) {//7.18
//                list2.add(po);
//            } else if (time <= 1534521600000L) {//8.18
//                list3.add(po);
//            } else if (time <= 1537200000000L) {//9.18
//                list4.add(po);
//            } else if (time <= 1539792000000L) {//10.18
//                list5.add(po);
//            } else if (time <= 1541001600000L) {//11.1
//                list6.add(po);
//            } else if (time <= 1543593600000L) {//12.1
//                list7.add(po);
//            } else if (time <= 1547740800000L) {//2019.1.18
//                list8.add(po);
//            }
//            else if (time <= 1550419200000L) {//2.18
//                list9.add(po);
//            }
//            else if (time <= 1552838400000L) {//3.18
//                list10.add(po);
//            }
//            else if (time <= 1555516800000L) {//4.18
//                list11.add(po);
//            }
//            else if (time <= 1558108800000L) {//5.18
//                list12.add(po);
//            }
//            else {
//                list13.add(po);
//            }
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(11);
//        asyncTaskService.fixList(countDownLatch,list1_0,99);
//        asyncTaskService.fixList(countDownLatch, list1_1, 99);
//        asyncTaskService.fixList(countDownLatch, list1, 99);
//        asyncTaskService.fixList(countDownLatch, list2, 99);
//        asyncTaskService.fixList(countDownLatch, list2_1, 99);
//        asyncTaskService.fixList(countDownLatch, list2_2, 99);
//        asyncTaskService.fixList(countDownLatch, list2_3, 99);
//        asyncTaskService.fixList(countDownLatch, list2_4, 99);
//        asyncTaskService.fixList(countDownLatch, list2_5, 99);
//        asyncTaskService.fixList(countDownLatch, list3, 99);
//        asyncTaskService.fixList(countDownLatch, list4, 99);
//        asyncTaskService.fixList(countDownLatch, list5, 99);
//        asyncTaskService.fixList(countDownLatch, list6, 99);
//        asyncTaskService.fixList(countDownLatch, list7, 99);
//        asyncTaskService.fixList(countDownLatch, list8, 99);
//        asyncTaskService.fixList(countDownLatch, list9, 99);
//        asyncTaskService.fixList(countDownLatch, list10, 99);
//        asyncTaskService.fixList(countDownLatch, list11, 99);
//        asyncTaskService.fixList(countDownLatch, list12, 99);
//        asyncTaskService.fixList(countDownLatch, list13, 99);
//        countDownLatch.await();
//        log.info("交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - bT);
    }

    @Test
    public void fixData() throws InterruptedException {
//        Long bT = System.currentTimeMillis();
//        List<TransactionPo> list = transactionService.selectList(1, 11352058, 49);
//        List<TransactionPo> list1 = new LinkedList<>();
//        List<TransactionPo> list2 = new LinkedList<>();
//        List<TransactionPo> list3 = new LinkedList<>();
//        List<TransactionPo> list4 = new LinkedList<>();
//        List<TransactionPo> list5 = new LinkedList<>();
//        List<TransactionPo> list6 = new LinkedList<>();
//        List<TransactionPo> list7 = new LinkedList<>();
//        List<TransactionPo> list8 = new LinkedList<>();
//        List<TransactionPo> list9 = new LinkedList<>();
//        List<TransactionPo> list10 = new LinkedList<>();
//        List<TransactionPo> list11 = new LinkedList<>();
//        List<TransactionPo> list12 = new LinkedList<>();
//        List<TransactionPo> list13 = new LinkedList<>();
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1529251200000L) {//2018.6.18
//                list1.add(po);
//            } else if (time <= 1531843200000L) {//7.18
//                list2.add(po);
//            } else if (time <= 1534521600000L) {//8.18
//                list3.add(po);
//            } else if (time <= 1537200000000L) {//9.18
//                list4.add(po);
//            } else if (time <= 1539792000000L) {//10.18
//                list5.add(po);
//            } else if (time <= 1542470400000L) {//11.18
//                list6.add(po);
//            } else if (time <= 1545062400000L) {//12.18
//                list7.add(po);
//            } else if (time <= 1547740800000L) {//2019.1.18
//                list8.add(po);
//            } else if (time <= 1550419200000L) {//2.18
//                list9.add(po);
//            } else if (time <= 1552838400000L) {//3.18
//                list10.add(po);
//            } else if (time <= 1555516800000L) {//4.18
//                list11.add(po);
//            } else if (time <= 1558108800000L) {//5.18
//                list12.add(po);
//            } else {
//                list13.add(po);
//            }
//        }
//        int blocks = list.size();
//        int clientNum = 1;
//        if (blocks >= 1000) {
//            clientNum = 30;
//        } else if (blocks >= 100) {
//            clientNum = 10;
//        }
//        int value = blocks / clientNum;
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        asyncTaskService.fixList(countDownLatch, list1, 99);
//        asyncTaskService.fixList(countDownLatch, list2, 99);
//        asyncTaskService.fixList(countDownLatch, list3, 99);
//        asyncTaskService.fixList(countDownLatch, list4, 99);
//        asyncTaskService.fixList(countDownLatch, list5, 99);
//        asyncTaskService.fixList(countDownLatch, list6, 99);
//        asyncTaskService.fixList(countDownLatch, list7, 99);
//        asyncTaskService.fixList(countDownLatch, list8, 99);
//        asyncTaskService.fixList(countDownLatch, list9, 99);
//        asyncTaskService.fixList(countDownLatch, list10, 99);
//        asyncTaskService.fixList(countDownLatch, list11, 99);
//        asyncTaskService.fixList(countDownLatch, list12, 99);
//        asyncTaskService.fixList(countDownLatch, list13, 99);

//        log.info("启用 {} 个线程，需要扫的块数: {}，平均每个线程分配的数量：{}", clientNum, blocks, value);
//        for (int i = 0; i < 1; i++) {
//            List<TransactionPo> transactionPos = new LinkedList<>();
//            int e = (i + 1) * value;
//            if (i == clientNum - 1) {
//                e = blocks;
//            }
//            for (int j = i * value; j < e; j++) {
//                transactionPos.add(list.get(j));
//            }
//            asyncTaskService.fixList(countDownLatch, transactionPos, 99);
//        }
//        asyncTaskService.fixList(countDownLatch, list, 99);
//        countDownLatch.await();
//        log.info("交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - bT);
    }

    @Test
    public void getIdByName() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        String accounts = "susu666,ino1010,jeff2018,xiada123,lqt543278,luefeng213,xiaoai9,zyx-1212,hangbang55,linwei2233,asen0101,power-me2,elfp7,zzzz,tptp,qwlf,uback110,tif88,xmx99,love-seer666,phil77,gbc,qqk,ziyuan911231,limit11,josua33,ibot01,zzh,mmkk,apple3,alice02,seer-bp,tomorrow2,tomato7,jxl,daniel82,lao5,hot-bot,ff001,pdpd20,super-seer,gege0401,sabrina1962,titan01,yy11,zlx";
//        List<String> list = Arrays.asList(accounts.split(","));
//        StringBuffer buffer = new StringBuffer();
//        for (int i = 0; i < list.size(); i++) {
//            GetAccount account = client.getAccount(list.get(i));
//            buffer.append(account.getId()).append(",");
//        }
//        System.out.println(buffer.toString());
    }

    @Test
    public void contextLoads() {
//        System.out.println(formatToDate(System.currentTimeMillis()));
//        System.out.println(formatTDateToEastZeroTimes("2019-05-19T20:02:48"));
//        System.out.println(Arrays.asList("1.3.0".split(",")));
//        System.out.println(Math.log(5));
//        BigDecimal v = new BigDecimal("53453");
//        System.out.println(v.divide(new BigDecimal("23"),5,BigDecimal.ROUND_HALF_DOWN));
//        BigInteger begin = BigInteger.valueOf(11000000);
//        BigInteger end = BigInteger.valueOf(11230737);
//        System.out.println(end.subtract(begin).add(BigInteger.ONE));
//        int value = (end.subtract(begin).add(BigInteger.ONE)).intValue() / 40;
//        System.out.println(value);
//        for (int i = 0; i < 40; i++) {
//            int b = i * value + begin.intValue() + 1;
//            int e = (i + 1) * value + begin.intValue();
//            if (i == 39) {
//                System.out.println(b + "-" + end.intValue());
//            } else {
//                System.out.println(b + "-" + e);
//            }
//        }
//        String str = updateHousesRate("1.2.14054,1.2.13634,1.2.13647", "[{\"count\":\"1\",\"house\":\"1.2.14054\"},{\"count\":\"0\",\"house\":\"1.2.13634\"},{\"count\":\"1\",\"house\":\"1.2.13647\"}]",
//                "[{\"count\":\"2\",\"house\":\"1.2.14054\"},{\"count\":\"1\",\"house\":\"1.2.13634\"},{\"count\":\"0\",\"house\":\"1.2.13647\"}]");
//        System.out.println(str);
//        String str = "1.15.1392,1.15.1385,1.15.1369,1.15.1368,1.15.1355,1.15.1246,1.15.1318,1.15.1315,1.15.1307,1.15.1288,1.15.1277,1.15.1258,1.15.1243,1.15.1211,1.15.1235,1.15.1216,1.15.1213,1.15.1178,1.15.1196,1.15.1136,1.15.1179,1.15.1180,1.15.1164,1.15.1076,1.15.1117,1.15.1105,1.15.1075,1.15.1022,1.15.1067,1.15.1053,1.15.1005,1.15.1029,1.15.1013,1.15.1014,1.15.1020,1.15.999,1.15.981,1.15.979,1.15.938,1.15.973,1.15.967,1.15.593,1.15.953,1.15.947,1.15.942,1.15.937,1.15.915,1.15.936,1.15.931,1.15.932,1.15.918,1.15.920,1.15.898,1.15.905,1.15.908,1.15.894,1.15.906,1.15.897,1.15.901,1.15.835,1.15.865,1.15.849,1.15.741,1.15.848,1.15.737,1.15.837,1.15.578,1.15.779,1.15.748,1.15.738,1.15.729,1.15.713,1.15.723,1.15.726,1.15.705,1.15.720,1.15.706,1.15.695,1.15.699,1.15.696,1.15.609,1.15.605,1.15.595,1.15.585,1.15.596,1.15.574,1.15.565,1.15.573,1.15.446,1.15.436";
//        System.out.println(str.split(",").length);
    }

    @Test
    public void testMySql() {
//        transactionService.selectListTime(1, 11265567, -1);
    }

    @Test
    public void fixDataTotal() {
//        List<DailyDataTotalPo> list = dataTotalService.selectList(1526572800000l, 1561651200000l);
//        UserConfigPo configPo = configService.selectOne(1);
//        for (int i = 1; i < list.size(); i++) {
//            DailyDataTotalPo dataTotalPo1 = list.get(i - 1);
//            DailyDataTotalPo dataTotalPo2 = list.get(i);
//            dataTotalPo2.setTotalRoomprtpSettle(StaticFunHandle.addAssetsAmount(configPo.getAssets(),
//                    dataTotalPo1.getTotalRoomprtpSettle(), dataTotalPo2.getTotalRoomprtpSettle()));
//            dataTotalPo2.setTotalRoomprtpAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(),
//                    dataTotalPo1.getTotalRoomprtpAmount(), dataTotalPo2.getTotalRoomprtpAmount()));
//            dataTotalPo2.setTotalAccountBotprtpAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(),
//                    dataTotalPo1.getTotalAccountBotprtpAmount(), dataTotalPo2.getTotalAccountBotprtpAmount()));
//            dataTotalService.updateById(dataTotalPo2);
//        }
    }

    @Transactional
    @Test
    public void testTransactional() {
//        BlockSyncPo syncPo = blockSyncService.selectOneByIdEqThree();
//        System.out.println(syncPo.getSaveTx());
//        syncPo.setSaveTx(2);
//        blockSyncService.updateById(syncPo);
//        syncPo = blockSyncService.selectOneByIdEqThree();
//        System.out.println(syncPo.getSaveTx());
//        syncPo.setSaveTx(1);
//        blockSyncService.updateById(syncPo);
    }

    @Test
    public void testInit() {
//        dataFaucetHandle.initDailyFaucet(null);
//        dataHousesHandle.initDateHouses(null);
//        dataTotalHandle.initDataTotal(null);
//        roomHandle.zeroTotalHandle(null);
    }

    @Test
    public void fixRoomType() {
//        List<DailyRoomDetailsPo> list = dailyRoomService.selectList(1528300800000L, 1562083200000L);
//        for (int i = 0; i < list.size(); i++) {
//            DailyRoomDetailsPo detailsPo = list.get(i);
//            Integer type = clientFunService.getRoomType(detailsPo.getRoom());
//            Integer id = detailsPo.getId();
//            detailsPo = new DailyRoomDetailsPo();
//            detailsPo.setId(id);
//            detailsPo.setType(type);
//            dailyRoomService.updateById(detailsPo);
//        }
//        log.info("fixed size:{}", list.size());
    }

    @Test
    public void testAddAssets() {
//        String str = "1.3.0,1.3.2,1.3.3,1.3.5,1.3.6";
//        String str1 = "[{\"amount\":\"0.00450\",\"asset\":\"1.3.0\"},{\"amount\":\"0.00001\",\"asset\":\"1.3.2\"},{\"amount\":\"0.00000\",\"asset\":\"1.3.3\"},{\"amount\":\"0.00000\",\"asset\":\"1.3.5\"}]";
//        String str2 = "[{\"amount\":\"0.03000\",\"asset\":\"1.3.0\"},{\"amount\":\"1.00000\",\"asset\":\"1.3.2\"},{\"amount\":\"0.00000\",\"asset\":\"1.3.3\"},{\"amount\":\"0.00000\",\"asset\":\"1.3.5\"}]";
//        String str3 = "[{\"count\":\"2\",\"asset\":\"1.3.0\"},{\"count\":\"0\",\"asset\":\"1.3.2\"},{\"count\":\"0\",\"asset\":\"1.3.3\"},{\"count\":\"0\",\"asset\":\"1.3.5\"}]";
//        String str4 = "[{\"count\":\"0\",\"asset\":\"1.3.0\"},{\"count\":\"1\",\"asset\":\"1.3.2\"},{\"count\":\"0\",\"asset\":\"1.3.3\"},{\"count\":\"0\",\"asset\":\"1.3.5\"}]";
//        StaticFunHandle.addAssetsAmount(str, str1, str2);
//        String str5 = "[{\"count\":\"2\",\"house\":\"1.3.0\"},{\"count\":\"1\",\"house\":\"1.3.2\"},{\"count\":\"2\",\"house\":\"1.3.3\"},{\"count\":\"0\",\"house\":\"1.3.5\"}]";
//        String str6 = "[{\"count\":\"2\",\"house\":\"1.3.0\"},{\"count\":\"3\",\"house\":\"1.3.2\"},{\"count\":\"3\",\"house\":\"1.3.3\"},{\"count\":\"0\",\"house\":\"1.3.5\"}]";
//        System.out.println(StaticFunHandle.subAssetsAmount(str, str1, str2));
//        System.out.println(StaticFunHandle.addAssetsCount(str, str3, str4));
//        System.out.println(StaticFunHandle.subAssetsCount(str, str3, str4));
//        System.out.println(StaticFunHandle.addHousesCount(str, str5, str6));
//        System.out.println(StaticFunHandle.subHousesCount(str, str5, str6));
//        System.out.println(StaticFunHandle.updateHousesRate(str, str5, str6));
    }

    @Test
    public void fixTx() {
//        List<TransactionPo> list = transactionService.selectListByNonce();
//        System.out.println(list.size());
//        for (TransactionPo transactionPo : list) {
//            logger.info("{} 区块开始修复", transactionPo.getBlockHeight());
//            transactionService.delete(transactionPo.getId());
//            logger.info("{} 区块删除完成", transactionPo.getBlockHeight());
//            scan.scanBlock(transactionPo.getBlockHeight(), transactionPo.getBlockHeight());
//            logger.info("{} 区块修复完成", transactionPo.getBlockHeight());
//        }
//        logger.info("总共修复的区块数：{}", list.size());
    }

    @Test
    public void initDailyTable() {

    }

    @Test
    public void testSql() {
//        List<BlockSyncPo> list = blockSyncService.selectList();
//        list.forEach(System.out::println);
//        IPage<BlockSyncPo> page = blockSyncService.selectPage();
    }

    @Test
    public void testRpc() {
//        URL url = buildClient();
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        BlockInfo info = client.blockInfo();
//        logger.info("info:\n{}", info.getActiveWitnesses());
    }

    @Test
    public void testBlock() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        Block str = client.getBlock("1593686");
//        JSONObject object = str.getTransactions().get(0).getOperations().get(0).getJSONObject(1);
//        TransferVo transferVo = new TransferVo(object);
//        logger.info("transferVo:\n{}", transferVo.toString());
    }

    @Test
    public void testCreateAccount() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        Block str = client.getBlock("1593662");
//        JSONObject object = str.getTransactions().get(0).getOperations().get(0).getJSONObject(1);
//        String id = str.getTransactions().get(0).getOperationResults().get(0).getString(1);
//        CreateAccountVo createAccountVo = new CreateAccountVo(object, id);
//        logger.info("createAccountVo:\n{}", createAccountVo.toString());
    }

    @Test
    public void testGetAsset() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        GetAsset asset = client.getAsset("1.3.2");
//        logger.info("\ncore_exchange_rate.base.asset:{},amount:{}",
//                asset.getCoreExchangeRateBaseAssetId(), asset.getCoreExchangeRateBaseAmount());
    }

    @Test
    public void testVestingBalances() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        JSONArray object = client.getVestingBalances("gateway");
//        System.out.println(object);
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        HouseByAccount account = client.getHouseByAccount("1.2.14054");
//        System.out.println(account);
    }

    @Test
    public void testGetAccount() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        GetAccount account = client.getAccount("1.2.25");
//        logger.info("\nid:{},name:{}", account.getId(), account.getName());
    }

    @Test
    public void testGetGlobal() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        GetGlobal global = client.getGlobal();
//        logger.info("global-43-fee:{}", global.getParameters().getCurrentFees()
//                .getJSONArray("parameters").getJSONArray(43).getJSONObject(1).getString("fee"));
    }

    @Test
    public void testGetSeerRoom() {
//        SeerJsonRpcClient client = new SeerJsonRpcClient("127.0.0.1", "9992");
//        SeerRoom seerRoom = client.getSeerRoom("1.15.32", 0, 0);
//        logger.info("room-description:{}", seerRoom.getDescription());
//        logger.info("house-id：{}", seerRoom.getHouseId());
//        logger.info("total-play-count：{}", seerRoom.getRunningOption().getTotalPlayerCount());
    }

    private URL buildClient() {
        URL url = null;
        try {
            url = new URL("http://admin:admin@127.0.0.1:9992/");
        } catch (MalformedURLException e) {
            System.out.println("build bitcoin rpc url error!");
            return null;
        }
        return url;
    }

}
