package com.seer.operation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.BlockPo;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.BlockService;
import com.seer.operation.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "seer/block")
public class BlockController {
    @Autowired
    private BlockService blockService;
    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "page")
    public ResponseVo selectBlockPage(Integer current, Integer size, String block) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<BlockPo> page = blockService.selectPage(current, size, block);
        responseVo.setData(page);
        return responseVo;
    }

    @RequestMapping(value = "tx/page")
    public ResponseVo selectTxPage(Integer current, Integer size, String block, String tx) {
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        IPage<TransactionPo> page = transactionService.selectPage(current, size, block, tx);
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        //重构返回对象
        JSONArray newList = new JSONArray();
        List<TransactionPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            TransactionPo transactionPo = list.get(i);
            jsonObject.put("blockHeight",transactionPo.getBlockHeight());
            jsonObject.put("txId",transactionPo.getTxId());
            jsonObject.put("nonce",transactionPo.getNonce());
            jsonObject.put("createTime",transactionPo.getCreateTime());
            jsonObject.put("operationResults", transactionPo.getOperationResults());
            jsonObject.put("operations",JSON.parseArray(transactionPo.getOperations()));
            jsonObject.put("blockTime",transactionPo.getBlockTime());
            jsonObject.put("type",transactionPo.getType());
            newList.add(jsonObject);
        }
        object.put("records", newList);
        responseVo.setData(object);
        return responseVo;
    }
}
