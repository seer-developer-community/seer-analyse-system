package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.AccountHouseTypePo;
import com.seer.operation.mapper.AccountHouseTypeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountHouseTypeService {
    @Autowired
    private AccountHouseTypeMapper houseTypeMapper;

    public void insert(AccountHouseTypePo houseTypePo) {
        houseTypePo.setCreateTime(System.currentTimeMillis());
        houseTypePo.setUpdateTime(System.currentTimeMillis());
        houseTypeMapper.insert(houseTypePo);
    }

    public void updateById(AccountHouseTypePo houseTypePo) {
        houseTypePo.setUpdateTime(System.currentTimeMillis());
        houseTypeMapper.updateById(houseTypePo);
    }

    public AccountHouseTypePo selectById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return houseTypeMapper.selectById(id);
    }

    public IPage<AccountHouseTypePo> selectPage(Integer current, Integer size, String id, String name) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        LambdaQueryWrapper<AccountHouseTypePo> wrapper = new QueryWrapper<AccountHouseTypePo>().lambda();
        if (!StringUtils.isBlank(name)) {
            wrapper.eq(AccountHouseTypePo::getName, name);
        }
        if (!StringUtils.isBlank(id)) {
            wrapper.eq(AccountHouseTypePo::getId, id);
        }
        Page<AccountHouseTypePo> page = new Page<>(current, size);
        wrapper.orderByDesc(AccountHouseTypePo::getCreateTime);
        return houseTypeMapper.selectPage(page, wrapper);
    }

}
