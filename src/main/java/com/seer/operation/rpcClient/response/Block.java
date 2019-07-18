package com.seer.operation.rpcClient.response;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Block implements Serializable {
    private static final long serialVersionUID = 1L;
    //上一个块的块号
    private String previous;
    //时间戳
    private String timestamp;
    //见证人
    private String witness;
    private String transactionMerkleRoot;
    private List extensions;
    //见证人签名
    private String witnessSignature;
    //交易列表
    private List<Transactions> transactions;
    //块id
    private String blockId;
    //见证人签名公钥
    private String signingKey;
    //交易id集
    private ArrayList<String> transactionIds;
    private Integer txsCount;
}
