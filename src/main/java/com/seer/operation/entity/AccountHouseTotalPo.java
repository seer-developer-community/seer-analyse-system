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
@TableName("account_house_total")
public class AccountHouseTotalPo implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String name;
    private BigDecimal houseAdvprtpFees;
    private BigDecimal houseBotadvprtpFees;
    private BigDecimal houseSeerbotFees;
    private String totalPrtpProfit;
    private String totalPlayAmount;
    private String totalAdvplayAmount;
    private String totalBotadvplayAmount;
    private String totalBotplayAmount;
    private Integer houseRooms;
    private String prtpRooms;
    private String activeRooms;
    private Integer housePrtpCount;
    private Integer houseBotprtpCount;
    private Integer housePrtpTimes;
    private Integer houseBotprtpTimes;
    private String houseAdvSettle;
    private String houseBotadvSettle;
    private BigDecimal totalAdvFees;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;

}
