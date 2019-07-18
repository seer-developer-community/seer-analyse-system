package com.seer.operation;

import com.alibaba.fastjson.JSON;
import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.handleService.OperateHandle;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.service.DailyRoomService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.threadPool.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static com.seer.operation.request.Constants.SEER_ASSET;
import static com.seer.operation.utils.Times.getTimesDayZero;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FixRoomTests {
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OperateHandle operateHandle;
    @Autowired
    private DailyRoomService dailyRoomService;

    @Test
    public void fixRoom44() {
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 44);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，44的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("44修复完成，size:{},耗时：{}", list.size(), (System.currentTimeMillis() - b));
    }

    @Test
    public void fixRoom45() {
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 45);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，45的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("45修复完成，size:{},耗时：{}", list.size(), (System.currentTimeMillis() - b));
    }

    @Test
    public void fixRoom47() {
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 47);
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
    public void fixRoom48() {
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 48);
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
    public void fixRoom49() {
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 49);
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
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 50);
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
//            if (time <= 1529337600000L) {//6.19 -1
//                list2_3.add(po);
//            } else if (time <= 1529856000000L) {//6.25 -2
//                list2_4.add(po);
//            } else if (time <= 1530374400000L) {//7.1 -3
//                list2_5.add(po);
//            } else if (time <= 1531843200000L) {//7.18 -4
//                list3.add(po);
//            } else if (time <= 1534521600000L) {//8.18 -5
//                list4.add(po);
//            } else if (time <= 1537200000000L) {//9.18 -6
//                list5.add(po);
//            } else if (time <= 1538668800000L) {//10.05 -7
//                list6.add(po);
//            } else if (time <= 1539532800000L) {//10.15 -8
//                list7.add(po);
//            } else if (time <= 1540828800000L) {//10.30 -9
//                list8.add(po);
//            } else if (time <= 1542211200000L) {//11.15 -10
//                list9.add(po);
//            } else if (time <= 1546617600000L) {//2019.1.05 -11
//                list10.add(po);
//            } else if (time <= 1554998400000L) {//4.20 -12
//                list11.add(po);
//            } else if (time <= 1560268800000L) {//6.20 -13
//                list12.add(po);
//            } else {
//                list13.add(po); //-14
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
    public void fixDailyDapp() {
//        List<DailyRoomDetailsPo> list = dailyRoomService.selectList(1528300800000L, 1562947200000L, null);
//        for (DailyRoomDetailsPo detailsPo : list) {
//            detailsPo.setDappTotalAmount(StaticFunHandle.subAssetAmount(SEER_ASSET, detailsPo.getDappTotalAmount(),
//                    new BigDecimal(300)));
//            dailyRoomService.updateById(detailsPo);
//        }
//        List<TransactionPo> list = transactionService.selectList(1, 11974026, 46);
//        for (TransactionPo transactionPo : list) {
//            String room = JSON.parseArray(transactionPo.getOperations()).getJSONObject(1).getString("room");
//            Long zero = getTimesDayZero(transactionPo.getBlockTime());
//            DailyRoomDetailsPo detailsPo = dailyRoomService.selectOneByRoomToday(room, zero);
//            String assets = "1.3.0,1.3.2,1.3.5";
//            detailsPo.setDappTotalAmount(StaticFunHandle.addAssetAmount(SEER_ASSET, assets, detailsPo.getDappTotalAmount(),
//                    new BigDecimal(300)).toJSONString());
//            dailyRoomService.updateById(detailsPo);
//        }
    }
}
