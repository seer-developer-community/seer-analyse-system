package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.AccountAuthPo;
import com.seer.operation.mapper.AccountAuthMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountAuthService {
    @Autowired
    private AccountAuthMapper accountAuthMapper;

    public void insert(AccountAuthPo accountAuthPo) {
        accountAuthPo.setCreateTime(System.currentTimeMillis());
        accountAuthPo.setUpdateTime(System.currentTimeMillis());
        accountAuthMapper.insert(accountAuthPo);
    }

    public void updateById(AccountAuthPo accountAuthPo) {
        accountAuthPo.setUpdateTime(System.currentTimeMillis());
        accountAuthMapper.updateById(accountAuthPo);
    }

    public AccountAuthPo selectById(String id) {
        return accountAuthMapper.selectById(id);
    }

    public IPage<AccountAuthPo> selectPage(Integer current, Integer size, String id, String name) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        LambdaQueryWrapper<AccountAuthPo> wrapper = new QueryWrapper<AccountAuthPo>().lambda();
        if (!StringUtils.isBlank(id)) {
            wrapper.eq(AccountAuthPo::getId, id);
        }
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(AccountAuthPo::getName, name);
        }
        Page<AccountAuthPo> page = new Page<>(current, size);
        wrapper.orderByDesc(AccountAuthPo::getCreateTime);
        return accountAuthMapper.selectPage(page, wrapper);
    }
}
