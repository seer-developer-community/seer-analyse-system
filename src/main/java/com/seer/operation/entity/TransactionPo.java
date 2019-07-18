package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("seer_transaction")
public class TransactionPo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private BigInteger id;
    private BigInteger blockHeight; //块高
    private String txId; //交易哈希
    private BigInteger refBlockNum;
    private String refBlockPrefix;
    private String expiration;
    private String operations;
    private String extensions;
    private String signatures;
    private String operationResults;
    private Long createTime;
    private Integer type;
    private Integer nonce;
    private Long blockTime;
}
