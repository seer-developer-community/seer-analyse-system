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
@TableName("daily_type_room")
public class DailyTypeRoomPo implements Serializable {
    @TableId(value = "timestamp", type = IdType.INPUT)
    private Long timestamp;
    private String time;
    private String pvpProfit;
    private String totalPvpProfit;
    private String pvpPlayAmount;
    private String totalPvpPlayAmount;
    private String pvpSettle;
    private String totalPvpSettle;
    private BigDecimal pvpFees;
    private BigDecimal totalPvpFees;
    private BigDecimal pvpPrtpFees;
    private BigDecimal totalPvpPrtpFees;
    private String pvdPlayAmount;
    private String pvdSettle;
    private String totalPvdPlayAmount;
    private String totalPvdSettle;
    private BigDecimal pvdFees;
    private BigDecimal totalPvdFees;
    private BigDecimal pvdPrtpFees;
    private BigDecimal totalPvdPrtpFees;
    private String advPlayAmount;
    private String totalAdvPlayAmount;
    private String advSettle;
    private String totalAdvSettle;
    private BigDecimal advFees;
    private BigDecimal totalAdvFees;
    private BigDecimal advPrtpFees;
    private BigDecimal totalAdvPrtpFees;
    private Integer lastBlock;
    private Long createTime;
    private Long updateTime;
}
