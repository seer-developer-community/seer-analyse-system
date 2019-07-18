package com.seer.operation.rpcClient.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

@Data
public class BlockInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    //当前块高
    private BigInteger headBlockNum;
    //当前块号
    private String headBlockId;
    //上一个区块生成时间
    private String headBlockAge;
    //维护更新时间
    private String nextMaintenanceTime;
    //链号
    private String chainId;
    //区块生产参与率
    private String participation;
    //活跃见证人ID
    private ArrayList<String> activeWitnesses;
    //活跃理事会成员ID
    private ArrayList<String> activeCommitteeMembers;
}
