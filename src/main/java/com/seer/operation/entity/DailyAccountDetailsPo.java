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
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("daily_account_details")
public class DailyAccountDetailsPo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Long zeroTimestamp;
    private String time;
    private String issuer;
    private String name;
    private Integer dailyPrtpCount;
    private String dailyPrtpAmount;
    private BigDecimal dailyFee;
    private BigDecimal dailyProfit;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
    private Date createDate;
    private Integer isBot;

}
