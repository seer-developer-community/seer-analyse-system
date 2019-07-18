package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
public class TransferVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger feeAmount;
    private BigDecimal feeRealAmount;
    private String feeAssetId;
    private String from;
    private String to;
    private BigInteger amountAmount;
    private BigDecimal amountRealAmount;
    private String amountAssetId;
    private List extensions;

    public TransferVo(JSONObject object) {
        this.amountAmount = object.getJSONObject("amount").getBigInteger("amount");
        this.amountAssetId = object.getJSONObject("amount").getString("asset_id");
        this.amountRealAmount = new BigDecimal(this.amountAmount).divide(new BigDecimal("100000")).setScale(5);
        this.extensions = object.getJSONArray("extensions");
        this.from = object.getString("from");
        this.to = object.getString("to");
        this.feeAmount = object.getJSONObject("fee").getBigInteger("amount");
        this.feeAssetId = object.getJSONObject("fee").getString("asset_id");
        this.feeRealAmount = new BigDecimal(this.feeAmount).divide(new BigDecimal("100000")).setScale(5);
    }
}
