package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class RoomRunningOptionVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer roomType;
    private List selectionDescription;
    private Long range;
    private List participators;
    private Long totalShares;
    private Long settledBalance;
    private Long settledRow;
    private List playerCount;
    private Long totalPlayerCount;
    private JSONObject pvpRunning;
    private BigInteger guarantyAlone;
    private Long pvpOwnerPercent;
    private Long pvpOwnerShares;
}
