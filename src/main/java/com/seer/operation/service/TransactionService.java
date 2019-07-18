package com.seer.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.mapper.TransactionMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionMapper transactionMapper;

    public void insert(TransactionPo transactionPo) {
        transactionPo.setCreateTime(System.currentTimeMillis());
        transactionMapper.insert(transactionPo);
    }

    public TransactionPo selectOne(String txId, BigInteger height) {
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda()
                .eq(TransactionPo::getBlockHeight, height)
                .eq(TransactionPo::getTxId, txId).last("LIMIT 1");
        return transactionMapper.selectOne(wrapper);
    }

    public TransactionPo selectLastOne() {
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda().last("LIMIT 1");
        wrapper.orderByDesc(TransactionPo::getBlockHeight);
        return transactionMapper.selectOne(wrapper);
    }

    public List<TransactionPo> selectListByNonce() {
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda()
                .notBetween(TransactionPo::getNonce, 0, 0);
        return transactionMapper.selectList(wrapper);
    }

    public void delete(BigInteger id) {
        transactionMapper.deleteById(id);
    }

    public Integer countByDay(Long begin, Long end) {
        if (null == begin || null == end) {
            return 0;
        }
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda()
                .between(TransactionPo::getBlockTime, begin, end);
        return transactionMapper.selectCount(wrapper);
    }

    public IPage<TransactionPo> selectPage(Integer current, Integer size, String height, String tx) {
        if (null == current) {
            current = 1;
        }
        if (null == size) {
            size = 50;
        }
        Page<TransactionPo> page = new Page<>(current, size);
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda();
        if (!StringUtils.isBlank(height)) {
            wrapper.eq(TransactionPo::getBlockHeight, height);
        }
        if (!StringUtils.isBlank(tx)) {
            wrapper.eq(TransactionPo::getTxId, tx);
        }
        wrapper.orderByDesc(TransactionPo::getBlockHeight);
        return transactionMapper.selectPage(page, wrapper);
    }

    public IPage<TransactionPo> selectScanPage(int current, int begin, int end) {
        Page<TransactionPo> page = new Page<>(current, 500);
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda();
        wrapper.between(TransactionPo::getBlockHeight, begin, end);
        wrapper.orderByAsc(TransactionPo::getBlockHeight);
        return transactionMapper.selectPage(page, wrapper);
    }

    public List<TransactionPo> selectList(int begin, int end, int type) {
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda();
        if (-1 != type) {
            wrapper.eq(TransactionPo::getType, type);
        }
        wrapper.between(TransactionPo::getBlockHeight, begin, end);
        wrapper.orderByAsc(TransactionPo::getBlockHeight);
        return transactionMapper.selectList(wrapper);
    }

    public List<TransactionPo> selectListTime(int begin, int end, int type) {
        LambdaQueryWrapper<TransactionPo> wrapper = new QueryWrapper<TransactionPo>().lambda().select(TransactionPo::getBlockTime);
        if (-1 != type) {
            wrapper.eq(TransactionPo::getType, type);
        }
        wrapper.between(TransactionPo::getBlockHeight, begin, end);
        wrapper.orderByAsc(TransactionPo::getBlockHeight);
        return transactionMapper.selectList(wrapper);
    }
}
