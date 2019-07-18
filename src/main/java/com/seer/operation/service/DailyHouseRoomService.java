package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyHouseRoomPo;
import com.seer.operation.mapper.DailyHouseRoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyHouseRoomService {
    @Autowired
    private DailyHouseRoomMapper dailyHouseRoomMapper;

    public void insert(DailyHouseRoomPo dailyHouseRoomPo) {
        dailyHouseRoomPo.setCreateTime(System.currentTimeMillis());
        dailyHouseRoomPo.setUpdateTime(System.currentTimeMillis());
        dailyHouseRoomMapper.insert(dailyHouseRoomPo);
    }

    public void updateById(DailyHouseRoomPo dailyHouseRoomPo) {
        dailyHouseRoomPo.setUpdateTime(System.currentTimeMillis());
        dailyHouseRoomMapper.updateById(dailyHouseRoomPo);
    }

    public DailyHouseRoomPo selectOneByTimestamp(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyHouseRoomPo> wrapper = new QueryWrapper<DailyHouseRoomPo>().lambda()
                .eq(DailyHouseRoomPo::getTimestamp, timestamp);
        return dailyHouseRoomMapper.selectOne(wrapper);
    }

    public List<DailyHouseRoomPo> selectList(Long zero1, Long zero2) {
        LambdaQueryWrapper<DailyHouseRoomPo> wrapper = new QueryWrapper<DailyHouseRoomPo>().lambda();
        if (null != zero1 && null != zero2) {
            wrapper.between(DailyHouseRoomPo::getTimestamp, zero1, zero2);
        }
        wrapper.orderByDesc(DailyHouseRoomPo::getTimestamp);
        return dailyHouseRoomMapper.selectList(wrapper);
    }

    public IPage<DailyHouseRoomPo> selectPage(Integer current, Integer size, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyHouseRoomPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyHouseRoomPo> wrapper = new QueryWrapper<DailyHouseRoomPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyHouseRoomPo::getTimestamp, zeroTime1, zeroTime2);
        }
        wrapper.orderByDesc(DailyHouseRoomPo::getTimestamp);
        return dailyHouseRoomMapper.selectPage(page, wrapper);
    }

    public DailyHouseRoomPo selectLastOne() {
        LambdaQueryWrapper<DailyHouseRoomPo> wrapper = new QueryWrapper<DailyHouseRoomPo>().lambda();
        wrapper.orderByDesc(DailyHouseRoomPo::getTimestamp);
        wrapper.last("LIMIT 1");
        return dailyHouseRoomMapper.selectOne(wrapper);
    }
}
