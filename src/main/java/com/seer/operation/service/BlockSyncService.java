package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.BlockSyncPo;
import com.seer.operation.mapper.BlockSyncMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockSyncService {
    @Autowired
    private BlockSyncMapper blockSyncMapper;

    public List<BlockSyncPo> selectList() {
        LambdaQueryWrapper<BlockSyncPo> wrapper = new QueryWrapper<BlockSyncPo>().lambda();
        return blockSyncMapper.selectList(wrapper);
    }

    public IPage<BlockSyncPo> selectPage() {
        Page<BlockSyncPo> page = new Page<>(2, 5);
        LambdaQueryWrapper<BlockSyncPo> wrapper = new QueryWrapper<BlockSyncPo>().lambda();
        return blockSyncMapper.selectPage(page, wrapper);
    }

    public BlockSyncPo selectOneByIdEqOne() {
        return blockSyncMapper.selectById(1);
    }

    public BlockSyncPo selectOneByIdEqThree() {
        return blockSyncMapper.selectById(3);
    }

    public BlockSyncPo selectOneByIdEqTwo() {
        return blockSyncMapper.selectById(2);
    }

    public void updateById(BlockSyncPo blockSyncPo) {
        blockSyncMapper.updateById(blockSyncPo);
    }

    public void updateBlockSync() {
        BlockSyncPo blockSyncPo = blockSyncMapper.selectById(1);
        if (null == blockSyncPo) {
            throw new IllegalArgumentException("id=1的区块同步记录不存在");
        }
        blockSyncPo.setBlockSync(blockSyncPo.getBlockSync() + 1);
        blockSyncPo.setStatus(null);
        blockSyncPo.setSaveTx(null);
        blockSyncPo.setSaveBlock(null);
        blockSyncMapper.updateById(blockSyncPo);
    }
}
