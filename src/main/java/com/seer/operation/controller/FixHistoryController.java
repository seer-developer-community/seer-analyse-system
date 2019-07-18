package com.seer.operation.controller;

import com.seer.operation.entity.TransactionPo;
import com.seer.operation.handleService.*;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.BlockSyncService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.threadPool.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RestController
@RequestMapping(value = "seer/fix")
public class FixHistoryController {
    @Autowired
    private AsyncTaskService asyncTaskService;
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

    @RequestMapping(value = "dailyTotal")
    public void fixDailyRoom(int begin, int end) throws InterruptedException {
        List<TransactionPo> list = transactionService.selectListTime(begin, end, -1);
        Long bT = System.currentTimeMillis();
        int blocks = list.size();
        int clientNum = getClientNum(blocks);
        int value = blocks / clientNum;
        CountDownLatch countDownLatch = new CountDownLatch(clientNum);
        log.info("启用 {} 个线程，需要扫的块数: {}，平均每个线程分配的数量：{}", clientNum, blocks, value);
        for (int i = 0; i < clientNum; i++) {
            List<TransactionPo> transactionPos = new LinkedList<>();
            int e = (i + 1) * value;
            if (i == clientNum - 1) {
                e = blocks;
            }
            for (int j = i * value; j < e; j++) {
                transactionPos.add(list.get(j));
            }
            asyncTaskService.fixList(countDownLatch, transactionPos, -1);
        }
        countDownLatch.await();
        log.info("起始:{},终止:{},交易笔数:{}，耗时：{} ms", begin, end, list.size(), System.currentTimeMillis() - bT);
    }

    @RequestMapping(value = "totalRoom")
    public ResponseVo fixTotalRoom(int begin, int end) throws InterruptedException {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        Long bT = System.currentTimeMillis();
        //开启房间时会插入每日房间和累计房间
        List<TransactionPo> list = transactionService.selectList(begin, end, 46);
        int blocks = list.size();
        int clientNum = getClientNum(blocks);
        int value = blocks / clientNum;
        CountDownLatch countDownLatch = new CountDownLatch(clientNum);
        log.info("启用 {} 个线程，需要扫的块数: {}，平均每个线程分配的数量：{}", clientNum, blocks, value);
        for (int i = 0; i < clientNum; i++) {
            List<TransactionPo> transactionPos = new LinkedList<>();
            int e = (i + 1) * value;
            if (i == clientNum - 1) {
                e = blocks;
            }
            for (int j = i * value; j < e; j++) {
                transactionPos.add(list.get(j));
            }
            asyncTaskService.fixList(countDownLatch, transactionPos, 46);
        }
        countDownLatch.await();
        log.info("起始:{},终止:{},交易笔数:{}，耗时：{} ms", begin, end, list.size(), System.currentTimeMillis() - bT);
        responseVo.setData("交易笔数:" + list.size() + ",耗时:" + (System.currentTimeMillis() - bT) + " 毫秒");
        return responseVo;
    }

    @RequestMapping(value = "user")
    public ResponseVo fixUser(int begin, int end) throws InterruptedException {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        Long bT = System.currentTimeMillis();
        List<TransactionPo> list = transactionService.selectList(begin, end, 4);
        int blocks = list.size();
        int clientNum = getClientNum(blocks);
        int value = blocks / clientNum;
        CountDownLatch countDownLatch = new CountDownLatch(clientNum);
        log.info("启用 {} 个线程，需要扫的块数: {}，平均每个线程分配的数量：{}", clientNum, blocks, value);
        for (int i = 0; i < clientNum; i++) {
            List<TransactionPo> transactionPos = new LinkedList<>();
            int e = (i + 1) * value;
            if (i == clientNum - 1) {
                e = blocks;
            }
            for (int j = i * value; j < e; j++) {
                transactionPos.add(list.get(j));
            }
            asyncTaskService.fixList(countDownLatch, transactionPos, 4);
        }
        countDownLatch.await();
        log.info("起始:{},终止:{},交易笔数:{}，耗时：{} ms", begin, end, list.size(), System.currentTimeMillis() - bT);
        responseVo.setData("交易笔数:" + list.size() + ",耗时:" + (System.currentTimeMillis() - bT) + " 毫秒");
        return responseVo;
    }

    private int getClientNum(int blocks) {
        int clientNum = 1;
        if (blocks >= 1000) {
            clientNum = 20;
        } else if (blocks >= 100) {
            clientNum = 10;
        }
        return clientNum;
    }
}
