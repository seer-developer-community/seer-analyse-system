package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyRoomDetailsPo;
import com.seer.operation.mapper.DailyRoomMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyRoomService {
    @Autowired
    private DailyRoomMapper dailyRoomMapper;

    public void insert(DailyRoomDetailsPo roomDetailsPo) {
        roomDetailsPo.setCreateDate(new Date());
        roomDetailsPo.setCreateTime(System.currentTimeMillis());
        roomDetailsPo.setUpdateTime(System.currentTimeMillis());
        dailyRoomMapper.insert(roomDetailsPo);
    }

    public void updateById(DailyRoomDetailsPo roomDetailsPo) {
        roomDetailsPo.setUpdateTime(System.currentTimeMillis());
        dailyRoomMapper.updateById(roomDetailsPo);
    }

    public DailyRoomDetailsPo selectOneByRoomToday(String room, Long zero) {
        if (StringUtils.isBlank(room) || null == zero) {
            return null;
        }
        LambdaQueryWrapper<DailyRoomDetailsPo> wrapper = new QueryWrapper<DailyRoomDetailsPo>().lambda()
                .eq(DailyRoomDetailsPo::getRoom, room)
                .eq(DailyRoomDetailsPo::getZeroTimestamp, zero);
        return dailyRoomMapper.selectOne(wrapper);
    }

    public List<DailyRoomDetailsPo> selectListToday(Long zero) {
        LambdaQueryWrapper<DailyRoomDetailsPo> wrapper = new QueryWrapper<DailyRoomDetailsPo>().lambda()
                .eq(DailyRoomDetailsPo::getZeroTimestamp, zero);
        return dailyRoomMapper.selectList(wrapper);
    }

    public List<DailyRoomDetailsPo> selectList(Long zero1, Long zero2, String room) {
        LambdaQueryWrapper<DailyRoomDetailsPo> wrapper = new QueryWrapper<DailyRoomDetailsPo>().lambda()
                .between(DailyRoomDetailsPo::getZeroTimestamp, zero1, zero2);
        if (StringUtils.isNotBlank(room)) {
            wrapper.eq(DailyRoomDetailsPo::getRoom, room);
        }
        wrapper.orderByDesc(DailyRoomDetailsPo::getZeroTimestamp);
        return dailyRoomMapper.selectList(wrapper);
    }

    public IPage<DailyRoomDetailsPo> selectPage(Integer current, Integer size, String room, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyRoomDetailsPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyRoomDetailsPo> wrapper = new QueryWrapper<DailyRoomDetailsPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyRoomDetailsPo::getZeroTimestamp, zeroTime1, zeroTime2);
        }
        if (!StringUtils.isBlank(room)) {
            wrapper.eq(DailyRoomDetailsPo::getRoom, room);
        }
        wrapper.orderByDesc(DailyRoomDetailsPo::getZeroTimestamp);
        return dailyRoomMapper.selectPage(page, wrapper);
    }
}
