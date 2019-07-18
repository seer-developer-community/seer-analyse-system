package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.DailyAccountDetailsPo;
import com.seer.operation.mapper.DailyAccountMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyAccountService {
    @Autowired
    private DailyAccountMapper dailyAccountMapper;

    public void insert(DailyAccountDetailsPo detailsPo) {
        detailsPo.setCreateTime(System.currentTimeMillis());
        detailsPo.setCreateDate(new Date());
        detailsPo.setUpdateTime(System.currentTimeMillis());
        dailyAccountMapper.insert(detailsPo);
    }

    public List<DailyAccountDetailsPo> selectListByIssuer(String issuer) {
        LambdaQueryWrapper<DailyAccountDetailsPo> wrapper = new QueryWrapper<DailyAccountDetailsPo>().lambda()
                .eq(DailyAccountDetailsPo::getIssuer, issuer);
        return dailyAccountMapper.selectList(wrapper);
    }

    public List<DailyAccountDetailsPo> selectList(Long time1, Long time2, String bot, String name, String issuer) {
        LambdaQueryWrapper<DailyAccountDetailsPo> wrapper = new QueryWrapper<DailyAccountDetailsPo>().lambda()
                .between(DailyAccountDetailsPo::getZeroTimestamp, time1, time2);
        if (StringUtils.isNotBlank(bot)) {
            wrapper.eq(DailyAccountDetailsPo::getIsBot, bot);
        }
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(DailyAccountDetailsPo::getName, name);
        }
        if (!StringUtils.isBlank(issuer)) {
            wrapper.eq(DailyAccountDetailsPo::getIssuer, issuer);
        }
        wrapper.orderByDesc(DailyAccountDetailsPo::getZeroTimestamp, DailyAccountDetailsPo::getCreateTime);
        return dailyAccountMapper.selectList(wrapper);
    }

    public IPage<DailyAccountDetailsPo> selectPage(Integer current, Integer size, String bot, String issuer, String name, Long zeroTime1, Long zeroTime2) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        Page<DailyAccountDetailsPo> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyAccountDetailsPo> wrapper = new QueryWrapper<DailyAccountDetailsPo>().lambda();
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(DailyAccountDetailsPo::getName, name);
        }
        if (!StringUtils.isBlank(issuer)) {
            wrapper.eq(DailyAccountDetailsPo::getIssuer, issuer);
        }
        if (null != zeroTime1 && null != zeroTime2) {
            wrapper.between(DailyAccountDetailsPo::getZeroTimestamp, zeroTime1, zeroTime2);
        }
        if (StringUtils.isNotBlank(bot)) {
            wrapper.eq(DailyAccountDetailsPo::getIsBot, bot);
        }
        wrapper.orderByDesc(DailyAccountDetailsPo::getZeroTimestamp, DailyAccountDetailsPo::getCreateTime);
        return dailyAccountMapper.selectPage(page, wrapper);
    }

    public DailyAccountDetailsPo selectOneByIssuerToday(String issuer, Long zeroTimestamp) {
        if (StringUtils.isBlank(issuer) || null == zeroTimestamp) {
            return null;
        }
        LambdaQueryWrapper<DailyAccountDetailsPo> wrapper = new QueryWrapper<DailyAccountDetailsPo>().lambda()
                .eq(DailyAccountDetailsPo::getIssuer, issuer)
                .eq(DailyAccountDetailsPo::getZeroTimestamp, zeroTimestamp);
        return dailyAccountMapper.selectOne(wrapper);
    }

    public Integer countByDay(Long begin, Long end) {
        if (null == begin || null == end) {
            return 0;
        }
        LambdaQueryWrapper<DailyAccountDetailsPo> wrapper = new QueryWrapper<DailyAccountDetailsPo>().lambda()
                .between(DailyAccountDetailsPo::getZeroTimestamp, begin, end);
        return dailyAccountMapper.selectCount(wrapper);
    }

    public void updateById(DailyAccountDetailsPo detailsPo) {
        detailsPo.setUpdateTime(System.currentTimeMillis());
        dailyAccountMapper.updateById(detailsPo);
    }

}
