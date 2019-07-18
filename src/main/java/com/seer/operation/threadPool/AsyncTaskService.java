package com.seer.operation.threadPool;

import com.seer.operation.entity.TransactionPo;
import com.seer.operation.handleService.FixDataService;
import com.seer.operation.handleService.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class AsyncTaskService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static Integer value = 0;
    @Autowired
    private Scan scan;
    @Autowired
    private FixDataService fixDataService;

    @Async
    public void executeAsyncTask(CountDownLatch countDownLatch, BigInteger begin, BigInteger end) throws InterruptedException {
        logger.info("Thread {} starting", Thread.currentThread().getName());
        scan.scanBlock(begin, end);
        countDownLatch.countDown();
    }

    @Async
    public void fixList(CountDownLatch countDownLatch, List<TransactionPo> list, int type) {
        logger.info("线程:{} 分配的笔数:{}", Thread.currentThread().getName(), list.size());
        if (type == 46) {
            fixDataService.dealTotalRoom(list);
            countDownLatch.countDown();
        } else if (type == -1) {
            fixDataService.dealTotal(list);
            countDownLatch.countDown();
        } else if (type == 4) {
            fixDataService.dealHandle(list);
            countDownLatch.countDown();
        } else if (type == 99) {
            fixDataService.dealHandle(list);
            countDownLatch.countDown();
            logger.info("线程:{} 扫描完成，笔数:{}", Thread.currentThread().getName(), list.size());
        }
    }
}
