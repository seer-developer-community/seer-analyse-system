package com.seer.operation.rpcClient.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class Transactions implements Serializable {
    private static final long serialVersionUID = 1L;
    //引用的区块号
    private BigInteger refBlockNum;
    //引用的区块头
    private BigInteger refBlockPrefix;
    //交易过期时间
    private String expiration;
    //操作列表
    private List<JSONArray> operations;
    private List extensions;
    //交易签名集合
    private List<String> signatures;
    private List<JSONArray> operationResults;
}
