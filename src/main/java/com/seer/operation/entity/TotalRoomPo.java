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
@TableName("total_room_details")
public class TotalRoomPo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String room;
    private String totalBotPlayAmount;
    private Integer roomStatus;
    private Long roomStart;
    private Long roomStop;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
    private Date createDate;
}
