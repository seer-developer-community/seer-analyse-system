package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyDataFaucetPo;
import com.seer.operation.mapper.DailyDataFaucetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyDataFaucetService {
    @Autowired
    private DailyDataFaucetMapper dataFaucetMapper;

    public void insert(DailyDataFaucetPo dailyDataFaucetPo) {
        dailyDataFaucetPo.setCreateTime(System.currentTimeMillis());
        dailyDataFaucetPo.setUpdateTime(System.currentTimeMillis());
        dataFaucetMapper.insert(dailyDataFaucetPo);
    }

    public void updateById(DailyDataFaucetPo dailyDataFaucetPo) {
        dailyDataFaucetPo.setUpdateTime(System.currentTimeMillis());
        dataFaucetMapper.updateById(dailyDataFaucetPo);
    }

    public DailyDataFaucetPo selectOneByTimestamp(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyDataFaucetPo> wrapper = new QueryWrapper<DailyDataFaucetPo>().lambda()
                .eq(DailyDataFaucetPo::getTimestamp, timestamp);
        return dataFaucetMapper.selectOne(wrapper);
    }

    public DailyDataFaucetPo selectLastOne() {
        LambdaQueryWrapper<DailyDataFaucetPo> wrapper = new QueryWrapper<DailyDataFaucetPo>().lambda();
        wrapper.orderByDesc(DailyDataFaucetPo::getTimestamp);
        wrapper.last("limit 1");
        return dataFaucetMapper.selectOne(wrapper);
    }

    public List<DailyDataFaucetPo> selectList(Long zero1, Long zero2) {
        LambdaQueryWrapper<DailyDataFaucetPo> wrapper = new QueryWrapper<DailyDataFaucetPo>().lambda();
        if (null != zero1 && null != zero2) {
            wrapper.between(DailyDataFaucetPo::getTimestamp, zero1, zero2);
        }
        wrapper.orderByDesc(DailyDataFaucetPo::getTimestamp);
        return dataFaucetMapper.selectList(wrapper);
    }

    public IPage<DailyDataFaucetPo> selectPage(Integer current, Integer size, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyDataFaucetPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyDataFaucetPo> wrapper = new QueryWrapper<DailyDataFaucetPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyDataFaucetPo::getTimestamp, zeroTime1, zeroTime2);
        }
        wrapper.orderByDesc(DailyDataFaucetPo::getTimestamp);
        return dataFaucetMapper.selectPage(page, wrapper);
    }
}
