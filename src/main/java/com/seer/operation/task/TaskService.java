package com.seer.operation.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.BlockSyncPo;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.handleService.*;
import com.seer.operation.request.STATUS;
import com.seer.operation.rpcClient.SeerJsonRpcClient;
import com.seer.operation.rpcClient.response.BlockInfo;
import com.seer.operation.service.BlockSyncService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.threadPool.AsyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.seer.operation.request.Constants.USER_CONFIG_ID;

@Component
public class TaskService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${seer.rpc.ip}")
    private String rpcIp;
    @Value("${seer.rpc.port}")
    private String rpcPort;
    @Resource
    private AsyncTaskService asyncTaskService;
    @Autowired
    private Scan scan;
    @Autowired
    private BlockSyncService blockSyncService;
    @Autowired
    private RoomHandle roomHandle;
    @Autowired
    private DailyDataFaucetHandle dailyDataFaucetHandle;
    @Autowired
    private DailyDataHousesHandle dailyDataHousesHandle;
    @Autowired
    private DailyDataTotalHandle dailyDataTotalHandle;
    @Autowired
    private OperateHandle operateHandle;
    @Autowired
    private TransactionService transactionService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Autowired
    private DailyTypeRoomHandle dailyTypeRoomHandle;
    @Autowired
    private DailyHouseRoomHandle dailyHouseRoomHandle;
    @Autowired
    private DailyProfitHandle dailyProfitHandle;

    @Scheduled(fixedDelay = 3600000)
    public void syncScan() throws InterruptedException {
        Integer clientNum = 1;
        Long bT = System.currentTimeMillis();
        BlockSyncPo syncPo = blockSyncService.selectOneByIdEqTwo();
        if (null == syncPo || syncPo.getStatus() == STATUS.PAUSED.getCode()) {
            return;
        }
        //获取最新块高
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        BlockInfo blockInfo = client.blockInfo();
        if (null == blockInfo) {
            logger.error("syncScan:获取最新块高异常");
            return;
        }
        BigInteger begin = BigInteger.valueOf(syncPo.getBlockSync());
        BigInteger end = blockInfo.getHeadBlockNum();
        if (end.compareTo(begin) <= 0) {
            return;
        }
        int blocks = end.subtract(begin).intValue();
        //如果超过5万个块 则启用40个线程
        if (blocks >= 50000) {
            clientNum = 20;
        } else if (blocks >= 10000) {
            clientNum = 10;
        } else if (blocks >= 1000) {
            clientNum = 10;
        } else if (blocks >= 200) {
            clientNum = 4;
        }
        int value = blocks / clientNum;
        CountDownLatch countDownLatch = new CountDownLatch(clientNum);
        logger.info("启用 {} 个线程，需要扫的块数: {}，平均每个线程分配的数量：{}", clientNum, blocks, value);
        for (int i = 0; i < clientNum; i++) {
            int b = i * value + begin.intValue() + 1;
            int e = (i + 1) * value + begin.intValue();
            if (i == clientNum - 1) {
                asyncTaskService.executeAsyncTask(countDownLatch, BigInteger.valueOf(b), end);
            } else {
                asyncTaskService.executeAsyncTask(countDownLatch, BigInteger.valueOf(b), BigInteger.valueOf(e));
            }
        }
        countDownLatch.await();
        logger.info("起始:{},最新快:{} 全部扫描完成，耗时：{} ms", begin.add(BigInteger.ONE), end, System.currentTimeMillis() - bT);
        syncPo.setBlockSync(end.intValue());
        blockSyncService.updateById(syncPo);
    }

    /**
     * 扫描区块
     */
    @Transactional
    @Scheduled(fixedDelay = 50)
    public void scanBlock() {
        Long begin = System.currentTimeMillis();
        BlockSyncPo blockSyncPo = blockSyncService.selectOneByIdEqOne();
        if (null == blockSyncPo) {
            throw new IllegalArgumentException("获取同步块高异常，请检查是否存在id=1的记录！");
        }
        if (blockSyncPo.getStatus() == STATUS.PAUSED.getCode()) {
            return;
        }
        boolean aBoolean = scan.scanFromBlockChain(blockSyncPo.getBlockSync() + 1, blockSyncPo.getSaveBlock(), blockSyncPo.getSaveTx());
        if (aBoolean) {
            logger.info("区块：{} 扫描完成，耗时：{} ms", blockSyncPo.getBlockSync() + 1, System.currentTimeMillis() - begin);
        }
    }

    /**
     * 每天0点执行，
     * 处理房间系列
     *
     * @throws InterruptedException
     */
    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void zeroUpdateTotalRoom() throws InterruptedException {
        logger.info("begin zero-time-task ..");
        Long t = System.currentTimeMillis();
        BlockSyncPo blockSyncPo = blockSyncService.selectOneByIdEqOne();
        if (null == blockSyncPo) {
            return;
        }
        if (blockSyncPo.getStatus() == STATUS.PAUSED.getCode()) {
            logger.info("配置项状态为：暂停扫描");
            return;
        }
        blockSyncPo.setStatus(STATUS.PAUSED.getCode());
        blockSyncService.updateById(blockSyncPo);
        Thread.sleep(5000L); //休息5s后保证没有定时任务在扫块
        //处理房间系列
        roomHandle.zeroTotalHandle(null);
        //处理daily_data_faucet
        dailyDataFaucetHandle.initDailyFaucet(null);
        //初始化daily_data_houses
//        dailyDataHousesHandle.initDateHouses(null);
        //初始化daily_data_total
//        dailyDataTotalHandle.initDataTotal(null);
        dailyHouseRoomHandle.initHouseRoom(null);
        dailyProfitHandle.initDailyProfit(null);
        dailyTypeRoomHandle.initTypeRoom(null);
        //重新开启任务扫区块
        blockSyncPo = new BlockSyncPo();
        blockSyncPo.setId(USER_CONFIG_ID);
        blockSyncPo.setStatus(STATUS.NORMAL.getCode());
        blockSyncService.updateById(blockSyncPo);
        logger.info("zero-time-task finished..used time:{} ms", (System.currentTimeMillis() - t));
    }

    /**
     * 从数据库中扫描交易操作
     */
    @Scheduled(fixedDelay = 10000)
    public void scanFromSql() {
        BlockSyncPo syncPo = blockSyncService.selectOneByIdEqThree();
        if (syncPo.getStatus() == STATUS.PAUSED.getCode()) {
            return;
        }
        TransactionPo lastPo = transactionService.selectLastOne();
        if (lastPo.getBlockHeight().compareTo(BigInteger.valueOf(syncPo.getBlockSync())) <= 0) {
            logger.info("scanFromSql:没有交易需要统计！");
            return;
        }
        IPage<TransactionPo> page = transactionService.selectScanPage(1, syncPo.getBlockSync() + 1, lastPo.getBlockHeight().intValue());
        Long begin = System.currentTimeMillis();
        logger.info("开始统计历史记录，此次共有 {} 笔交易操作", page.getTotal());
        for (int i = 1; i <= 1; i++) {
            Long pageBegin = System.currentTimeMillis();
            page = transactionService.selectScanPage(i, syncPo.getBlockSync() + 1, lastPo.getBlockHeight().intValue());
            List<TransactionPo> list = page.getRecords();
            for (int j = 0; j < list.size(); j++) {
                TransactionPo po = list.get(j);
                transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus transactionStatus) {
                        Long times = po.getBlockTime();
                        //处理房间系列
                        Long t = System.currentTimeMillis();
                        roomHandle.zeroTotalHandle(times);
//                        logger.info("zeroRoom:{} ms", (System.currentTimeMillis() - t));
                        //处理daily_data_faucet
                        t = System.currentTimeMillis();
                        dailyDataFaucetHandle.initDailyFaucet(times);
//                        logger.info("initFaucet:{} ms", (System.currentTimeMillis() - t));
                        //初始化daily_data_houses
                        t = System.currentTimeMillis();
//                        dailyDataHousesHandle.initDateHouses(times);
//                        logger.info("initHouses:{} ms", (System.currentTimeMillis() - t));
                        //初始化daily_data_total
                        t = System.currentTimeMillis();
//                        dailyDataTotalHandle.initDataTotal(times);
//                        logger.info("initTotal:{} ms", (System.currentTimeMillis() - t));
                        dailyHouseRoomHandle.initHouseRoom(times);
                        dailyProfitHandle.initDailyProfit(times);
                        dailyTypeRoomHandle.initTypeRoom(times);
                        //对操作进行分析
                        t = System.currentTimeMillis();
                        logger.info("块高:{},id:{}", po.getBlockHeight(), po.getId());
                        JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
                        operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
                        BigInteger block = po.getBlockHeight();
//                        logger.info("operateHandle:{} ms", (System.currentTimeMillis() - t));
                        BlockSyncPo syncPo1 = new BlockSyncPo();
                        syncPo1.setId(3);
                        syncPo1.setBlockSync(block.intValue());
                        blockSyncService.updateById(syncPo1);
                        return null;
                    }
                });
            }
            logger.info("page:{} 的交易统计完成，耗时：{} ms", i, (System.currentTimeMillis() - pageBegin));
        }
//        logger.info("总计 {} 条交易统计完成，耗时：{} ms", page.getTotal(), (System.currentTimeMillis() - begin));
    }
}
