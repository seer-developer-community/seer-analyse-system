package com.seer.operation.handleService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.seer.operation.request.Constants.*;

public class StaticFunHandle {
    /**
     * 增加资产
     *
     * @param asset    当前资产
     * @param assets   资产配置
     * @param oldTotal 原有资产
     * @param amount   金额
     * @return
     */
    public static JSONArray addAssetAmount(String asset, String assets, String oldTotal, BigDecimal amount) {
        JSONArray oldTotalArray = JSONArray.parseArray(oldTotal);
        List<String> assetsList = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        for (String string : assetsList) {
            boolean isExits = false;
            if (asset.equals(string)) {
                for (int i = 0; i < oldTotalArray.size(); i++) {
                    JSONObject object = oldTotalArray.getJSONObject(i);
                    if (asset.equals(object.getString("asset"))) {
                        BigDecimal oldAmount = object.getBigDecimal("amount");
                        object.replace("amount", oldAmount.add(amount).toString());
                        oldTotalArray.set(i, object);
                        isExits = true;
                        break;
                    }
                }
                if (!isExits) {
                    JSONObject object = new JSONObject();
                    object.put("asset", asset);
                    object.put("amount", amount);
                    oldTotalArray.add(object);
                }
                break;
            }
        }
        return oldTotalArray;
    }

    public static String subAssetAmount(String asset, String oldTotal, BigDecimal amount) {
        JSONArray oldTotalArray = JSONArray.parseArray(oldTotal);
        for (int i = 0; i < oldTotalArray.size(); i++) {
            JSONObject object = oldTotalArray.getJSONObject(i);
            if (asset.equals(object.getString("asset"))) {
                BigDecimal oldAmount = object.getBigDecimal("amount");
                if (oldAmount.compareTo(amount) >= 0) {
                    object.replace("amount", oldAmount.subtract(amount).toString());
                }
                oldTotalArray.set(i, object);
                break;
            }
        }
        return oldTotalArray.toJSONString();
    }

    public static JSONArray addAssetCount(String oldTotal, String asset, String assets) {
        JSONArray oldTotalArray = JSONArray.parseArray(oldTotal);
        List<String> assetsList = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        for (String string : assetsList) {
            boolean isExits = false;
            if (asset.equals(string)) {
                for (int i = 0; i < oldTotalArray.size(); i++) {
                    JSONObject object = oldTotalArray.getJSONObject(i);
                    if (asset.equals(object.getString("asset"))) {
                        Integer oldCount = object.getInteger("count");
                        object.replace("count", String.valueOf(oldCount + 1));
                        oldTotalArray.set(i, object);
                        isExits = true;
                        break;
                    }
                }
                if (!isExits) {
                    JSONObject object = new JSONObject();
                    object.put("asset", asset);
                    object.put("count", 1);
                    oldTotalArray.add(object);
                }
                break;
            }
        }
        return oldTotalArray;
    }

    public static JSONArray addSeerAsset(String oldTotal, BigDecimal amount) {
        JSONArray oldTotalArray = JSONArray.parseArray(oldTotal);
        for (int i = 0; i < oldTotalArray.size(); i++) {
            JSONObject object = oldTotalArray.getJSONObject(i);
            if (SEER_ASSET.equals(object.getString("asset"))) {
                BigDecimal oldAmount = object.getBigDecimal("amount");
                object.replace("amount", oldAmount.add(amount).toString());
                oldTotalArray.set(i, object);
                break;
            }
        }
        return oldTotalArray;
    }

    public static JSONArray initAssetAmount(String assets) {
        List<String> list = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject object = new JSONObject();
            object.put("asset", str);
            object.put("amount", "0.00000");
            array.add(object);
        }
        return array;
    }

    public static JSONArray initAssetCount(String assets) {
        List<String> list = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject object = new JSONObject();
            object.put("asset", str);
            object.put("count", "0");
            array.add(object);
        }
        return array;
    }

    public static String initHouseRate(String houses) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject object = new JSONObject();
            object.put("house", str);
            object.put("rate", "0.00");
            array.add(object);
        }
        return array.toJSONString();
    }

    public static String initHouseCount(String houses) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject object = new JSONObject();
            object.put("house", str);
            object.put("count", "0");
            array.add(object);
        }
        return array.toJSONString();
    }

    public static String updateHousesRate(String houses, String activeRoomsList, String roomsList) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray activeRooms = JSON.parseArray(activeRoomsList);
        JSONArray rooms = JSON.parseArray(roomsList);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String house = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", house);
            BigDecimal rate = BigDecimal.ZERO.setScale(2);
            Long activeRoom = 0L;
            Long room = 0L;
            //获取真人参与房间数
            for (int j = 0; j < activeRooms.size(); j++) {
                JSONObject object = activeRooms.getJSONObject(j);
                if (house.equals(object.getString("house"))) {
                    activeRoom = object.getLongValue("count");
                    break;
                }
            }
            //获取房间数
            for (int k = 0; k < rooms.size(); k++) {
                JSONObject object = rooms.getJSONObject(k);
                if (house.equals(object.getString("house"))) {
                    room = object.getLongValue("count");
                    break;
                }
            }
            if (room != 0) {
                rate = new BigDecimal(activeRoom).divide(new BigDecimal(room), 2, BigDecimal.ROUND_HALF_DOWN);
            }
            jsonObject.put("rate", rate.toString());
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String updateOneHousesRate(String house, String old, int activeRoomsSize, int roomsSize) {
        JSONArray oldArray = JSON.parseArray(old);
        for (int i = 0; i < oldArray.size(); i++) {
            JSONObject object = oldArray.getJSONObject(i);
            if (object.getString("house").equals(house)) {
                if (roomsSize == 0) {
                    object.replace("rate", "0.00");
                    oldArray.set(i, object);
                    break;
                }
                BigDecimal rate = new BigDecimal(activeRoomsSize).divide(new BigDecimal(roomsSize), 2, BigDecimal.ROUND_HALF_DOWN);
                object.replace("rate", rate.toString());
                oldArray.set(i, object);
                break;
            }
        }
        return oldArray.toJSONString();
    }

    public static String addHouseCount(String houses, String old, String beHouse, Long beCount) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray array = JSON.parseArray(old);
        for (int i = 0; i < list.size(); i++) {
            boolean isExits = false;
            String house = list.get(i);
            if (house.equals(beHouse)) {
                for (int j = 0; j < array.size(); j++) {
                    JSONObject object = array.getJSONObject(j);
                    if (beHouse.equals(object.getString("house"))) {
                        Long count = object.getLongValue("count");
                        object.replace("count", String.valueOf(count + beCount));
                        array.set(j, object);
                        isExits = true;
                        break;
                    }
                }
                if (!isExits) {
                    JSONObject object = new JSONObject();
                    object.put("house", house);
                    object.put("count", "0");
                    array.add(object);
                }
                break;
            }
        }
        return array.toJSONString();
    }

    public static boolean isContainRoom(String list, String owner, String room) {
        JSONArray array = JSON.parseArray(list);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("house").equals(owner)) {
                if (object.getString("list").contains(room)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String addRoomList(String houses, String oldList, String room, String house) {
        List<String> list = Arrays.asList(houses.split(STRING_SPLIT_CHAR));
        JSONArray array = JSON.parseArray(oldList);
        JSONArray jsonArray = new JSONArray();
        for (String str : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", str);
            String roomList = "";
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("house").equals(str)) {
                    roomList = object.getString("list");
                    break;
                }
            }
            if (str.equals(house)) {
                if (StringUtils.isBlank(roomList)) {
                    roomList = room;
                } else if (!roomList.contains(room)) {
                    roomList = roomList + STRING_SPLIT_CHAR + room;
                }
            }
            jsonObject.put("list", roomList);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    public static String addTotalRoomList(String houses, String prevList, String curList) {
        List<String> list = Arrays.asList(houses.split(STRING_SPLIT_CHAR));
        JSONArray prev = JSON.parseArray(prevList);
        JSONArray cur = JSON.parseArray(curList);
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", str);
            String roomList = "";
            if (null != cur) {
                for (int i = 0; i < cur.size(); i++) {
                    JSONObject object = cur.getJSONObject(i);
                    if (object.getString("house").equals(str)) {
                        roomList = object.getString("list");
                        break;
                    }
                }
            }
            for (int i = 0; i < prev.size(); i++) {
                JSONObject object = prev.getJSONObject(i);
                if (object.getString("house").equals(str)) {
                    List<String> stringList = Arrays.asList(object.getString("list").split(STRING_SPLIT_CHAR));
                    for (String s : stringList) {
                        if (StringUtils.isBlank(roomList)) {
                            roomList = s;
                        } else if (!roomList.contains(s)) {
                            roomList = roomList + STRING_SPLIT_CHAR + s;
                        }
                    }
                    break;
                }
            }
            jsonObject.put("list", roomList);
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String initRoomsList(String room, String houses) {
        JSONArray array = new JSONArray();
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        for (String house : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", house);
            jsonObject.put("list", room);
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String addActiveRoom(String list, String owner, String room) {
        JSONArray array = JSON.parseArray(list);
        boolean isExist = false;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("house").equals(owner)) {
                if (StringUtils.isBlank(object.getString("list"))) {
                    object.replace("list", room);
                } else {
                    object.replace("list", object.getString("list") + STRING_SPLIT_CHAR + room);
                }
                array.set(i, object);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            JSONObject object = new JSONObject();
            object.put("house", owner);
            object.put("list", room);
            array.add(object);
        }
        return array.toJSONString();
    }

    public static String addHousesCount(String houses, String addList, String addendList) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray add = JSON.parseArray(addList);
        JSONArray addend = JSON.parseArray(addendList);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String house = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", house);
            Long count = 0L;
            //获取加列表对应房间数
            for (int j = 0; j < add.size(); j++) {
                JSONObject object = add.getJSONObject(j);
                if (house.equals(object.getString("house"))) {
                    count = count + object.getLongValue("count");
                    break;
                }
            }
            //获取被加列表对应房间数
            for (int k = 0; k < addend.size(); k++) {
                JSONObject object = addend.getJSONObject(k);
                if (house.equals(object.getString("house"))) {
                    count = count + object.getLongValue("count");
                    break;
                }
            }
            jsonObject.put("count", String.valueOf(count));
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String subHousesCount(String houses, String housesSub, String housesBeSub) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray sub = JSON.parseArray(housesSub);
        JSONArray beSub = JSON.parseArray(housesBeSub);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String house = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", house);
            Long count = 0L;
            //获取减数列表对应资产笔数
            for (int j = 0; j < sub.size(); j++) {
                JSONObject object = sub.getJSONObject(j);
                if (house.equals(object.getString("house"))) {
                    count = count + object.getLongValue("count");
                    break;
                }
            }
            //获取被减列表对应资产笔数
            for (int k = 0; k < beSub.size(); k++) {
                JSONObject object = beSub.getJSONObject(k);
                if (house.equals(object.getString("house"))) {
                    if (count - object.getLongValue("count") >= 0) {
                        count = count - object.getLongValue("count");
                    }
                    break;
                }
            }
            jsonObject.put("count", String.valueOf(count));
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String addAssetsAmount(String assets, String assetsList, String assetsAddend) {
        List<String> list = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        JSONArray add = JSON.parseArray(assetsList);
        JSONArray addend = JSON.parseArray(assetsAddend);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String asset = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("asset", asset);
            BigDecimal amount = BigDecimal.ZERO;
            //获取加列表对应资产值
            for (int j = 0; j < add.size(); j++) {
                JSONObject object = add.getJSONObject(j);
                if (asset.equals(object.getString("asset"))) {
                    amount = amount.add(object.getBigDecimal("amount"));
                    break;
                }
            }
            //获取被加列表对应资产值
            for (int k = 0; k < addend.size(); k++) {
                JSONObject object = addend.getJSONObject(k);
                if (asset.equals(object.getString("asset"))) {
                    amount = amount.add(object.getBigDecimal("amount"));
                    break;
                }
            }
            jsonObject.put("amount", amount.toString());
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String subAssetsAmount(String assets, String assetsSub, String assetsBeSub) {
        List<String> list = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        JSONArray sub = JSON.parseArray(assetsSub);
        JSONArray beSub = JSON.parseArray(assetsBeSub);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String asset = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("asset", asset);
            BigDecimal amount = BigDecimal.ZERO.setScale(5);
            //获取减数列表对应资产值
            for (int j = 0; j < sub.size(); j++) {
                JSONObject object = sub.getJSONObject(j);
                if (asset.equals(object.getString("asset"))) {
                    amount = amount.add(object.getBigDecimal("amount"));
                    break;
                }
            }
            //获取被减列表对应资产值
            for (int k = 0; k < beSub.size(); k++) {
                JSONObject object = beSub.getJSONObject(k);
                if (asset.equals(object.getString("asset"))) {
                    if (amount.compareTo(object.getBigDecimal("amount")) >= 0) {
                        amount = amount.subtract(object.getBigDecimal("amount"));
                    }
                    break;
                }
            }
            jsonObject.put("amount", amount);
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String addAssetsCount(String assets, String assetsList, String assetsAddend) {
        List<String> list = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        JSONArray add = JSON.parseArray(assetsList);
        JSONArray addend = JSON.parseArray(assetsAddend);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String asset = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("asset", asset);
            Long count = 0L;
            //获取加列表对应资产笔数
            for (int j = 0; j < add.size(); j++) {
                JSONObject object = add.getJSONObject(j);
                if (asset.equals(object.getString("asset"))) {
                    count = count + object.getLongValue("count");
                    break;
                }
            }
            //获取被加列表对应资产笔数
            for (int k = 0; k < addend.size(); k++) {
                JSONObject object = addend.getJSONObject(k);
                if (asset.equals(object.getString("asset"))) {
                    count = count + object.getLongValue("count");
                    break;
                }
            }
            jsonObject.put("count", String.valueOf(count));
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static String subAssetsCount(String assets, String assetsSub, String assetsBeSub) {
        List<String> list = Arrays.asList(assets.split(CONFIG_ASSET_SPLIT));
        JSONArray sub = JSON.parseArray(assetsSub);
        JSONArray beSub = JSON.parseArray(assetsBeSub);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            String asset = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("asset", asset);
            Long count = 0L;
            //获取减数列表对应资产笔数
            for (int j = 0; j < sub.size(); j++) {
                JSONObject object = sub.getJSONObject(j);
                if (asset.equals(object.getString("asset"))) {
                    count = count + object.getLongValue("count");
                    break;
                }
            }
            //获取被减列表对应资产笔数
            for (int k = 0; k < beSub.size(); k++) {
                JSONObject object = beSub.getJSONObject(k);
                if (asset.equals(object.getString("asset"))) {
                    if (count - object.getLongValue("count") >= 0) {
                        count = count - object.getLongValue("count");
                    }
                    break;
                }
            }
            jsonObject.put("count", count);
            array.add(jsonObject);
        }
        return array.toJSONString();
    }

    public static BigDecimal getAssetAmount(String list, String asset) {
        if (StringUtils.isBlank(asset)) {
            asset = SEER_ASSET;
        }
        JSONArray array = JSON.parseArray(list);
        BigDecimal amount = BigDecimal.ZERO;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("asset").equals(asset)) {
                amount = object.getBigDecimal("amount");
            }
        }
        return amount;
    }

    public static Long getAssetCount(String list, String asset) {
        if (StringUtils.isBlank(asset)) {
            asset = SEER_ASSET;
        }
        JSONArray array = JSON.parseArray(list);
        Long count = 0L;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("asset").equals(asset)) {
                count = object.getLong("count");
            }
        }
        return count;
    }

    public static Long getHouseCount(String list, String house) {
        JSONArray array = JSON.parseArray(list);
        Long count = 0L;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("house").equals(house)) {
                count = object.getLong("count");
            }
        }
        return count;
    }

    public static JSONArray getHousesCount(String list, List<String> house) {
        JSONArray array = JSON.parseArray(list);
        JSONArray result = new JSONArray();
        for (int i = 0; i < house.size(); i++) {
            Long count = 0L;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", house.get(i));
            for (int j = 0; j < array.size(); j++) {
                JSONObject object = array.getJSONObject(j);
                if (object.getString("house").equals(house.get(i))) {
                    count = object.getLong("count");
                }
            }
            jsonObject.put("count", count);
            result.add(jsonObject);
        }
        return result;
    }

    public static Long getHousesCountSum(String list, List<String> house) {
        JSONArray array = JSON.parseArray(list);
        Long count = 0L;
        for (int i = 0; i < house.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                JSONObject object = array.getJSONObject(j);
                if (object.getString("house").equals(house.get(i))) {
                    count = count + object.getLong("count");
                }
            }
        }
        return count;
    }

    public static BigDecimal getHouseRate(String list, String house) {
        JSONArray array = JSON.parseArray(list);
        BigDecimal rate = BigDecimal.ZERO;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("house").equals(house)) {
                rate = object.getBigDecimal("rate");
            }
        }
        return rate;
    }

    public static JSONArray getHousesRate(String list, List<String> house) {
        JSONArray array = JSON.parseArray(list);
        JSONArray result = new JSONArray();
        for (int i = 0; i < house.size(); i++) {
            String rate = "0.00";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("house", house.get(i));
            for (int j = 0; j < array.size(); j++) {
                JSONObject object = array.getJSONObject(j);
                if (object.getString("house").equals(house.get(i))) {
                    rate = object.getString("rate");
                }
            }
            jsonObject.put("rate", rate);
            result.add(jsonObject);
        }
        return result;
    }

    public static String getHousesRateSum(String list, List<String> house) {
        JSONArray array = JSON.parseArray(list);
        BigDecimal rate = BigDecimal.ZERO.setScale(2);
        for (int i = 0; i < house.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                JSONObject object = array.getJSONObject(j);
                if (object.getString("house").equals(house.get(i))) {
                    rate = rate.add(object.getBigDecimal("rate"));
                }
            }
        }
        return rate.divide(new BigDecimal(house.size()), 2, BigDecimal.ROUND_HALF_DOWN).toString();
    }

    public static String convertAmount(BigDecimal decimal) {
        BigDecimal tenThousand = new BigDecimal("10000");
        String amountStr = "";
        if (decimal.compareTo(tenThousand) > 0) {
            amountStr = decimal.divide(tenThousand, 2, BigDecimal.ROUND_HALF_DOWN).toString() + " W";
        } else {
            amountStr = decimal.toString();
        }
        return amountStr;
    }

    public static Long getHouseAmount(String list, String house) {
        JSONArray array = JSON.parseArray(list);
        Long count = 0L;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("house").equals(house)) {
                count = object.getLong("amount");
            }
        }
        return count;
    }

    public static String updateHouseAmount(String list, String house, Long amount) {
        JSONArray array = JSON.parseArray(list);
        Long count = 0L;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("house").equals(house)) {
                object.replace("amount", amount);
                array.set(i, object);
            }
        }
        return array.toJSONString();
    }

    public static String initHouseAmount(String houses) {
        List<String> list = Arrays.asList(houses.split(CONFIG_HOUSES_SPLIT));
        JSONArray array = new JSONArray();
        for (String str : list) {
            JSONObject object = new JSONObject();
            object.put("house", str);
            object.put("amount", "0");
            array.add(object);
        }
        return array.toJSONString();
    }
}
