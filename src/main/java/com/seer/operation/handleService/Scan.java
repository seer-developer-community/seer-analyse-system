package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.BlockPo;
import com.seer.operation.entity.TransactionPo;
import com.seer.operation.rpcClient.SeerJsonRpcClient;
import com.seer.operation.rpcClient.response.Block;
import com.seer.operation.rpcClient.response.BlockInfo;
import com.seer.operation.rpcClient.response.Transactions;
import com.seer.operation.service.BlockService;
import com.seer.operation.service.BlockSyncService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.utils.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

import static com.seer.operation.request.STATUS.SAVE_BLOCK;
import static com.seer.operation.request.STATUS.SAVE_TX;

/**
 * @author zhang sir
 * @date 2019-5-19
 */
@Service
public class Scan {

    @Value("${seer.rpc.ip}")
    private String rpcIp;
    @Value("${seer.rpc.port}")
    private String rpcPort;

    @Autowired
    private OperateHandle operateHandle;
    @Autowired
    private BlockService blockService;
    @Autowired
    private BlockSyncService blockSyncService;
    @Autowired
    private TransactionService transactionService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public Boolean scanFromBlockChain(Integer height, Integer saveBlock, Integer saveTx) {
        if (null == height) {
            throw new IllegalArgumentException("参数为空！");
        }
        BigInteger current = BigInteger.valueOf(height);
        //获取最新块高
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        BlockInfo blockInfo = client.blockInfo();
        if (null == blockInfo) {
            logger.error("获取最新块高异常");
            return false;
        }
        BigInteger lastBlock = blockInfo.getHeadBlockNum();
        if (current.compareTo(lastBlock) > 0) {
//            logger.info("已同步至最新区块，当前块高：{}", lastBlock);
            return false;
        }
        //获取块信息
        Block block = client.getBlock(current.toString());
        if (null == block) {
            logger.error("获取块信息异常");
            return false;
        }
        //储存区块信息
        if (SAVE_BLOCK.getCode() == saveBlock) {
            saveBlock(current, block);
        }
        List<Transactions> list = block.getTransactions();
        //获取交易并储存
        if (SAVE_TX.getCode() == saveTx) {
            Long times = Times.formatTDateToEastTimes(block.getTimestamp());
            for (int i = 0; i < list.size(); i++) {
                Transactions transactions = list.get(i);
                String txId = block.getTransactionIds().get(i);
                saveTransaction(current, txId, transactions, times);
            }
        }
        String time = block.getTimestamp();
        boolean isCheck = false;
        //对交易进行数据分析
        for (int k = 0; k < list.size(); k++) {
            Transactions transactions = list.get(k);
            List<JSONArray> arrayList = transactions.getOperations();
            List<JSONArray> results = transactions.getOperationResults();
            for (int i = 0; i < arrayList.size(); i++) {
                if ((k == list.size() - 1) && (i == arrayList.size() - 1)) {
                    isCheck = true;
                }
                Integer type = arrayList.get(i).getInteger(0);
                JSONObject jsonObject = arrayList.get(i).getJSONObject(1);
                JSONArray result = results.get(i);
                operateHandle.handleType(current, Times.formatTDateToEastTimes(time), type, jsonObject, result, isCheck);
            }
        }
        //更新同步记录
        blockSyncService.updateBlockSync();
        return true;
    }

    @Transactional
    public void scanBlock(BigInteger begin, BigInteger end) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        BigInteger current = begin;
        Long t = System.currentTimeMillis();
        logger.info("begin={},end={}开始扫描", begin, end);
        int txs = 0;
        while (current.compareTo(end) <= 0) {
            Long a = System.currentTimeMillis();
            Block block = client.getBlock(current.toString());
            if (null == block) {
                logger.error("{}获取块信息异常", current);
                break;
            }
            List<Transactions> list = block.getTransactions();
            //获取交易并储存
            Long times = Times.formatTDateToEastTimes(block.getTimestamp());
            for (int i = 0; i < list.size(); i++) {
                Transactions transactions = list.get(i);
                String txId = block.getTransactionIds().get(i);
                saveTransaction(current, txId, transactions, times);
                txs++;
            }
//            logger.info("{}扫描完成，耗时：{} ms", current, (System.currentTimeMillis() - a));
            current = current.add(BigInteger.ONE);
        }
        logger.info("{}-{}扫描完成，交易总数:{},耗时：{} ms", begin, current.subtract(BigInteger.ONE), txs, System.currentTimeMillis() - t);
    }

    /**
     * 储存区块信息
     *
     * @param height
     * @param block
     */
    @Transactional
    public void saveBlock(BigInteger height, Block block) {
        BlockPo blockPo = blockService.selectById(height);
        if (null != blockPo) {
            logger.warn("区块：{} 已存在", height);
            return;
        }
        blockPo = new BlockPo();
        blockPo.setId(height);
        blockPo.setPrevious(block.getPrevious());
        blockPo.setTimestamp(block.getTimestamp());
        Long eastTimestamp = Times.formatTDateToEastTimes(block.getTimestamp());
        if (null == eastTimestamp) {
            blockPo.setEastEightTimestamp(block.getTimestamp() + "+08:00:00");
        } else {
            blockPo.setEastEightTimestamp(eastTimestamp.toString());
        }
        blockPo.setWitness(block.getWitness());
        blockPo.setMerkleRoot(block.getTransactionMerkleRoot());
        blockPo.setExtensions(block.getExtensions().toString());
        blockPo.setWitnessSignature(block.getWitnessSignature());
        blockPo.setTransactionIds(block.getTransactionIds().toString());
        blockPo.setBlockId(block.getBlockId());
        blockPo.setSigningKey(block.getSigningKey());
        blockPo.setTxsCount(block.getTxsCount());
        blockPo.setCreateTime(System.currentTimeMillis());
        blockService.insert(blockPo);
    }

    @Transactional
    public void saveTransaction(BigInteger height, String txId, Transactions transactions, Long times) {
        TransactionPo po = transactionService.selectOne(txId, height);
        if (null != po) {
//            logger.warn("区块：{} 中的交易：{} 已存在", height, txId);
            return;
        }
        TransactionPo transactionPo = new TransactionPo();
        transactionPo.setBlockHeight(height);
        transactionPo.setTxId(txId);
        transactionPo.setBlockTime(times);
        transactionPo.setRefBlockNum(transactions.getRefBlockNum());
        transactionPo.setRefBlockPrefix(transactions.getRefBlockPrefix().toString());
        transactionPo.setExpiration(transactions.getExpiration());
        transactionPo.setExtensions(transactions.getExtensions().toString());
        transactionPo.setSignatures(transactions.getSignatures().toString());
        transactionPo.setOperationResults(transactions.getOperationResults().toString());
        List<JSONArray> array = transactions.getOperations();
        for (int i = 0; i < array.size(); i++) {
            transactionPo.setType(array.get(i).getInteger(0));
            transactionPo.setOperations(array.get(i).toJSONString());
            transactionPo.setNonce(i);
            transactionPo.setOperationResults(transactions.getOperationResults().get(i).toJSONString());
            transactionService.insert(transactionPo);
        }
    }
}
