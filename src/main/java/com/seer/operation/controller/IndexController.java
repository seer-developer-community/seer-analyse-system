package com.seer.operation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.BlockSyncPo;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.rpcClient.SeerJsonRpcClient;
import com.seer.operation.rpcClient.response.BlockInfo;
import com.seer.operation.service.BlockSyncService;
import com.seer.operation.service.DailyAccountService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.utils.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "index")
public class IndexController {
    @Autowired
    private BlockSyncService blockSyncService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private DailyAccountService dailyAccountService;
    @Value("${seer.rpc.ip}")
    private String rpcIp;
    @Value("${seer.rpc.port}")
    private String rpcPort;

    private final Long LAST_SEVEN_ZERO_TIMES = 1000 * 60 * 60 * 24 * 7L;
    private final Long DAY_ZERO_TIMES = 1000 * 60 * 60 * 24L;

    @RequestMapping(value = "info")
    public ResponseVo getIndex() {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        BlockSyncPo syncPo = blockSyncService.selectOneByIdEqOne();
        if (null == syncPo) {
            return ResponseVo.ResultFailed("获取信息失败");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", syncPo.getBlockSync());
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        BlockInfo blockInfo = client.blockInfo();
        if (null == blockInfo) {
            return ResponseVo.ResultFailed("无法获取最新块高");
        }
        jsonObject.put("last", blockInfo.getHeadBlockNum());
        BigDecimal point = new BigDecimal(syncPo.getBlockSync()).divide(new BigDecimal(blockInfo.getHeadBlockNum()),
                6, BigDecimal.ROUND_HALF_DOWN);
        point = point.multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_FLOOR);
        jsonObject.put("point", point);
        jsonObject.put("txs", analyseTxs());
        jsonObject.put("actives", analyseActives());
        responseVo.setData(jsonObject);
        return responseVo;
    }

    @RequestMapping(value = "sync")
    public ResponseVo getSync() {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        BlockSyncPo syncPo = blockSyncService.selectOneByIdEqOne();
        responseVo.setData(syncPo);
        return responseVo;
    }

    @RequestMapping(value = "update/sync")
    public ResponseVo updateSync(Integer saveBlock, Integer saveTx, Integer syncStatus) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        BlockSyncPo syncPo = new BlockSyncPo();
        syncPo.setId(1);
        if (null != saveBlock) {
            syncPo.setSaveBlock(saveBlock);
        }
        if (null != saveTx) {
            syncPo.setSaveTx(saveTx);
        }
        if (null != syncStatus) {
            syncPo.setStatus(syncStatus);
        }
        try {
            blockSyncService.updateById(syncPo);
        } catch (Exception e) {
            responseVo = ResponseVo.ResultFailed("更新失败");
        }
        syncPo = blockSyncService.selectOneByIdEqOne();
        responseVo.setData(syncPo);
        return responseVo;
    }

    public JSONArray analyseTxs() {
        JSONArray array = new JSONArray();
        for (int i = 6; i >= 0; i--) {
            JSONObject object = new JSONObject();
            Long begin = Times.getTimesDayZero(null) - (DAY_ZERO_TIMES * i);
            object.put("name", Times.formatDateByTimes(begin));
            Long end = Times.getTimesDayZero(null) + DAY_ZERO_TIMES;
            if (i != 0) {
                end = Times.getTimesDayZero(null) - (DAY_ZERO_TIMES * (i - 1));
            }
            Integer count = transactionService.countByDay(begin, end - 1);
            object.put("value", count);
            array.add(object);
        }
        return array;
    }

    public JSONArray analyseActives() {
        JSONArray array = new JSONArray();
        for (int i = 6; i >= 0; i--) {
            JSONObject object = new JSONObject();
            Long begin = Times.getTimesDayZero(null) - (DAY_ZERO_TIMES * i);
            object.put("name", Times.formatDateByTimes(begin));
            Long end = Times.getTimesDayZero(null) + DAY_ZERO_TIMES - 1;
            if (i != 0) {
                end = Times.getTimesDayZero(null) - (DAY_ZERO_TIMES * (i - 1)) - 1;
            }
            Integer count = dailyAccountService.countByDay(begin, end);
            object.put("value", count);
            array.add(object);
        }
        return array;
    }
}
