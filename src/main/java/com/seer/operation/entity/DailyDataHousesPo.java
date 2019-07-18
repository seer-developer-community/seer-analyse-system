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
@TableName("daily_data_houses")
public class DailyDataHousesPo implements Serializable {
    @TableId(value = "timestamp", type = IdType.INPUT)
    private Long timestamp;
    private String time;
    private String totalPvpProfit;
    private BigDecimal totalSubsidy;
    private BigDecimal dailySubsidy;
    private String dailyNewRooms;
    private String dailyActiveRooms;
    private String dailyPrtpRate;
    private String totalRooms;
    private String totalPrtpRate;
    private String totalAdvplayAmount;
    private BigDecimal totalAdvprtpFees;
    private String totalAdvSettle;
    private String dailyOpeningRoom;
    private String prtpTimes;
    private String totalPrtpTimes;
    private String totalPvpplayAmount;
    private String totalPlayAmount;
    private BigDecimal totalPvpprtpFees;
    private String totalPvpSettle;
    private BigDecimal totalPvpFees;
    private String totalPvdplayAmount;
    private BigDecimal totalPvdFees;
    private BigDecimal totalAdvFees;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
    private String dailyActiveRoomsList;
    private String totalActiveRoomsList;
    private String totalActiveRooms;
}
