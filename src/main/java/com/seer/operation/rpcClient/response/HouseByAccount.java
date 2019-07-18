package com.seer.operation.rpcClient.response;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.Serializable;

@Data
public class HouseByAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String owner;
    private String description;
    private String script;
    private Integer reputation;
    private Long guaranty;
    private Integer volume;
    private Long collectedFees;
    private JSONArray rooms;
    private JSONArray finishedRooms;
}
