package com.seer.operation.rpcClient.response;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.seer.operation.rpcClient.operation.RoomRunningOptionVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SeerRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String houseId;
    private String owner;
    private List label;
    private String description;
    private String script;
    private Integer roomType;
    private String status;
    private JSONPObject option;
    private Integer ownerPayFeePercent;
    private RoomRunningOptionVo runningOption;
    private List ownerResult;
    private List finalResult;
    private List committeeResult;
    private List oracleSets;
    private Boolean finalFinished;
    private Boolean settleFinished;
    private String lastDealTime;
}
