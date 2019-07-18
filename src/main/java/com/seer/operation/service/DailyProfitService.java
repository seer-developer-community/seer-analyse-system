package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyProfitPo;
import com.seer.operation.mapper.DailyProfitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyProfitService {
    @Autowired
    private DailyProfitMapper dailyProfitMapper;

    public void insert(DailyProfitPo dailyProfitPo) {
        dailyProfitPo.setCreateTime(System.currentTimeMillis());
        dailyProfitPo.setUpdateTime(System.currentTimeMillis());
        dailyProfitMapper.insert(dailyProfitPo);
    }

    public void updateById(DailyProfitPo dailyProfitPo) {
        dailyProfitPo.setUpdateTime(System.currentTimeMillis());
        dailyProfitMapper.updateById(dailyProfitPo);
    }

    public DailyProfitPo selectOneByTimestamp(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyProfitPo> wrapper = new QueryWrapper<DailyProfitPo>().lambda()
                .eq(DailyProfitPo::getTimestamp, timestamp);
        return dailyProfitMapper.selectOne(wrapper);
    }

    public List<DailyProfitPo> selectList(Long zero1, Long zero2) {
        LambdaQueryWrapper<DailyProfitPo> wrapper = new QueryWrapper<DailyProfitPo>().lambda();
        if (null != zero1 && null != zero2) {
            wrapper.between(DailyProfitPo::getTimestamp, zero1, zero2);
        }
        wrapper.orderByDesc(DailyProfitPo::getTimestamp);
        return dailyProfitMapper.selectList(wrapper);
    }

    public IPage<DailyProfitPo> selectPage(Integer current, Integer size, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyProfitPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyProfitPo> wrapper = new QueryWrapper<DailyProfitPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyProfitPo::getTimestamp, zeroTime1, zeroTime2);
        }
        wrapper.orderByDesc(DailyProfitPo::getTimestamp);
        return dailyProfitMapper.selectPage(page, wrapper);
    }

    public DailyProfitPo selectLastOne() {
        LambdaQueryWrapper<DailyProfitPo> wrapper = new QueryWrapper<DailyProfitPo>().lambda();
        wrapper.orderByDesc(DailyProfitPo::getTimestamp);
        wrapper.last("LIMIT 1");
        return dailyProfitMapper.selectOne(wrapper);
    }
}
