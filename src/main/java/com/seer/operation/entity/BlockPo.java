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
@TableName("seer_block")
public class BlockPo implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private BigInteger id;
    private String previous;
    private String timestamp;
    private String eastEightTimestamp;
    private String witness;
    private String merkleRoot;
    private String extensions;
    private String witnessSignature;
    private String transactionIds;
    private String blockId;
    private String signingKey;
    private Integer txsCount;
    private Long createTime;
}
