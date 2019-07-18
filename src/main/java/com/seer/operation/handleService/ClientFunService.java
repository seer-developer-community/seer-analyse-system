package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.SeerJsonRpcClient;
import com.seer.operation.rpcClient.response.GetAccount;
import com.seer.operation.rpcClient.response.GetAsset;
import com.seer.operation.rpcClient.response.GetGlobal;
import com.seer.operation.rpcClient.response.SeerRoom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.SEER_ASSET;
import static com.seer.operation.request.Constants.SEER_DECIMALS;

@Service
public class ClientFunService {
    @Value("${seer.rpc.ip}")
    private String rpcIp;
    @Value("${seer.rpc.port}")
    private String rpcPort;

    @Transactional
    public FeeBo getFee(String asset, BigInteger amount) {
        FeeBo feeBo = new FeeBo();
        feeBo.setAmount(amount);
        feeBo.setAssetId(asset);
        feeBo.setRealAmount(new BigDecimal(amount).divide(SEER_DECIMALS)
                .setScale(5, BigDecimal.ROUND_HALF_DOWN));
        if (SEER_ASSET.equals(asset)) {
            feeBo.setOtherAssetBaseSeer(BigDecimal.ZERO);
            feeBo.setIsSeer(true);
        } else {
            SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
            GetAsset getAsset = client.getAsset(asset);
            feeBo.setIsSeer(false);
            BigInteger rate = getAsset.getCoreExchangeRateBaseAmount();
            BigDecimal other = new BigDecimal(amount).divide(new BigDecimal(rate), 5, BigDecimal.ROUND_HALF_DOWN);
            other = other.divide(SEER_DECIMALS).setScale(5, BigDecimal.ROUND_HALF_DOWN);
            feeBo.setOtherAssetBaseSeer(other);
        }
        return feeBo;
    }

    @Transactional
    public SeerRoom getSeerRoom(String room) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        return client.getSeerRoom(room, 0, 0);
    }

    @Transactional
    public Integer getRoomType(String room) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        return client.getSeerRoom(room, 0, 0).getRoomType();
    }

    @Transactional
    public String getAccountRegistrar(String issuer) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        GetAccount account = client.getAccount(issuer);
        if (account == null) {
            return null;
        }
        return account.getRegistrar();
    }

    @Transactional
    public Long getAllowedWithdraw(String id) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        JSONArray array = client.getVestingBalances(id);
        Long amount = 0L;
        if (null == array) {
            return amount;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            JSONObject allowedWithdraw = object.getJSONObject("allowed_withdraw");
            amount = amount + allowedWithdraw.getLongValue("amount");
        }
        return amount;
    }

    @Transactional
    public String getSeerRoomOwner(String room) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        return client.getSeerRoom(room, 0, 0).getOwner();
    }

    @Transactional
    public BigInteger getBlockHeight() {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        return client.blockInfo().getHeadBlockNum();
    }

    @Transactional
    public Long getHouseByAccount(String account) {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        return client.getHouseByAccount(account).getCollectedFees();
    }

    @Transactional
    public BigDecimal getCreateRoomFee() {
        SeerJsonRpcClient client = new SeerJsonRpcClient(rpcIp, rpcPort);
        GetGlobal getGlobal = client.getGlobal();
        JSONObject object = getGlobal.getParameters().getCurrentFees();
        JSONArray parameters = object.getJSONArray("parameters");
        BigDecimal fee = parameters.getJSONArray(43).getJSONObject(1).getBigDecimal("fee");
        fee = fee.divide(SEER_DECIMALS).setScale(5);
        return fee;
    }
}
