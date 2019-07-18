package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.TotalRoomPo;
import com.seer.operation.mapper.TotalRoomMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.seer.operation.request.STATUS.ROOM_OPENING;

@Service
public class TotalRoomService {
    @Autowired
    private TotalRoomMapper totalRoomMapper;

    public void insert(TotalRoomPo totalRoomPo) {
        totalRoomPo.setCreateDate(new Date());
        totalRoomPo.setCreateTime(System.currentTimeMillis());
        totalRoomPo.setUpdateTime(System.currentTimeMillis());
        totalRoomMapper.insert(totalRoomPo);
    }

    public void updateById(TotalRoomPo totalRoomPo) {
        totalRoomPo.setUpdateTime(System.currentTimeMillis());
        totalRoomMapper.updateById(totalRoomPo);
    }

    public TotalRoomPo selectOneByRoom(String room) {
        if (StringUtils.isBlank(room)) {
            return null;
        }
        LambdaQueryWrapper<TotalRoomPo> wrapper = new QueryWrapper<TotalRoomPo>().lambda()
                .eq(TotalRoomPo::getRoom, room);
        return totalRoomMapper.selectOne(wrapper);
    }

    public List<TotalRoomPo> selectAllListForUpdate(Long zeroTimes) {
        LambdaQueryWrapper<TotalRoomPo> wrapper = new QueryWrapper<TotalRoomPo>().lambda()
                .eq(TotalRoomPo::getRoomStatus, ROOM_OPENING.getCode())
                .and(i->i.le(TotalRoomPo::getRoomStart,zeroTimes).ge(TotalRoomPo::getRoomStop,zeroTimes))
                .last("for update");
        return totalRoomMapper.selectList(wrapper);
    }

    public IPage<TotalRoomPo> selectPage(Integer current, Integer size, String room) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<TotalRoomPo> page = new Page<>(current, size);
        LambdaQueryWrapper<TotalRoomPo> wrapper = new QueryWrapper<TotalRoomPo>().lambda();
        if (!StringUtils.isBlank(room)) {
            wrapper.eq(TotalRoomPo::getRoom, room);
        }
        wrapper.orderByDesc(TotalRoomPo::getCreateTime, TotalRoomPo::getUpdateTime);
        return totalRoomMapper.selectPage(page, wrapper);
    }
}
