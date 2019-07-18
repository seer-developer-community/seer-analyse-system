package com.seer.operation.handleService;

import com.seer.operation.entity.AccountBasePo;
import com.seer.operation.entity.DailyDataFaucetPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.TransferVo;
import com.seer.operation.service.*;
import com.seer.operation.utils.Times;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.SEER_ASSET;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;
import static com.seer.operation.utils.Times.DAY_TIMESTAMPS;

@Service
public class DailyDataFaucetHandle {
    @Autowired
    private ClientFunService clientFunService;
    @Autowired
    private DailyDataFaucetService faucetService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private AccountBaseService baseService;
    @Autowired
    private AccountRegisterService registerService;
    @Autowired
    private AccountHouseTypeService houseTypeService;
    @Autowired
    private AccountHouseTotalService houseTotalService;
    @Autowired
    private DataCacheManager cacheManager;

    /**
     * 更新水龙头账户系列的新增注册用户
     *
     * @param current
     * @param registrar
     */
    @Transactional
    public void updateDailyFaucetDailyRegistered(BigInteger current, String registrar, FeeBo feeBo, boolean isCheck, Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataFaucetPo faucetPo = faucetService.selectOneByTimestamp(time);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null == configPo) {
            return;
        }
        if (isCheck) {
            if (current.compareTo(faucetPo.getLastBlock()) <= 0) {
                return;
            }
        }
        if (null == faucetPo) {
            initDailyFaucet(time);
            faucetPo = faucetService.selectOneByTimestamp(time);
        }
        Integer registered = faucetPo.getDailyRegistered();//新增注册用户
        Integer totalRegistered = faucetPo.getTotalRegistered();//累计注册用户
        BigDecimal registerFee = faucetPo.getRegisteredFees();//DAPP注册用户支出
        BigDecimal totalRegisterFee = faucetPo.getTotalRegisteredFees();//累计DAPP注册用户支出
        if (configPo.getFaucetAccounts().contains(registrar)) {
            registered = registered + 1;
            totalRegistered = totalRegistered + 1;
            registerFee = registerFee.add(feeBo.getRealAmount());
            totalRegisterFee = totalRegisterFee.add(feeBo.getRealAmount());
        }
//        List<String> seerBots = Arrays.asList(configPo.getSeerBots());
//        if (registered >= seerBots.size()) {
//            registered = registered - seerBots.size();
//        }
        faucetPo = new DailyDataFaucetPo();
        faucetPo.setTimestamp(time);
        //DAPP注册用户支出
        faucetPo.setRegisteredFees(registerFee);
        faucetPo.setDailyRegistered(registered);
        faucetPo.setTotalRegisteredFees(totalRegisterFee);
        faucetPo.setTotalRegistered(totalRegistered);
        faucetPo.setLastBlock(current);
        faucetService.updateById(faucetPo);
    }

    @Transactional
    public void updateFaucetTrueAndPlayers(BigInteger current, Long blockTime, String issuer, String registrar, Long times, boolean isCheck, boolean checkBlock) {
        Long time = Times.getTimesDayZero(times);
        DailyDataFaucetPo faucetPo = faucetService.selectOneByTimestamp(time);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (isCheck && checkBlock) {
            if (current.compareTo(faucetPo.getLastBlock()) <= 0) {
                return;
            }
        }
        if (null == configPo) {
            return;
        }
        if (null == faucetPo) {
            initDailyFaucet(time);
            faucetPo = faucetService.selectOneByTimestamp(time);
        }
        Integer truePlayers = faucetPo.getDailyTrueplayers();//新增注册且投注用户数
        Integer players = faucetPo.getDailyPlayers();//新增投注用户
        Integer totalPlayers = faucetPo.getTotalPlayers();//累计投注用户
        AccountBasePo basePo = baseService.selectById(issuer);
        if (!configPo.getFaucetAccounts().contains(registrar) || configPo.getSeerBots().contains(issuer)) {
            return;
        }
        if (null != basePo) {
            Long nowTime = Times.getTimesDayZero(blockTime);
            if (nowTime - basePo.getRegTime() <= DAY_TIMESTAMPS) {
                truePlayers = truePlayers + 1;
            }
        }
        players = players + 1;
        totalPlayers = totalPlayers + 1;
        faucetPo = new DailyDataFaucetPo();
        faucetPo.setTimestamp(time);
        faucetPo.setDailyPlayers(players);
        faucetPo.setTotalPlayers(totalPlayers);
        faucetPo.setDailyTrueplayers(truePlayers);
        faucetPo.setLastBlock(current);
        faucetService.updateById(faucetPo);
    }

    /**
     * 充值笔数和充值金额
     * 转账笔数和转账金额
     *
     * @param transferVo
     * @param fromRegistrar
     * @param toRegistrar
     * @param current
     */
    @Transactional
    public void updateDailyDepositCount(TransferVo transferVo, String fromRegistrar, String toRegistrar, Long times, BigInteger current, FeeBo feeBo, boolean isCheck) {
        Long time = Times.getTimesDayZero(times);
        DailyDataFaucetPo faucetPo = faucetService.selectOneByTimestamp(time);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        String assets = SEER_ASSET;
        if (null != configPo) {
            assets = configPo.getAssets();
        }
        if (null == faucetPo) {
            initDailyFaucet(time);
            faucetPo = faucetService.selectOneByTimestamp(time);
        }
        if (isCheck) {
            if (current.compareTo(faucetPo.getLastBlock()) <= 0) {
                return;
            }
        }
        String depositCount = faucetPo.getDailyDepositCount(); //充值笔数
        String totalDepositCount = faucetPo.getTotalDepositCount(); //累计充值笔数
        String depositAmount = faucetPo.getDailyDepositAmount();//充值金额
        String totalDepositAmount = faucetPo.getTotalDepositAmount();//累计充值金额
        String transferCount = faucetPo.getDailyTransferCount();//转账笔数
        String totalTransferCount = faucetPo.getTotalTransferCount();//累计转账笔数
        String transferAmount = faucetPo.getDailyTransferAmount();//转账金额
        String totalTransferAmount = faucetPo.getTotalTransferAmount();//累计转账金额
        String transferCountFees = faucetPo.getTransferCountFees();//DAPP新注册用户转账支出
        String totalTransferCountFees = faucetPo.getTotalTransferCountFees();//累计DAPP新注册用户转账支出
        faucetPo = new DailyDataFaucetPo();
        faucetPo.setTimestamp(time);
        faucetPo.setLastBlock(current);
        //DAPP新注册用户转账支出
        if (configPo.getFaucetAccounts().contains(transferVo.getFrom())) {
            BigDecimal amount = BigDecimal.ZERO;
            if (feeBo.getIsSeer()) {
                amount = amount.add(feeBo.getRealAmount());
            } else {
                amount = amount.add(feeBo.getOtherAssetBaseSeer());
            }
            transferCountFees = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, transferCountFees
                    , transferVo.getAmountRealAmount()).toJSONString();
            transferCountFees = StaticFunHandle.addSeerAsset(transferCountFees, amount).toJSONString();
            totalTransferCountFees = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, totalTransferCountFees
                    , transferVo.getAmountRealAmount()).toJSONString();
            totalTransferCountFees = StaticFunHandle.addSeerAsset(totalTransferCountFees, amount).toJSONString();
            faucetPo.setTransferCountFees(transferCountFees);
            faucetPo.setTotalTransferCountFees(totalTransferCountFees);
        }
        //充值
//        if (configPo.getFaucetAccounts().contains(toRegistrar)) {
        if (configPo.getGateways().contains(transferVo.getFrom()) && !configPo.getSeerBots().contains(transferVo.getFrom())) {
            depositCount = StaticFunHandle.addAssetCount(depositCount, transferVo.getAmountAssetId(), assets).toJSONString();
            totalDepositCount = StaticFunHandle.addAssetCount(totalDepositCount, transferVo.getAmountAssetId(), assets).toJSONString();
            depositAmount = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, depositAmount,
                    transferVo.getAmountRealAmount()).toJSONString();
            totalDepositAmount = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, totalDepositAmount,
                    transferVo.getAmountRealAmount()).toJSONString();
            faucetPo.setDailyDepositCount(depositCount);
            faucetPo.setTotalDepositCount(totalDepositCount);
            faucetPo.setDailyDepositAmount(depositAmount);
            faucetPo.setTotalDepositAmount(totalDepositAmount);
        }
//        }
        //转账
        if (configPo.getFaucetAccounts().contains(fromRegistrar)) {
            if (!configPo.getSeerBots().equals(transferVo.getFrom())) {
                transferAmount = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, transferAmount
                        , transferVo.getAmountRealAmount()).toJSONString();
                totalTransferAmount = StaticFunHandle.addAssetAmount(transferVo.getAmountAssetId(), assets, totalTransferAmount
                        , transferVo.getAmountRealAmount()).toJSONString();
                transferCount = StaticFunHandle.addAssetCount(transferCount, transferVo.getAmountAssetId(), assets).toJSONString();
                totalTransferCount = StaticFunHandle.addAssetCount(totalTransferCount, transferVo.getAmountAssetId(), assets).toJSONString();
                faucetPo.setDailyTransferAmount(transferAmount);
                faucetPo.setTotalTransferAmount(totalTransferAmount);
                faucetPo.setDailyTransferCount(transferCount);
                faucetPo.setTotalTransferCount(totalTransferCount);
            }
        }
        faucetService.updateById(faucetPo);
    }

    /**
     * 更新水龙头账户系列的活跃投注用户
     *
     * @param current
     * @param issuer
     * @param registrar
     */
    @Transactional
    public void updateDailyFaucetActivePlayer(BigInteger current, String issuer, String registrar, Long times, boolean isCheck) {
        Long time = Times.getTimesDayZero(times);
        DailyDataFaucetPo faucetPo = faucetService.selectOneByTimestamp(time);
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (isCheck) {
            if (current.compareTo(faucetPo.getLastBlock()) <= 0) {
                return;
            }
        }
        if (null == configPo) {
            return;
        }
        if (null == faucetPo) {
            initDailyFaucet(time);
            faucetPo = faucetService.selectOneByTimestamp(time);
        }
        //判断registrar是否为水龙头账户，且issuer不为机器人
        if (!configPo.getFaucetAccounts().contains(registrar) || configPo.getSeerBots().contains(issuer)) {
            return;
        }
        Integer activePlayer = faucetPo.getTotalActivePlayer();
        faucetPo = new DailyDataFaucetPo();
        faucetPo.setTimestamp(time);
        faucetPo.setLastBlock(current);
        faucetPo.setTotalActivePlayer(activePlayer + 1);
        faucetService.updateById(faucetPo);
    }

    /**
     * 初始化每日水龙头用户系列表，0点插入一条记录
     */
    @Transactional
    public void initDailyFaucet(Long times) {
        Long time = Times.getTimesDayZero(times);
        DailyDataFaucetPo faucetPo = faucetService.selectOneByTimestamp(time);
        if (null != faucetPo) {
            return;
        }
        String assets = SEER_ASSET;
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            assets = configPo.getAssets();
        }
        DailyDataFaucetPo last = faucetService.selectLastOne();
        faucetPo = new DailyDataFaucetPo();
        faucetPo.setTimestamp(time);
        faucetPo.setTime(Times.formatDateByTimes(time));
        faucetPo.setDailyRegistered(0);
        faucetPo.setTotalRegistered(0);//累计注册用户
        faucetPo.setDailyTrueplayers(0);
        faucetPo.setDailyPlayers(0);
        faucetPo.setTotalPlayers(0);//累计投注用户
        faucetPo.setTotalActivePlayer(0);
        faucetPo.setDailyDepositCount(StaticFunHandle.initAssetCount(assets).toJSONString());
        faucetPo.setTotalTransferCountFees(StaticFunHandle.initAssetAmount(assets).toJSONString());//累计DAPP新注册用户转账支出
        faucetPo.setTotalDepositCount(StaticFunHandle.initAssetCount(assets).toJSONString());//累计充值笔数
        faucetPo.setDailyDepositAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        faucetPo.setTotalDepositAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());//累计充值额
        faucetPo.setDailyTransferCount(StaticFunHandle.initAssetCount(assets).toJSONString());
        faucetPo.setTotalTransferCount(StaticFunHandle.initAssetCount(assets).toJSONString());//累计转账笔数
        faucetPo.setDailyTransferAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());
        faucetPo.setTotalTransferAmount(StaticFunHandle.initAssetAmount(assets).toJSONString());//累计转账额
        faucetPo.setTotalRegisteredFees(BigDecimal.ZERO.setScale(5));//累计DAPP注册用户支出
        faucetPo.setRegisteredFees(BigDecimal.ZERO.setScale(5));
        faucetPo.setTransferCountFees(StaticFunHandle.initAssetAmount(assets).toJSONString());
        faucetPo.setLastBlock(BigInteger.ZERO);
        faucetPo.setTotalFaucetProfit(BigDecimal.ZERO.setScale(5));//累计水龙头收入
        faucetPo.setFaucetProfit(BigDecimal.ZERO.setScale(5));
        faucetService.insert(faucetPo);
        //更新累计系列 = 今天 + 昨天的值
        if (null != last) {
            faucetPo.setTotalRegistered(last.getTotalRegistered() + faucetPo.getTotalRegistered());
            faucetPo.setTotalPlayers(last.getTotalPlayers() + faucetPo.getTotalPlayers());
            faucetPo.setTotalTransferCountFees(StaticFunHandle.addAssetsAmount(configPo.getAssets(), last.getTotalTransferCountFees(), faucetPo.getTotalTransferCountFees()));
            faucetPo.setTotalDepositCount(StaticFunHandle.addAssetsCount(configPo.getAssets(), last.getTotalDepositCount(), faucetPo.getTotalDepositCount()));
            faucetPo.setTotalDepositAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), last.getTotalDepositAmount(), faucetPo.getTotalDepositAmount()));
            faucetPo.setTotalTransferCount(StaticFunHandle.addAssetsCount(configPo.getAssets(), last.getTotalTransferCount(), faucetPo.getTotalTransferCount()));
            faucetPo.setTotalTransferAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), last.getTotalTransferAmount(), faucetPo.getTotalTransferAmount()));
            faucetPo.setTotalRegisteredFees(last.getTotalRegisteredFees().add(faucetPo.getTotalRegisteredFees()));
            faucetPo.setTotalFaucetProfit(last.getTotalFaucetProfit().add(faucetPo.getTotalFaucetProfit()));
            faucetService.updateById(faucetPo);
        }
    }

}
