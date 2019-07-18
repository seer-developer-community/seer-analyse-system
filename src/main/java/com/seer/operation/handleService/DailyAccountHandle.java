package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.AccountBasePo;
import com.seer.operation.entity.DailyAccountDetailsPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.SeerJsonRpcClient;
import com.seer.operation.rpcClient.operation.PredictionVo;
import com.seer.operation.rpcClient.response.GetAccount;
import com.seer.operation.service.AccountBaseService;
import com.seer.operation.service.DailyAccountService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.seer.operation.handleService.StaticFunHandle.addAssetAmount;
import static com.seer.operation.request.Constants.*;
import static com.seer.operation.utils.Times.formatDateByTimes;
import static com.seer.operation.utils.Times.getTimesDayZero;

@Service
public class DailyAccountHandle {
    @Value("${seer.rpc.ip}")
    private String rpcIp;
    @Value("${seer.rpc.port}")
    private String rpcPort;
    @Autowired
    private DailyAccountService dailyAccountService;
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DataCacheManager cacheManager;
    @Autowired
    private AccountBaseService baseService;

    @Transactional
    public void prediction(BigInteger block, Long time, PredictionVo predictionVo, FeeBo feeBo, JSONObject result, boolean isCheck) {
        //检查是否存在此issuer
        Long zero = getTimesDayZero(time);
        String date = formatDateByTimes(zero);
        String issuer = predictionVo.getIssuer();
        DailyAccountDetailsPo detailsPo = dailyAccountService.selectOneByIssuerToday(issuer, zero);
        String asset = result.getString("asset_id");
        String amountStr = result.getJSONArray("deltas").getJSONArray(0).getString(1);
        if (amountStr.startsWith("-")) {
            amountStr = amountStr.split("-")[1];
        }
        BigDecimal amount = new BigDecimal(amountStr).divide(SEER_DECIMALS)
                .setScale(5, BigDecimal.ROUND_HALF_DOWN);
        //不存在则插入
        if (null == detailsPo) {
            detailsPo = new DailyAccountDetailsPo();
            detailsPo.setZeroTimestamp(zero);
            detailsPo.setTime(date);
            detailsPo.setIssuer(issuer);
            detailsPo.setDailyPrtpCount(1);
            detailsPo.setLastBlock(block);
            //根据issuer获取用户名
            SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
            GetAccount account = client.getAccount(issuer);
            detailsPo.setName(account.getName());
            AccountBasePo basePo = baseService.selectIsBotById(issuer);
            if (null == basePo) {
                detailsPo.setIsBot(0);
            } else {
                detailsPo.setIsBot(basePo.getIsSeerbot());
            }
            //手续费
            if (feeBo.getIsSeer()) {
                detailsPo.setDailyFee(feeBo.getRealAmount());
            } else {
                detailsPo.setDailyFee(feeBo.getOtherAssetBaseSeer());
            }
            //投注额处理
            JSONArray array = initDailyAmount(asset, amount);
            detailsPo.setDailyPrtpAmount(array.toJSONString());
            detailsPo.setDailyProfit(new BigDecimal("2.5").multiply(BigDecimal.ONE));
            dailyAccountService.insert(detailsPo);
            return;
        }
        //上次统计的块高，防止重复统计同一笔交易
        if (isCheck) {
            if (detailsPo.getLastBlock().compareTo(block) >= 0) {
                return;
            }
        }
        //更新该issuer数据
        detailsPo.setDailyPrtpCount(detailsPo.getDailyPrtpCount() + 1);
        detailsPo.setLastBlock(block);
        detailsPo.setDailyProfit(detailsPo.getDailyProfit().add(new BigDecimal("2.5")));
        if (feeBo.getIsSeer()) {
            detailsPo.setDailyFee(detailsPo.getDailyFee().add(feeBo.getRealAmount()));
        } else {
            detailsPo.setDailyFee(detailsPo.getDailyFee().add(feeBo.getOtherAssetBaseSeer()));
        }
        //投注额处理
        JSONArray array = new JSONArray();
        String dailyPrtpAmount = detailsPo.getDailyPrtpAmount();
        if (StringUtils.isBlank(dailyPrtpAmount)) {
            array = initDailyAmount(asset, amount);
            detailsPo.setDailyPrtpAmount(array.toJSONString());
        } else {
            UserConfigPo userConfigPo = cacheManager.getConfigClient(USER_CONFIG_ID);
            if (null == userConfigPo) {
                throw new IllegalArgumentException("获取用户配置表失败！");
            }
            String assets = userConfigPo.getAssets();
            array = JSONArray.parseArray(dailyPrtpAmount);
            array = addAssetAmount(asset, assets, dailyPrtpAmount, amount);
            detailsPo.setDailyPrtpAmount(array.toJSONString());
        }
        dailyAccountService.updateById(detailsPo);
    }

    /**
     * 提供给非50的操作使用，统计用户每日手续费
     *
     * @param time
     * @param issuer
     * @param feeBo
     */
    @Transactional
    public void updateFee(BigInteger block, Long time, String issuer, FeeBo feeBo, boolean isCheck) {
        Long zero = getTimesDayZero(time);
        DailyAccountDetailsPo detailsPo = dailyAccountService.selectOneByIssuerToday(issuer, zero);
        if (null == detailsPo) {
            return;
        }
        if (isCheck) {
            if (block.compareTo(detailsPo.getLastBlock()) <= 0) {
                return;
            }
        }
        //因为只有用户参与预测了才统计，所以插入记录由50的操作执行，其他操作无需插入
        if (feeBo.getIsSeer()) {
            detailsPo.setDailyFee(detailsPo.getDailyFee());
        } else {
            detailsPo.setDailyFee(detailsPo.getDailyFee().add(feeBo.getOtherAssetBaseSeer()));
        }
        detailsPo.setLastBlock(block);
        dailyAccountService.updateById(detailsPo);

    }

    public List<String> getAssets() {
        UserConfigPo userConfigPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null == userConfigPo) {
            throw new IllegalArgumentException("获取用户配置表失败！");
        }
        String assets = userConfigPo.getAssets();
        String[] strings = assets.split(CONFIG_ASSET_SPLIT);
        return Arrays.asList(strings);
    }

    public JSONArray initDailyAmount(String asset, BigDecimal amount) {
        List<String> list = getAssets();
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject object = new JSONObject();
            object.put("asset", str);
            if (asset.equals(str)) {
                object.put("amount", amount);
            } else {
                object.put("amount", "0.00000");
            }
            array.add(object);
        }
        return array;
    }
}
