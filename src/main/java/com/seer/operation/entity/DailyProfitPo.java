package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("daily_profit")
public class DailyProfitPo implements Serializable {
    @TableId(value = "timestamp", type = IdType.INPUT)
    private Long timestamp;
    private String time;
    private String dappProfit;
    private String totalDappProfit;
    private BigDecimal faucetProfit;
    private BigDecimal totalFaucetProfit;
    private BigDecimal feesProfit;
    private BigDecimal totalFeesProfit;
    private BigDecimal dailySubsidy;
    private BigDecimal totalSubsidy;
    private String accountBotprtpAmount;
    private String totalAccountBotprtpAmount;
    private String roomprtpAmount;
    private String totalRoomprtpAmount;
    private String roomprtpSettle;
    private String totalRoomprtpSettle;
    private BigDecimal registeredFees;
    private BigDecimal totalRegisteredFees;
    private String transferCountFees;
    private String totalTransferCountFees;
    private Integer lastBlock;
    private Long createTime;
    private Long updateTime;
    private String collectedFees;
    private String allowedWithdraw;
}
