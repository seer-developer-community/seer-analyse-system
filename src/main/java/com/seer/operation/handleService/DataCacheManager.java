package com.seer.operation.handleService;

import com.seer.operation.entity.DailyDataFaucetPo;
import com.seer.operation.entity.DailyDataHousesPo;
import com.seer.operation.entity.DailyDataTotalPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.service.DailyDataFaucetService;
import com.seer.operation.service.DailyDataHousesService;
import com.seer.operation.service.DailyDataTotalService;
import com.seer.operation.service.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataCacheManager {
    @Autowired
    private UserConfigService configService;
    @Autowired
    private DailyDataFaucetService dataFaucetService;
    @Autowired
    private DailyDataHousesService dataHousesService;
    @Autowired
    private DailyDataTotalService dataTotalService;

    private ConcurrentHashMap<Integer, UserConfigPo> configMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, DailyDataFaucetPo> dataFaucetMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, DailyDataHousesPo> dataHousesMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, DailyDataTotalPo> dataTotalMap = new ConcurrentHashMap<>();

    public UserConfigPo getConfigClient(Integer id) {
        if (configMap.containsKey(id)) {
            return configMap.get(id);
        }
        UserConfigPo client = configService.selectOne(id);
        if (null != client) {
            configMap.put(id, client);
        }
        return client;
    }

    public void updateConfigCache(Integer id) {
        if (configMap.containsKey(id)) {
            configMap.remove(id);
        }
        UserConfigPo client = configService.selectOne(id);
        if (null != client) {
            configMap.put(id, client);
        }
    }

    public DailyDataFaucetPo getFaucetClient(Long times) {
        if (configMap.containsKey(times)) {
            return dataFaucetMap.get(times);
        }
        DailyDataFaucetPo faucetPo = dataFaucetService.selectOneByTimestamp(times);
        if (null != faucetPo) {
            dataFaucetMap.put(times, faucetPo);
        }
        return faucetPo;
    }

    public DailyDataHousesPo getDataHousesClient(Long times) {
        if (configMap.containsKey(times)) {
            return dataHousesMap.get(times);
        }
        DailyDataHousesPo housesPo = dataHousesService.selectOneByTimestamp(times);
        if (null != housesPo) {
            dataHousesMap.put(times, housesPo);
        }
        return housesPo;
    }

    public DailyDataTotalPo getDataTotalClient(Long times) {
        if (configMap.containsKey(times)) {
            return dataTotalMap.get(times);
        }
        DailyDataTotalPo totalPo = dataTotalService.selectOneByTimestamp(times);
        if (null != totalPo) {
            dataTotalMap.put(times, totalPo);
        }
        return totalPo;
    }

}
