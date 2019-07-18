package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyDataTotalPo;
import com.seer.operation.mapper.DailyDataTotalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyDataTotalService {
    @Autowired
    private DailyDataTotalMapper dataTotalMapper;

    public void insert(DailyDataTotalPo dataTotalPo) {
        dataTotalPo.setCreateTime(System.currentTimeMillis());
        dataTotalPo.setUpdateTime(System.currentTimeMillis());
        dataTotalMapper.insert(dataTotalPo);
    }

    public void updateById(DailyDataTotalPo dataTotalPo) {
        dataTotalPo.setUpdateTime(System.currentTimeMillis());
        dataTotalMapper.updateById(dataTotalPo);
    }

    public DailyDataTotalPo selectOneByTimestamp(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyDataTotalPo> wrapper = new QueryWrapper<DailyDataTotalPo>().lambda()
                .eq(DailyDataTotalPo::getTimestamp, timestamp);
        return dataTotalMapper.selectOne(wrapper);
    }

    public DailyDataTotalPo selectLastOneWithThree() {
        LambdaQueryWrapper<DailyDataTotalPo> wrapper = new QueryWrapper<DailyDataTotalPo>().lambda()
                .select(DailyDataTotalPo::getTotalAccountBotprtpAmount,
                        DailyDataTotalPo::getTotalRoomprtpAmount,
                        DailyDataTotalPo::getTotalRoomprtpSettle);
        wrapper.orderByDesc(DailyDataTotalPo::getTimestamp);
        wrapper.last("LIMIT 1");
        return dataTotalMapper.selectOne(wrapper);
    }

    public List<DailyDataTotalPo> selectList(Long zero1, Long zero2) {
        LambdaQueryWrapper<DailyDataTotalPo> wrapper = new QueryWrapper<DailyDataTotalPo>().lambda();
        if (null != zero1 && null != zero2) {
            wrapper.between(DailyDataTotalPo::getTimestamp, zero1, zero2);
        }
        wrapper.orderByAsc(DailyDataTotalPo::getTimestamp);
        return dataTotalMapper.selectList(wrapper);
    }

    public IPage<DailyDataTotalPo> selectPage(Integer current, Integer size, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyDataTotalPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyDataTotalPo> wrapper = new QueryWrapper<DailyDataTotalPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyDataTotalPo::getTimestamp, zeroTime1, zeroTime2);
        }
        wrapper.orderByDesc(DailyDataTotalPo::getTimestamp);
        return dataTotalMapper.selectPage(page, wrapper);
    }
}
