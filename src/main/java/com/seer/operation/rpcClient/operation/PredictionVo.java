package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import com.seer.operation.request.FeeBo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
public class PredictionVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger feeAmount;
    private String feeAssetId;
    private String issuer;
    private String room;
    private Integer type;
    private List input;
    private List input1;
    private List input2;
    private BigInteger amount;
    private String deltasAsset;
    private BigDecimal deltasAmount;
    private FeeBo feeBo;

    public PredictionVo(JSONObject jsonObject) {
        this.feeAmount = jsonObject.getJSONObject("fee").getBigInteger("amount");
        this.feeAssetId = jsonObject.getJSONObject("fee").getString("asset_id");
        this.issuer = jsonObject.getString("issuer");
        this.room = jsonObject.getString("room");
        this.type = jsonObject.getInteger("type");
        this.input = jsonObject.getJSONArray("input");
        this.input1 = jsonObject.getJSONArray("input1");
        this.input2 = jsonObject.getJSONArray("input2");
        this.amount = jsonObject.getBigInteger("amount");
    }
}
