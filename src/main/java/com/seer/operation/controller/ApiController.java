package com.seer.operation.controller;

import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "api")
public class ApiController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "time")
    public String sysTime() {
        return new Date().toString();
    }

    @RequestMapping(value = "records")
    public String records(Integer limit, String codeNum) {
        if (null == limit) {
            limit = 10;
        }
        List<TransactionPo> list = transactionService.selectRecords(limit, codeNum);
        JSONObject object = new JSONObject();
        object.put("code", 200);
        object.put("id", 1);
        object.put("jsonrpc", "2.0");
        object.put("result", list);
        return object.toJSONString();
    }
}
