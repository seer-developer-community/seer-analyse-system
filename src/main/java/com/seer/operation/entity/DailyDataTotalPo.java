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
@TableName("daily_data_total")
public class DailyDataTotalPo implements Serializable {
    @TableId(value = "timestamp",type = IdType.INPUT)
    private Long timestamp;
    private String time;
    private String pvpplayAmount;
    private BigDecimal pvpprtpFees;
    private String pvpSettle;
    private BigDecimal pvpFees;
    private String pvdplayAmount;
    private BigDecimal pvdprtpFees;
    private String pvdSettle;
    private String totalRoomprtpAmount;
    private String totalAccountBotprtpAmount;
    private BigDecimal pvdFees;
    private String advplayAmount;
    private BigDecimal advprtpFees;
    private String advSettle;
    private BigDecimal advFees;
    private String roomprtpAmount;
    private String roomprtpSettle;
    private String accountBotprtpAmount;
    private String totalRoomprtpSettle;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
}
