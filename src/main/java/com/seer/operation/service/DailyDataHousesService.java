package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyDataHousesPo;
import com.seer.operation.mapper.DailyDataHousesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyDataHousesService {
    @Autowired
    private DailyDataHousesMapper dataHousesMapper;

    public void insert(DailyDataHousesPo dailyDataHousesPo) {
        dailyDataHousesPo.setCreateTime(System.currentTimeMillis());
        dailyDataHousesPo.setUpdateTime(System.currentTimeMillis());
        dataHousesMapper.insert(dailyDataHousesPo);
    }

    public void updateById(DailyDataHousesPo dailyDataHousesPo) {
        dailyDataHousesPo.setUpdateTime(System.currentTimeMillis());
        dataHousesMapper.updateById(dailyDataHousesPo);
    }

    public DailyDataHousesPo selectOneByTimestamp(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyDataHousesPo> wrapper = new QueryWrapper<DailyDataHousesPo>().lambda()
                .eq(DailyDataHousesPo::getTimestamp, timestamp);
        return dataHousesMapper.selectOne(wrapper);
    }

    public List<DailyDataHousesPo> selectList(Long zero1, Long zero2) {
        LambdaQueryWrapper<DailyDataHousesPo> wrapper = new QueryWrapper<DailyDataHousesPo>().lambda();
        if (null != zero1 && null != zero2) {
            wrapper.between(DailyDataHousesPo::getTimestamp, zero1, zero2);
        }
        wrapper.orderByAsc(DailyDataHousesPo::getTimestamp);
        return dataHousesMapper.selectList(wrapper);
    }

    public DailyDataHousesPo selectLastOne() {
        LambdaQueryWrapper<DailyDataHousesPo> wrapper = new QueryWrapper<DailyDataHousesPo>().lambda();
        wrapper.orderByDesc(DailyDataHousesPo::getTimestamp);
        wrapper.last("limit 1");
        return dataHousesMapper.selectOne(wrapper);
    }

    public IPage<DailyDataHousesPo> selectPage(Integer current, Integer size, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyDataHousesPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyDataHousesPo> wrapper = new QueryWrapper<DailyDataHousesPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyDataHousesPo::getTimestamp, zeroTime1, zeroTime2);
        }
        wrapper.orderByDesc(DailyDataHousesPo::getTimestamp);
        return dataHousesMapper.selectPage(page, wrapper);
    }
}
