package com.seer.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.handleService.DailyTypeRoomHandle;
import com.seer.operation.handleService.OperateHandle;
import com.seer.operation.service.DailyTypeRoomService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.threadPool.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DailyTypeRoomTests {
    @Autowired
    private UserConfigService configService;
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private DailyTypeRoomHandle typeRoomHandle;
    @Autowired
    private OperateHandle operateHandle;
    @Autowired
    private DailyTypeRoomService dailyTypeRoomService;

    @Test
    public void initFix() {
//        Long begin = 1526572800000L; //2018-05-18
//        Long end = 1562083200000L; //2019-07-03
//        Long day = 86400000L;
//        Long b = System.currentTimeMillis();
//        for (Long i = begin; i <= end; i = i + day) {
//            typeRoomHandle.initTypeRoom(i);
//            log.info("日期:{} 初始化完成", Times.formatDateByTimes(i));
//        }
//        log.info("初始化完成，耗时:{} ms", (System.currentTimeMillis() - b));
    }

    @Test
    public void fix44() {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 44);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，44的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("44修复完成，耗时：{}", (System.currentTimeMillis() - b));
    }

    @Test
    public void fix45() {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 45);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，45的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("45修复完成，耗时：{}", (System.currentTimeMillis() - b));
    }

    @Test
    public void fix46() {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 46);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，46的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("46修复完成，size:{},耗时：{}", list.size(), (System.currentTimeMillis() - b));
    }

    @Test
    public void fix47() {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 47);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，47的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("47修复完成，size:{},耗时：{}", list.size(), (System.currentTimeMillis() - b));
    }

    @Test
    public void fix48() {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 48);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，48的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("48修复完成，size:{},耗时：{}", list.size(), (System.currentTimeMillis() - b));
    }

    @Test
    public void fix49() {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 49);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，49的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("49修复完成，size:{},耗时：{}", list.size(), (System.currentTimeMillis() - b));
    }

    @Test
    public void fix50() throws InterruptedException {
//        List<TransactionPo> list = transactionService.selectList(1, 11711491, 50);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，50的条数：{}", list.size());
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
//            if (time <= 1529337600000L) {//6.19
//                list2_3.add(po);
//            } else if (time <= 1529856000000L) {//6.25
//                list2_4.add(po);
//            } else if (time <= 1530374400000L) {//7.1
//                list2_5.add(po);
//            } else if (time <= 1531843200000L) {//7.18
//                list3.add(po);
//            } else if (time <= 1534521600000L) {//8.18
//                list4.add(po);
//            } else if (time <= 1537200000000L) {//9.18
//                list5.add(po);
//            } else if (time <= 1538668800000L) {//10.05
//                list6.add(po);
//            } else if (time <= 1539532800000L) {//10.15
//                list7.add(po);
//            } else if (time <= 1540828800000L) {//10.30
//                list8.add(po);
//            } else if (time <= 1542211200000L) {//11.15
//                list9.add(po);
//            } else if (time <= 1546617600000L) {//2019.1.05
//                list10.add(po);
//            } else if (time <= 1550592000000L) {//2.20
//                list11.add(po);
//            } else if (time <= 1553011200000L) {//3.20
//                list12.add(po);
//            } else {
//                list13.add(po);
//            }
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(14);
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
//        log.info("50修复完成，交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - b);
    }

    @Test
    public void fixTotal() {
        //注意selectList 不要倒序
//        List<DailyTypeRoomPo> list = dailyTypeRoomService.selectList(1526572800000L, 1562860800000L);
//        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
//        String assets = configPo.getAssets();
//        for (int i = 1; i < list.size(); i++) {
//            DailyTypeRoomPo po1 = list.get(i - 1);
//            DailyTypeRoomPo po2 = list.get(i);
//            //累计PVP房间手续费
//            po2.setTotalPvpFees(po2.getTotalPvpFees().add(po1.getTotalPvpFees()));
//            //累计PVD房间手续费
//            po2.setTotalPvdFees(po2.getTotalPvdFees().add(po1.getTotalPvdFees()));
//            //累计高级房间手续费
//            po2.setTotalAdvFees(po2.getTotalAdvFees().add(po1.getTotalAdvFees()));
//            //累计PVP派奖总额
//            po2.setTotalPvpSettle(StaticFunHandle.addAssetsAmount(assets, po2.getTotalPvpSettle(),
//                    po1.getTotalPvpSettle()));
//            //累计PVD派奖总额
//            po2.setTotalPvdSettle(StaticFunHandle.addAssetsAmount(assets, po2.getTotalPvdSettle(),
//                    po1.getTotalPvdSettle()));
//            //累计高级派奖总额
//            po2.setTotalAdvSettle(StaticFunHandle.addAssetsAmount(assets, po2.getTotalAdvSettle(),
//                    po1.getTotalAdvSettle()));
//            //累计PVD投注总额
//            po2.setTotalPvdPlayAmount(StaticFunHandle.addAssetsAmount(assets, po2.getTotalPvdPlayAmount(),
//                    po1.getTotalPvdPlayAmount()));
//            //累计PVP投注总额
//            po2.setTotalPvpPlayAmount(StaticFunHandle.addAssetsAmount(assets, po2.getTotalPvpPlayAmount(),
//                    po1.getTotalPvpPlayAmount()));
//            //累计高级投注总额
//            po2.setTotalAdvPlayAmount(StaticFunHandle.addAssetsAmount(assets, po2.getTotalAdvPlayAmount(),
//                    po1.getTotalAdvPlayAmount()));
//            //累计PVD房间手续费收入
//            po2.setTotalPvdPrtpFees(po2.getTotalPvdPrtpFees().add(po1.getTotalPvdPrtpFees()));
//            //累计PVP房间手续费收入
//            po2.setTotalPvpPrtpFees(po2.getTotalPvpPrtpFees().add(po1.getTotalPvpPrtpFees()));
//            //累计高级房间手续费收入
//            po2.setTotalAdvPrtpFees(po2.getTotalAdvPrtpFees().add(po1.getTotalAdvPrtpFees()));
//            //累计pvp抽成收入
//            po2.setTotalPvpProfit(StaticFunHandle.addAssetsAmount(assets, po2.getTotalPvpProfit(),
//                    po1.getTotalPvpProfit()));
//            dailyTypeRoomService.updateById(po2);
//        }
    }
}
