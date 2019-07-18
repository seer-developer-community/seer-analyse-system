package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.AccountHouseTotalPo;
import com.seer.operation.mapper.AccountHouseTotalMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountHouseTotalService {
    @Autowired
    private AccountHouseTotalMapper houseTotalMapper;

    public void insert(AccountHouseTotalPo houseTotalPo) {
        houseTotalPo.setCreateTime(System.currentTimeMillis());
        houseTotalPo.setUpdateTime(System.currentTimeMillis());
        houseTotalMapper.insert(houseTotalPo);
    }

    public void updateById(AccountHouseTotalPo houseTotalPo) {
        houseTotalPo.setUpdateTime(System.currentTimeMillis());
        houseTotalMapper.updateById(houseTotalPo);
    }

    public AccountHouseTotalPo selectById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return houseTotalMapper.selectById(id);
    }

    public IPage<AccountHouseTotalPo> selectPage(Integer current, Integer size, String id, String name) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        LambdaQueryWrapper<AccountHouseTotalPo> wrapper = new QueryWrapper<AccountHouseTotalPo>().lambda();
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(AccountHouseTotalPo::getName, name);
        }
        if (!StringUtils.isBlank(id)) {
            wrapper.eq(AccountHouseTotalPo::getId, id);
        }
        Page<AccountHouseTotalPo> page = new Page<>(current, size);
        wrapper.orderByDesc(AccountHouseTotalPo::getCreateTime);
        return houseTotalMapper.selectPage(page, wrapper);
    }
}
