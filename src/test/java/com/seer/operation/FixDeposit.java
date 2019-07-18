package com.seer.operation;

import com.seer.operation.entity.DailyDataFaucetPo;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.service.DailyDataFaucetService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.threadPool.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FixDeposit {
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private DailyDataFaucetService dataFaucetService;
    @Autowired
    private UserConfigService configService;

    @Test
    public void fixDeposit() throws InterruptedException {
//        List<TransactionPo> list = transactionService.selectList(1, 11942793, 0);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，0的条数：{}", list.size());
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
//        for (int i = 0; i < list.size(); i++) {//适应于0操作的线程分配
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1528732800000L) {//6.12 -1
//                list2_3.add(po);
//            } else if (time <= 1529337600000L) {//6.19 -2
//                list2_4.add(po);
//            } else if (time <= 1529596800000L) {//6.22 -3
//                list2_5.add(po);
//            } else if (time <= 1529856000000L) {//6.25 -4
//                list3.add(po);
//            } else if (time <= 1530201600000L) {//6.29 -5
//                list4.add(po);
//            } else if (time <= 1530547200000L) {//7.3 -6
//                list5.add(po);
//            } else if (time <= 1530806400000L) {//7.6 -7
//                list6.add(po);
//            } else if (time <= 1531152000000L) {//7.10 -8
//                list7.add(po);
//            } else if (time <= 1536076800000L) {//9.5 -9
//                list8.add(po);
//            } else if (time <= 1547481600000L) {//2019.1.15 -10
//                list9.add(po);
//            } else if (time <= 1551715200000L) {//2019.3.05 -11
//                list10.add(po);
//            } else if (time <= 1556294400000L) {//4.27 -12
//                list11.add(po);
//            } else if (time <= 1561392000000L) {//6.25 -13
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
//        log.info("0修复完成，交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - b);
    }

    @Test
    public void fixFaucetTotalData() {
        //注意selectList不要desc
//        List<DailyDataFaucetPo> list = dataFaucetService.selectList(1526572800000L,1562860800000L);
//        UserConfigPo configPo = configService.selectOne(1);
//        for (int i = 1; i < list.size(); i++) {
//            DailyDataFaucetPo faucetPo1 = list.get(i - 1);
//            DailyDataFaucetPo faucetPo2 = list.get(i);
//            faucetPo2.setTotalDepositCount(StaticFunHandle.addAssetsCount(configPo.getAssets(), faucetPo2.getTotalDepositCount(), faucetPo1.getTotalDepositCount()));
//            faucetPo2.setTotalDepositAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), faucetPo2.getTotalDepositAmount(), faucetPo1.getTotalDepositAmount()));
//            dataFaucetService.updateById(faucetPo2);
//        }
    }
}
