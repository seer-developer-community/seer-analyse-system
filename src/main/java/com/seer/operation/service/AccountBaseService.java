package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.AccountBasePo;
import com.seer.operation.mapper.AccountBaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountBaseService {
    @Autowired
    private AccountBaseMapper accountBaseMapper;

    public void insert(AccountBasePo accountBasePo) {
        accountBasePo.setCreateTime(System.currentTimeMillis());
        accountBasePo.setUpdateTime(System.currentTimeMillis());
        accountBaseMapper.insert(accountBasePo);
    }

    public void updateById(AccountBasePo accountBasePo) {
        accountBasePo.setUpdateTime(System.currentTimeMillis());
        accountBaseMapper.updateById(accountBasePo);
    }

    public AccountBasePo selectById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return accountBaseMapper.selectById(id);
    }

    public AccountBasePo selectIsBotById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        LambdaQueryWrapper<AccountBasePo> wrapper = new QueryWrapper<AccountBasePo>().lambda()
                .eq(AccountBasePo::getId,id).select(AccountBasePo::getIsSeerbot);
        return accountBaseMapper.selectOne(wrapper);
    }

    public IPage<AccountBasePo> selectPage(Integer current, Integer size, String id, String name) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        LambdaQueryWrapper<AccountBasePo> wrapper = new QueryWrapper<AccountBasePo>().lambda();
        if (!StringUtils.isBlank(id)) {
            wrapper.eq(AccountBasePo::getId, id);
        }
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(AccountBasePo::getName, name);
        }
        Page<AccountBasePo> page = new Page<>(current, size);
        wrapper.orderByDesc(AccountBasePo::getCreateTime);
        return accountBaseMapper.selectPage(page, wrapper);
    }
}
