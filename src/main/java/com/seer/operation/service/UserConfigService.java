package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.mapper.UserConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConfigService {
    @Autowired
    private UserConfigMapper userConfigMapper;

    public void insert(UserConfigPo userConfig) {
        if (null == userConfig || isNull(userConfig)) {
            throw new IllegalArgumentException("参数为空");
        }
        userConfigMapper.insert(userConfig);
    }

    public void updateById(UserConfigPo userConfig) {
        if (null == userConfig) {
            throw new IllegalArgumentException("参数为空");
        }
        userConfigMapper.updateById(userConfig);
    }

    public UserConfigPo selectOne(Integer id) {
        if (null == id) {
            throw new IllegalArgumentException("参数为空");
        }
        return userConfigMapper.selectById(id);
    }

    public List<UserConfigPo> selectList() {
        LambdaQueryWrapper<UserConfigPo> wrapper = new QueryWrapper<UserConfigPo>().lambda();
        return userConfigMapper.selectList(wrapper);
    }

    private boolean isNull(UserConfigPo userConfigPo) {
        if (null == userConfigPo.getAssets() && null == userConfigPo.getFaucetAccounts() && null == userConfigPo.getGateways()
                && null == userConfigPo.getHouses() && null == userConfigPo.getSeerBots()) {
            return true;
        }
        return false;
    }
}
