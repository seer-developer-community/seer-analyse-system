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
@TableName("account_house_type")
public class AccountHouseTypePo implements Serializable {
    @TableId(value = "id",type = IdType.INPUT)
    private String id;
    private String name;
    private BigDecimal housePvdprtpFees;
    private BigDecimal housePvpprtpFees;
    private BigDecimal houseBotpvdprtpFees;
    private BigDecimal houseBotpvpprtpFees;
    private String totalPvpProfit;
    private String totalBotpvpProfit;
    private String totalPvdplayAmount;
    private String totalPvpplayAmount;
    private String totalBotpvdplayAmount;
    private String totalBotpvpplayAmount;
    private String housePvdSettle;
    private String housePvpSettle;
    private BigDecimal totalPvdFees;
    private BigDecimal totalPvpFees;
    private String houseBotpvdSettle;
    private String houseBotpvpSettle;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
}
