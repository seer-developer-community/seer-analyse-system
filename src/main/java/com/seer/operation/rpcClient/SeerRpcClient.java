package com.seer.operation.rpcClient;

import com.alibaba.fastjson.JSONArray;
import com.seer.operation.rpcClient.response.*;

public interface SeerRpcClient {
    BlockInfo blockInfo();

    Block getBlock(String var1);

    GetAsset getAsset(String var1);

    GetAccount getAccount(String var1);

    GetGlobal getGlobal();

    SeerRoom getSeerRoom(String roomId, int start, int limit);

    JSONArray getVestingBalances(String id);

    HouseByAccount getHouseByAccount(String account);

}
