package com.seer.operation.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seer.operation.entity.TotalRoomPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.response.ResponseVo;
import com.seer.operation.service.TotalRoomService;
import com.seer.operation.service.UserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static com.seer.operation.request.Constants.CONFIG_ASSET_SPLIT;
import static com.seer.operation.request.Constants.USER_CONFIG_ID;

@RestController
@RequestMapping(value = "seer/total/room")
public class TotalRoomController {
    @Autowired
    private TotalRoomService totalRoomService;
    @Autowired
    private UserConfigService userConfigService;

    @RequestMapping(value = "page")
    public ResponseVo selectPage(Integer current, Integer size, String room, String asset) {
        IPage<TotalRoomPo> page = totalRoomService.selectPage(current, size, room);
        ResponseVo responseVo = ResponseVo.ResultSuccess();
        JSONObject object = new JSONObject();
        object.put("total", page.getTotal());
        object.put("size", page.getSize());
        object.put("current", page.getCurrent());
        object.put("pages", page.getPages());
        JSONArray newList = new JSONArray();
        //前端动态加载表头
        JSONArray colsArray = new JSONArray();
        UserConfigPo configPo = userConfigService.selectOne(USER_CONFIG_ID);
        if (null != configPo && StringUtils.isNotBlank(configPo.getAssets())) {
            List<String> strings = Arrays.asList(configPo.getAssets().split(CONFIG_ASSET_SPLIT));
            for (String str : strings) {
                JSONObject object1 = new JSONObject();
                object1.put("name", str);
                colsArray.add(object1);
            }
        }
        object.put("cols", colsArray);
        //返回显示的资产
        if (StringUtils.isBlank(asset)) {
            object.put("asset", configPo.getDefaultAsset());
            asset = configPo.getDefaultAsset();
        } else {
            object.put("asset", asset);
        }
        //处理投注额分列显示
        List<TotalRoomPo> list = page.getRecords();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
//            JSONArray array = JSON.parseArray(list.get(i).getTotalBotPlayAmount());
            TotalRoomPo totalRoomPo = list.get(i);
            jsonObject.put("room", totalRoomPo.getRoom());
            jsonObject.put("updateTime", totalRoomPo.getUpdateTime());
            jsonObject.put("createTime", totalRoomPo.getCreateTime());
            jsonObject.put("showAsset", StaticFunHandle.getAssetAmount(totalRoomPo.getTotalBotPlayAmount(), asset));
//            for (int k = 0; k < colsArray.size(); k++) {
//                boolean isNotExist = true;
//                boolean isNotExistAsset = true;
//                for (int j = 0; j < array.size(); j++) {
//                    //返回当前指定显示资产
//                    if (asset.equals(array.getJSONObject(j).getString("asset"))) {
//                        jsonObject.put("showAsset", array.getJSONObject(j).getBigDecimal("amount"));
//                        isNotExistAsset = false;
//                    }
//                    if (colsArray.getJSONObject(k).getString("name").equals(
//                            array.getJSONObject(j).getString("asset")
//                    )) {
//                        isNotExist = false;
//                    }
//                    jsonObject.put(array.getJSONObject(j).getString("asset"),
//                            array.getJSONObject(j).getString("amount"));
//                }
//                if (isNotExist) {
//                    jsonObject.put(colsArray.getJSONObject(k).getString("name"), "0.00000");
//                }
//                if (isNotExistAsset) {
//                    jsonObject.put("showAsset", 0);
//                }
//            }
            newList.add(jsonObject);
        }
        object.put("records", newList);
        responseVo.setData(object);
        return responseVo;
    }

}
