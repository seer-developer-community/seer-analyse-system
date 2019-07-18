package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class Opcode30Vo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String feeAssetId;
    private BigInteger feeAmount;
    private BigDecimal feeRealAmount;
    private String vestingBalance;
    private String owner;
    private BigInteger amountAmount;
    private BigDecimal amountRealAmount;
    private String amountAssetId;

    public Opcode30Vo(JSONObject jsonObject) {
        JSONObject fee = jsonObject.getJSONObject("fee");
        this.feeAssetId = fee.getString("asset_id");
        this.feeAmount = fee.getBigInteger("amount");
        this.feeRealAmount = fee.getBigDecimal("amount")
                .divide(new BigDecimal("100000")).setScale(5);
        this.vestingBalance = jsonObject.getString("vesting_balance");
        this.owner = jsonObject.getString("owner");
        JSONObject amount = jsonObject.getJSONObject("amount");
        this.amountAmount = amount.getBigInteger("amount");
        this.amountAssetId = amount.getString("asset_id");
        this.amountRealAmount = amount.getBigDecimal("amount")
                .divide(new BigDecimal("100000")).setScale(5);
    }
}
