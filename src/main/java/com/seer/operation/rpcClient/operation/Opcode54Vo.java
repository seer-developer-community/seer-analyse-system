package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class Opcode54Vo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String feeAssetId;
    private BigInteger feeAmount;
    private BigDecimal feeRealAmount;
    private String issuer;
    private String house;
    private BigInteger guaranty;
    private BigInteger claimFees;
    private BigDecimal realClaimFess;
    private String description;
    private String script;

    public Opcode54Vo(JSONObject jsonObject) {
        JSONObject fee = jsonObject.getJSONObject("fee");
        this.feeAssetId = fee.getString("asset_id");
        this.feeAmount = fee.getBigInteger("amount");
        this.feeRealAmount = fee.getBigDecimal("amount")
                .divide(new BigDecimal("100000")).setScale(5);
        this.issuer = jsonObject.getString("issuer");
        this.house = jsonObject.getString("house");
        this.guaranty = jsonObject.getBigInteger("guaranty");
        this.claimFees = jsonObject.getBigInteger("claim_fees");
        this.realClaimFess = jsonObject.getBigDecimal("claim_fees")
                .divide(new BigDecimal("100000")).setScale(5);
        this.description = jsonObject.getString("description");
        this.script = jsonObject.getString("script");
    }
}
