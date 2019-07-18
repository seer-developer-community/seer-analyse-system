package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyTypeRoomPo;
import com.seer.operation.mapper.DailyTypeRoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTypeRoomService {
    @Autowired
    private DailyTypeRoomMapper dailyTypeRoomMapper;

    public void insert(DailyTypeRoomPo dailyTypeRoomPo) {
        dailyTypeRoomPo.setCreateTime(System.currentTimeMillis());
        dailyTypeRoomPo.setUpdateTime(System.currentTimeMillis());
        dailyTypeRoomMapper.insert(dailyTypeRoomPo);
    }

    public void updateById(DailyTypeRoomPo dailyTypeRoomPo) {
        dailyTypeRoomPo.setUpdateTime(System.currentTimeMillis());
        dailyTypeRoomMapper.updateById(dailyTypeRoomPo);
    }

    public DailyTypeRoomPo selectOneByTimestamp(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyTypeRoomPo> wrapper = new QueryWrapper<DailyTypeRoomPo>().lambda()
                .eq(DailyTypeRoomPo::getTimestamp, timestamp);
        return dailyTypeRoomMapper.selectOne(wrapper);
    }

    public List<DailyTypeRoomPo> selectList(Long zero1, Long zero2) {
        LambdaQueryWrapper<DailyTypeRoomPo> wrapper = new QueryWrapper<DailyTypeRoomPo>().lambda();
        if (null != zero1 && null != zero2) {
            wrapper.between(DailyTypeRoomPo::getTimestamp, zero1, zero2);
        }
        wrapper.orderByDesc(DailyTypeRoomPo::getTimestamp);
        return dailyTypeRoomMapper.selectList(wrapper);
    }

    public IPage<DailyTypeRoomPo> selectPage(Integer current, Integer size, Long zeroTime1, Long zeroTime2) {
        if (null == size) {
            size = 50;
        }
        if (null == current) {
            current = 1;
        }
        Page<DailyTypeRoomPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyTypeRoomPo> wrapper = new QueryWrapper<DailyTypeRoomPo>().lambda();
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyTypeRoomPo::getTimestamp, zeroTime1, zeroTime2);
        }
        wrapper.orderByDesc(DailyTypeRoomPo::getTimestamp);
        return dailyTypeRoomMapper.selectPage(page, wrapper);
    }

    public DailyTypeRoomPo selectLastOne() {
        LambdaQueryWrapper<DailyTypeRoomPo> wrapper = new QueryWrapper<DailyTypeRoomPo>().lambda();
        wrapper.orderByDesc(DailyTypeRoomPo::getTimestamp);
        wrapper.last("LIMIT 1");
        return dailyTypeRoomMapper.selectOne(wrapper);
    }
}
