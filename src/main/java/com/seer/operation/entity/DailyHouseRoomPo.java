package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("daily_house_room")
public class DailyHouseRoomPo implements Serializable {
    @TableId(value = "timestamp", type = IdType.INPUT)
    private Long timestamp;
    private String time;
    private String dailyNewRooms;
    private String dailyPrtpRate;
    private String totalRooms;
    private String totalPrtpRate;
    private String dailyOpeningRoom;
    private String prtpTimes;
    private String totalPrtpTimes;
    private String playAmount;
    private String totalPlayAmount;
    private String newRoomsActiveList;
    private String newActiveRooms;
    private String newRoomsList;
    private String totalActiveRoomsList;
    private String totalActiveRooms;
    private Integer lastBlock;
    private Long createTime;
    private Long updateTime;
}
