package com.seer.operation.handleService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.service.BlockSyncService;
import com.seer.operation.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FixDataService {
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

    public void dealTotalRoom(List<TransactionPo> list) {
        for (int i = 0; i < list.size(); i++) {
            TransactionPo po = list.get(i);
            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
        }
    }

    public void dealTotal(List<TransactionPo> list) {
        for (int i = 0; i < list.size(); i++) {
            TransactionPo po = list.get(i);
            //处理daily_data_faucet
            dailyDataFaucetHandle.initDailyFaucet(po.getBlockTime());
            //初始化daily_data_houses
            dailyDataHousesHandle.initDateHouses(po.getBlockTime());
            //初始化daily_data_total
            dailyDataTotalHandle.initDataTotal(po.getBlockTime());
            if (i == 500 || i == 1000 || i == 1500 || i == 2000) {
                System.out.println(i);
            }
        }
    }

    public void dealHandle(List<TransactionPo> list) {
        for (int i = 0; i < list.size(); i++) {
            TransactionPo po = list.get(i);
            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
            log.info("块高:{}", po.getBlockHeight());
        }
    }
}
