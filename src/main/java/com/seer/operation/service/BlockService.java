package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.BlockPo;
import com.seer.operation.mapper.BlockMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class BlockService {
    @Autowired
    private BlockMapper blockMapper;

    public void insert(BlockPo blockPo) {
        if (null == blockPo.getId()) {
            throw new IllegalArgumentException("主键id为空");
        }
        blockMapper.insert(blockPo);
    }

    public BlockPo selectById(BigInteger id) {
        if (null == id) {
            throw new IllegalArgumentException("参数为空");
        }
        return blockMapper.selectById(id);
    }

    public IPage<BlockPo> selectPage(Integer current, Integer size, String block) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        LambdaQueryWrapper<BlockPo> wrapper = new QueryWrapper<BlockPo>().lambda();
        if (!StringUtils.isBlank(block)) {
            wrapper.eq(BlockPo::getId, Integer.valueOf(block));
            size = 1;
            current = 0;
        }
        Page<BlockPo> page = new Page<>(current, size);
        wrapper.orderByDesc(BlockPo::getId);
        return blockMapper.selectPage(page, wrapper);
    }
}
