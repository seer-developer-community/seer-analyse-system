package com.seer.operation;

import com.seer.operation.entity.DailyProfitPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.DailyProfitHandle;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.service.DailyProfitService;
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

import static com.seer.operation.request.Constants.USER_CONFIG_ID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DailyProfitTests {
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DailyProfitHandle dailyProfitHandle;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private DailyProfitService dailyProfitService;

    @Test
    public void profitInit() {
//        Long begin = 1526572800000L; //2018-05-18
//        Long end = 1562083200000L; //2019-07-03
//        Long day = 86400000L;
//        Long b = System.currentTimeMillis();
//        for (Long i = begin; i <= end; i = i + day) {
//            dailyProfitHandle.initDailyProfit(i);
//            log.info("日期:{} 初始化完成", Times.formatDateByTimes(i));
//        }
//        log.info("初始化完成，耗时:{} ms", (System.currentTimeMillis() - b));
    }

    @Test
    public void fixProfit() throws InterruptedException {
//        List<TransactionPo> list = transactionService.selectList(1, 11711491, -1);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，条数：{}", list.size());
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
//        for (int i = 0; i < list.size(); i++) {//适应于全部操作的线程分配
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1527264000000L) {//5.26 -1
//                list2_3.add(po);
//            } else if (time <= 1529164800000L) {//6.17 -2
//                list2_4.add(po);
//            } else if (time <= 1529596800000L) {//6.22 -3
//                list2_5.add(po);
//            } else if (time <= 1530115200000L) {//6.28 -4
//                list3.add(po);
//            } else if (time <= 1530806400000L) {//7.6 -5
//                list4.add(po);
//            } else if (time <= 1533139200000L) {//8.2 -6
//                list5.add(po);
//            } else if (time <= 1536508800000L) {//9.10 -7
//                list6.add(po);
//            } else if (time <= 1539532800000L) {//10.15 -8
//                list7.add(po);
//            } else if (time <= 1542211200000L) {//11.15 -9
//                list8.add(po);
//            } else if (time <= 1544803200000L) {//12.15 -10
//                list9.add(po);
//            } else if (time <= 1550592000000L) {//2019.2.20 -11
//                list10.add(po);
//            } else if (time <= 1556985600000L) {//5.5 -12
//                list11.add(po);
//            } else {
//                list12.add(po); // -13
//            }
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(13);
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
//        countDownLatch.await();
//        log.info("修复完成，交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - b);
    }

    @Test
    public void fixTotal() {
        //注意selectList不要从大到小排序
//        List<DailyProfitPo> list = dailyProfitService.selectList(1526572800000L, 1562083200000L);
//        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
//        String assets = configPo.getAssets();
//        for (int i = 1; i < list.size(); i++) {
//            DailyProfitPo last = list.get(i - 1);
//            DailyProfitPo dailyProfitPo = list.get(i);
//            //累计dapp收入
//            dailyProfitPo.setTotalDappProfit(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalDappProfit()
//                    , last.getTotalDappProfit()));
//            //累计DAPP水龙头收入
//            dailyProfitPo.setTotalFaucetProfit(dailyProfitPo.getTotalFaucetProfit().add(last.getTotalFaucetProfit()));
//            //累计手续费收入
//            dailyProfitPo.setTotalFeesProfit(dailyProfitPo.getTotalFeesProfit().add(last.getTotalFeesProfit()));
//            //累计dapp补贴
//            dailyProfitPo.setTotalSubsidy(dailyProfitPo.getTotalSubsidy().add(last.getTotalSubsidy()));
//            //累计机器人总体支出
//            dailyProfitPo.setTotalAccountBotprtpAmount(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalAccountBotprtpAmount()
//                    , last.getTotalAccountBotprtpAmount()));
//            //累计DAPP所有房间收入
//            dailyProfitPo.setTotalRoomprtpAmount(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalRoomprtpAmount()
//                    , last.getTotalRoomprtpAmount()));
//            //累计DAPP所有房间支出
//            dailyProfitPo.setTotalRoomprtpSettle(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalRoomprtpSettle()
//                    , last.getTotalRoomprtpSettle()));
//            //累计DAPP注册用户支出
//            dailyProfitPo.setTotalRegisteredFees(dailyProfitPo.getTotalRegisteredFees().add(last.getTotalRegisteredFees()));
//            //累计DAPP新注册用户转账支出
//            dailyProfitPo.setTotalTransferCountFees(StaticFunHandle.addAssetsAmount(assets, dailyProfitPo.getTotalTransferCountFees()
//                    , last.getTotalTransferCountFees()));
//            dailyProfitService.updateById(dailyProfitPo);
//        }
    }
}
