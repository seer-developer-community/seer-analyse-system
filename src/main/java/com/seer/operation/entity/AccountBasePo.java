package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("account_base")
public class AccountBasePo implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String name;
    private String registrar;
    private String referrer;
    private Integer referrerPercent;
    private Long regTime;
    private Long recentPlayTime;
    private Integer registered;
    private Integer isPlayer;
    private Integer isSeerbot;
    private Integer accountPrtpCount;
    private String accountPrtpAmount;
    private BigDecimal claimedFaucetProfit;
    private BigDecimal claimedFeesProfit;
    private BigDecimal totalFees;
    private String totalTransferCountFees;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
}
