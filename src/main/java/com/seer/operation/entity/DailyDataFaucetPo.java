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
@TableName("daily_data_faucet")
public class DailyDataFaucetPo implements Serializable {
    @TableId(value = "timestamp", type = IdType.INPUT)
    private Long timestamp;
    private String time;
    private Integer dailyRegistered;
    private Integer totalRegistered;
    private Integer dailyTrueplayers;
    private Integer dailyPlayers;
    private Integer totalPlayers;
    private Integer totalActivePlayer;
    private String dailyDepositCount;
    private String totalTransferCountFees;
    private String totalDepositCount;
    private String dailyDepositAmount;
    private String totalDepositAmount;
    private String dailyTransferCount;
    private String totalTransferCount;
    private String dailyTransferAmount;
    private String totalTransferAmount;
    private BigDecimal totalRegisteredFees;
    private BigDecimal registeredFees;
    private String transferCountFees;
    private Long createTime;
    private Long updateTime;
    private BigInteger lastBlock;
    private BigDecimal totalFaucetProfit;
    private BigDecimal faucetProfit;
}
