package com.seer.operation.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class FeeBo {
    private String assetId;
    private BigInteger amount;
    private BigDecimal realAmount;
    private Boolean isSeer;//是否为1.3.0
    private BigDecimal otherAssetBaseSeer;//其他资产按市价兑换成seer后的seer数量

    public FeeBo() {

    }

    public FeeBo(JSONObject jsonObject, Boolean isSeer, BigDecimal other) {
        this.assetId = jsonObject.getString("asset_id");
        this.amount = jsonObject.getBigInteger("amount");
        this.realAmount = new BigDecimal(amount).divide(new BigDecimal("100000"))
                .setScale(5, BigDecimal.ROUND_HALF_DOWN);
        this.isSeer = isSeer;
        this.otherAssetBaseSeer = other;
    }
}
