package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class OpenRoomVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger feeAmount;
    private String feeAsset;
    private String issuer;
    private String room;
    private String start;
    private String stop;
    private Integer inputDurationSecs;

    public OpenRoomVo(JSONObject object) {
        this.feeAsset = object.getJSONObject("fee").getString("asset_id");
        this.feeAmount = object.getJSONObject("fee").getBigInteger("amount");
        this.issuer = object.getString("issuer");
        this.room = object.getString("room");
        this.start = object.getString("start");
        this.stop = object.getString("stop");
        this.inputDurationSecs = object.getInteger("input_duration_secs");
    }
}
