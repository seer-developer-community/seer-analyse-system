package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.AccountRegisterPo;
import com.seer.operation.mapper.AccountRegisterMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountRegisterService {
    @Autowired
    private AccountRegisterMapper registerMapper;

    public void insert(AccountRegisterPo accountRegisterPo) {
        accountRegisterPo.setCreateTime(System.currentTimeMillis());
        accountRegisterPo.setUpdateTime(System.currentTimeMillis());
        registerMapper.insert(accountRegisterPo);
    }

    public void updateById(AccountRegisterPo registerPo) {
        registerPo.setUpdateTime(System.currentTimeMillis());
        registerMapper.updateById(registerPo);
    }

    public AccountRegisterPo selectById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return registerMapper.selectById(id);
    }

    public IPage<AccountRegisterPo> selectPage(Integer current, Integer size, String id, String name) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        LambdaQueryWrapper<AccountRegisterPo> wrapper = new QueryWrapper<AccountRegisterPo>().lambda();
        if (!StringUtils.isBlank(id)) {
            wrapper.eq(AccountRegisterPo::getId, id);
        }
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(AccountRegisterPo::getName, name);
        }
        Page<AccountRegisterPo> page = new Page<>(current, size);
        wrapper.orderByDesc(AccountRegisterPo::getCreateTime);
        return registerMapper.selectPage(page, wrapper);
    }
}
