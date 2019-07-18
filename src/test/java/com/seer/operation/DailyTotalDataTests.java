package com.seer.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.DailyHouseRoomPo;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.handleService.DailyHouseRoomHandle;
import com.seer.operation.handleService.OperateHandle;
import com.seer.operation.handleService.StaticFunHandle;
import com.seer.operation.service.DailyHouseRoomService;
import com.seer.operation.service.TransactionService;
import com.seer.operation.service.UserConfigService;
import com.seer.operation.threadPool.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.seer.operation.request.Constants.USER_CONFIG_ID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DailyTotalDataTests {
    @Autowired
    private DailyHouseRoomHandle houseRoomHandle;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OperateHandle operateHandle;
    @Autowired
    private AsyncTaskService asyncTaskService;
    @Autowired
    private DailyHouseRoomService houseRoomService;
    @Autowired
    private UserConfigService configService;

    @Test
    public void test() {
//        JSONArray array = JSON.parseArray("");
//        System.out.println(array.size());
//        array = new JSONArray();
//        System.out.println(array.toJSONString());
//        array = JSON.parseArray(array.toJSONString());
//        System.out.println(array.size());
//        String s = StaticFunHandle.initRoomsList("", "1.2,1.23");
//        System.out.println(s);
//        String s = "[{\"list\":\"1.3,1.4\",\"house\":\"1.1.1\"},{\"list\":\"\",\"house\":\"1.1.2\"},{\"list\":\"\",\"house\":\"1.1.3\"}]";
//        System.out.println(StaticFunHandle.addRoomList("1.1.1,1.1.2,1.1.3",s,"1.5","1.1.1"));
    }

    @Test
    public void houseRoomInit() {
//        Long begin = 1526572800000L; //2018-05-18
//        Long end = 1562083200000L; //2019-07-03
//        Long day = 86400000L;
//        Long b = System.currentTimeMillis();
//        for (Long i = begin; i <= end; i = i + day) {
//            houseRoomHandle.initHouseRoom(i);
//            log.info("日期:{} 初始化完成", Times.formatDateByTimes(i));
//        }
//        log.info("初始化完成，耗时:{} ms", (System.currentTimeMillis() - b));
    }

    @Test
    public void houseRoom46() {
//        List<TransactionPo> list = transactionService.selectList(1, 11711491, 46);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，46的条数：{}", list.size());
//        for (int i = 0; i < list.size(); i++) {
//            TransactionPo po = list.get(i);
//            JSONObject object = JSON.parseArray(po.getOperations()).getJSONObject(1);
//            operateHandle.handleType(po.getBlockHeight(), po.getBlockTime(), po.getType(), object, JSON.parseArray(po.getOperationResults()), false);
//            log.info("区块：{} 完成", po.getBlockHeight());
//        }
//        log.info("46修复完成，耗时：{}", (System.currentTimeMillis() - b));
    }

    @Test
    public void houseRoom50() throws InterruptedException {
//        List<TransactionPo> list = transactionService.selectList(1, 11711491, 50);
//        Long b = System.currentTimeMillis();
//        log.info("开始修复，50的条数：{}", list.size());
//        List<TransactionPo> list2_3 = new LinkedList<>();
//        List<TransactionPo> list2_4 = new LinkedList<>();
//        List<TransactionPo> list2_5 = new LinkedList<>();
//        List<TransactionPo> list3 = new LinkedList<>();
//        List<TransactionPo> list4 = new LinkedList<>();
//        List<TransactionPo> list5 = new LinkedList<>();
//        List<TransactionPo> list6 = new LinkedList<>();
//        List<TransactionPo> list7 = new LinkedList<>();
//        List<TransactionPo> list8 = new LinkedList<>();
//        List<TransactionPo> list9 = new LinkedList<>();
//        List<TransactionPo> list10 = new LinkedList<>();
//        List<TransactionPo> list11 = new LinkedList<>();
//        List<TransactionPo> list12 = new LinkedList<>();
//        List<TransactionPo> list13 = new LinkedList<>();
//        for (int i = 0; i < list.size(); i++) {//适应于50操作的线程分配
//            TransactionPo po = list.get(i);
//            Long time = po.getBlockTime();
//            if (time <= 1529337600000L) {//6.19
//                list2_3.add(po);
//            } else if (time <= 1529856000000L) {//6.25
//                list2_4.add(po);
//            } else if (time <= 1530374400000L) {//7.1
//                list2_5.add(po);
//            } else if (time <= 1531843200000L) {//7.18
//                list3.add(po);
//            } else if (time <= 1534521600000L) {//8.18
//                list4.add(po);
//            } else if (time <= 1537200000000L) {//9.18
//                list5.add(po);
//            } else if (time <= 1538668800000L) {//10.05
//                list6.add(po);
//            } else if (time <= 1539532800000L) {//10.15
//                list7.add(po);
//            } else if (time <= 1540828800000L) {//10.30
//                list8.add(po);
//            } else if (time <= 1542211200000L) {//11.15
//                list9.add(po);
//            } else if (time <= 1546617600000L) {//2019.1.05
//                list10.add(po);
//            } else if (time <= 1550592000000L) {//2.20
//                list11.add(po);
//            } else if (time <= 1553011200000L) {//3.20
//                list12.add(po);
//            }else {
//                list13.add(po);
//            }
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(14);
//        asyncTaskService.fixList(countDownLatch, list2_3, 99);
//        asyncTaskService.fixList(countDownLatch, list2_4, 99);
//        asyncTaskService.fixList(countDownLatch, list2_5, 99);
//        asyncTaskService.fixList(countDownLatch, list3, 99);
//        asyncTaskService.fixList(countDownLatch, list4, 99);
//        asyncTaskService.fixList(countDownLatch, list5, 99);
//        asyncTaskService.fixList(countDownLatch, list6, 99);
//        asyncTaskService.fixList(countDownLatch, list7, 99);
//        asyncTaskService.fixList(countDownLatch, list8, 99);
//        asyncTaskService.fixList(countDownLatch, list9, 99);
//        asyncTaskService.fixList(countDownLatch, list10, 99);
//        asyncTaskService.fixList(countDownLatch, list11, 99);
//        asyncTaskService.fixList(countDownLatch, list12, 99);
//        asyncTaskService.fixList(countDownLatch, list13, 99);
//        countDownLatch.await();
//        log.info("50修复完成，交易笔数:{}，耗时：{} ms", list.size(), System.currentTimeMillis() - b);
    }

    @Test
    public void fixHouseRoomTotal() {
//        List<DailyHouseRoomPo> list = houseRoomService.selectList(1526572800000L, 1562083200000L);
//        UserConfigPo configPo = configService.selectOne(USER_CONFIG_ID);
//        for (int i = 1; i < list.size(); i++) {
//            DailyHouseRoomPo po1 = list.get(i - 1);
//            DailyHouseRoomPo po2 = list.get(i);
            //累计房间数
//            po2.setTotalRooms(StaticFunHandle.addHousesCount(configPo.getHouses(), po1.getTotalRooms(),
//                    po2.getTotalRooms()));
            //累计投注人次
//            po2.setTotalPrtpTimes(StaticFunHandle.addHousesCount(configPo.getHouses(), po1.getTotalPrtpTimes(),
//                    po2.getTotalPrtpTimes()));
            //累计投注额
//            po2.setTotalPlayAmount(StaticFunHandle.addAssetsAmount(configPo.getAssets(), po2.getTotalPlayAmount(),
//                    po1.getTotalPlayAmount()));
            //累计真人参与房间列表
//            String rooms = StaticFunHandle.addTotalRoomList(configPo.getHouses(), po1.getTotalActiveRoomsList(),
//                    po2.getTotalActiveRoomsList());
//            po2.setTotalActiveRoomsList(rooms);
            //累计有真人参与的房间数
//            JSONArray array = JSON.parseArray(po2.getTotalActiveRoomsList());
//            JSONArray array1 = new JSONArray();
//            for (int k = 0; k < array.size(); k++) {
//                String house = array.getJSONObject(k).getString("house");
//                int len = array.getJSONObject(k).getString("list").split(",").length;
//                JSONObject object = new JSONObject();
//                object.put("house", house);
//                object.put("count", len);
//                array1.add(object);
//            }
//            po2.setTotalActiveRooms(array1.toJSONString());
            //累计房间参与率
//            String totalActiveRooms = po2.getTotalActiveRooms();
//            String totalRooms = po2.getTotalRooms();
//            String totalRate = StaticFunHandle.updateHousesRate(configPo.getHouses(), totalActiveRooms, totalRooms);
//            po2.setTotalPrtpRate(totalRate);
//            houseRoomService.updateById(po2);
//        }
    }
}
