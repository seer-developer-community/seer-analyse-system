package com.seer.operation.handleService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.entity.UserConfigPo;
import com.seer.operation.request.FeeBo;
import com.seer.operation.rpcClient.operation.*;
import com.seer.operation.rpcClient.response.SeerRoom;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.seer.operation.request.Constants.*;

/**
 * @author zhang sir
 */
@Service
public class OperateHandle {
    @Autowired
    private ClientFunService clientFunService;
    @Autowired
    private DailyAccountHandle dailyAccountHandle;
    @Autowired
    private RoomHandle roomHandle;
    @Autowired
    private AuthHandle authHandle;
    @Autowired
    private AccountBaseHandle baseHandle;
    @Autowired
    private AccountRegisterHandle registerHandle;
    @Autowired
    private HouseTotalHandle houseTotalHandle;
    @Autowired
    private HouseTypeHandle houseTypeHandle;
    @Autowired
    private DailyDataFaucetHandle dailyDataFaucetHandle;
    //    @Autowired
//    private DailyDataTotalHandle dailyDataTotalHandle;
//    @Autowired
//    private DailyDataHousesHandle dataHousesHandle;
    @Autowired
    private DailyHouseRoomHandle dailyHouseRoomHandle;
    @Autowired
    private DailyTypeRoomHandle dailyTypeRoomHandle;
    @Autowired
    private DataCacheManager cacheManager;
    @Autowired
    private DailyProfitHandle dailyProfitHandle;

    @Transactional
    public void handleType(BigInteger block, Long time, Integer type, JSONObject object, JSONArray result, boolean isCheck) {
        if (null == type || null == object || null == time) {
            throw new IllegalArgumentException("参数为空");
        }
        FeeBo feeBo = new FeeBo();
        JSONObject fee = new JSONObject();
        SeerRoom seerRoom = new SeerRoom();
        switch (type) {
            case 0:
                //处理 account_base 用户转账支出
                TransferVo transferVo = new TransferVo(object);
                if (checkWhiteAccount(transferVo.getFrom())) {
                    return;
                }
                feeBo = clientFunService.getFee(transferVo.getFeeAssetId(), transferVo.getFeeAmount());
                baseHandle.updateBy0(block, transferVo, feeBo, isCheck);
                //处理 account_register
                registerHandle.updateBy0(transferVo, block, isCheck);
                //处理 daily_faucet
                dailyDataFaucetHandle.updateDailyDepositCount(transferVo, clientFunService.getAccountRegistrar(transferVo.getFrom()),
                        clientFunService.getAccountRegistrar(transferVo.getTo()), time, block, feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, transferVo.getFrom(), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, transferVo.getFrom(), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateBy0(transferVo, feeBo, block, time, true, isCheck);
                break;
            case 1:
                if (checkWhiteAccount(object.getString("seller"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("seller"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("seller"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("seller"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("seller"), feeBo, block, time, true, isCheck);
                break;
            case 2:
                if (checkWhiteAccount(object.getString("fee_paying_account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("fee_paying_account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("fee_paying_account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("fee_paying_account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("fee_paying_account"), feeBo, block, time, true, isCheck);
                break;
            case 3:
                if (checkWhiteAccount(object.getString("account_id"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("account_id"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("account_id"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("account_id"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("account_id"), feeBo, block, time, true, isCheck);
                break;
            case 4:
                //处理 account_auth
                String id = result.getString(1); //被创建的用户id
                CreateAccountVo createAccountVo = new CreateAccountVo(object, id);
                if (checkWhiteAccount(createAccountVo.getRegistrar())) {
                    return;
                }
                feeBo = clientFunService.getFee(createAccountVo.getFeeAssetId(), createAccountVo.getFeeAmount());
                authHandle.initAuth(createAccountVo);
                //处理 account_base 初始化
                baseHandle.initBase(block, createAccountVo, time, feeBo);
                //更新累积注册用户
                baseHandle.updateBy4(block, createAccountVo, isCheck);
                //处理 account_register 初始化
                registerHandle.initRegister(createAccountVo, block, time);
                //更新 用户的注册用户支出
                registerHandle.updateRegisteredFees(block, createAccountVo, feeBo, false, isCheck);
                //处理 house_total 初始化
                houseTotalHandle.initHouseTotal(createAccountVo, block);
                //处理 house_type 初始化
                houseTypeHandle.initHouseType(createAccountVo, block);
                //处理 daily_faucet
                dailyDataFaucetHandle.updateDailyFaucetDailyRegistered(block, createAccountVo.getRegistrar(), feeBo, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, createAccountVo.getRegistrar(), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, createAccountVo.getRegistrar(), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateBy4(createAccountVo.getRegistrar(), feeBo, block, time, true, isCheck);
                break;
            case 5:
                if (checkWhiteAccount(object.getString("account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("account"), feeBo, block, time, true, isCheck);
                break;
            case 7:
                if (checkWhiteAccount(object.getString("account_to_upgrade"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("account_to_upgrade"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("account_to_upgrade"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("account_to_upgrade"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("account_to_upgrade"), feeBo, block, time, true, isCheck);
                break;
            case 9:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 10:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 11:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 12:
                if (checkWhiteAccount(object.getString("payer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("payer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("payer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("payer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("payer"), feeBo, block, time, true, isCheck);
                break;
            case 13:
                if (checkWhiteAccount(object.getString("from_account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("from_account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("from_account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("from_account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("from_account"), feeBo, block, time, true, isCheck);
                break;
            case 14:
                if (checkWhiteAccount(object.getString("witness_account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("witness_account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("witness_account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("witness_account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("witness_account"), feeBo, block, time, true, isCheck);
                break;
            case 15:
                if (checkWhiteAccount(object.getString("witness"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("witness"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("witness"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("witness"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("witness"), feeBo, block, time, true, isCheck);
                break;
            case 16:
                if (checkWhiteAccount(object.getString("witness"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("witness"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("witness"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("witness"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("witness"), feeBo, block, time, true, isCheck);
                break;
            case 17:
                if (checkWhiteAccount(object.getString("witness"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("witness"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("witness"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("witness"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("witness"), feeBo, block, time, true, isCheck);
                break;
            case 18:
                if (checkWhiteAccount(object.getString("witness"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("witness"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("witness"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("witness"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("witness"), feeBo, block, time, true, isCheck);
                break;
            case 19:
                if (checkWhiteAccount(object.getString("fee_paying_account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("fee_paying_account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("fee_paying_account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("fee_paying_account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("fee_paying_account"), feeBo, block, time, true, isCheck);
                break;
            case 20:
                if (checkWhiteAccount(object.getString("fee_paying_account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("fee_paying_account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("fee_paying_account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("fee_paying_account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("fee_paying_account"), feeBo, block, time, true, isCheck);
                break;
            case 26:
                if (checkWhiteAccount(object.getString("committee_member_account"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("committee_member_account"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("committee_member_account"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("committee_member_account"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("committee_member_account"), feeBo, block, time, true, isCheck);
                break;
            case 30:
                //处理 account_base
                Opcode30Vo opcode30Vo = new Opcode30Vo(object);
                if (checkWhiteAccount(opcode30Vo.getOwner())) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                baseHandle.updateBy30(opcode30Vo, block, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, opcode30Vo.getOwner(), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, opcode30Vo.getOwner(), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateBy30(opcode30Vo, feeBo, block, time, true, isCheck);
                break;
            case 40:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 41:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 42:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 43:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                Integer room_type = object.getInteger("room_type");
                seerRoom = new SeerRoom();
                seerRoom.setRoomType(room_type);
                seerRoom.setOwner(object.getString("issuer"));
                String asset43 = object.getJSONObject("option").getString("accept_asset");
                JSONObject initial43 = object.getJSONObject("initial_option");
                if (ADV_ROOM == room_type) {
                    BigDecimal amountPool43 = initial43.getJSONObject("advanced")
                            .getBigDecimal("pool").divide(SEER_DECIMALS).setScale(5);
                    houseTotalHandle.updateAdvPlayAmount(seerRoom, asset43, amountPool43, block, isCheck);
//                    dataHousesHandle.updateAdvPlayAmount(seerRoom, asset43, amountPool43, block, time, true, isCheck);
                }
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, true, isCheck);
                //house_type 更新PVD投注额
                if (PVD_ROOM == room_type) {
                    BigDecimal lnRange = new BigDecimal(Math.log(initial43.getDouble("range")))
                            .setScale(5, BigDecimal.ROUND_HALF_DOWN);
                    BigDecimal amountLmsr43 = initial43.getJSONObject("lmsr").getBigDecimal("L")
                            .divide(SEER_DECIMALS).multiply(lnRange).setScale(5);
                    houseTypeHandle.updatePvdPlayAmount(seerRoom, asset43, amountLmsr43, block, false, isCheck);
                }
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, false, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 44:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //处理 total_room
                roomHandle.updateTotalRoomDapp(type, object, block, time, result.getJSONObject(1), true, isCheck);
                //更新 house_total的adv_fees
                seerRoom = clientFunService.getSeerRoom(object.getString("room"));
                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, true, isCheck);
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, true, isCheck);
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                //daily_data_houses 房间手续费
//                dataHousesHandle.updateFees(seerRoom, feeBo, block, false, time, isCheck);
                dailyTypeRoomHandle.updateFees(seerRoom, feeBo, block, time, true, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 45:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //处理 total_room
                roomHandle.updateTotalRoomDapp(type, object, block, time, result.getJSONObject(1), true, isCheck);
                //更新 house_total的adv_fees
                seerRoom = clientFunService.getSeerRoom(object.getString("room"));
                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, true, isCheck);
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, true, isCheck);
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                //daily_data_houses 房间手续费
//                dataHousesHandle.updateFees(seerRoom, feeBo, block, false, time, isCheck);
                dailyTypeRoomHandle.updateFees(seerRoom, feeBo, block, time, true, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 46:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                OpenRoomVo roomVo = new OpenRoomVo(object);
                feeBo = clientFunService.getFee(roomVo.getFeeAsset(), roomVo.getFeeAmount());
                seerRoom = clientFunService.getSeerRoom(roomVo.getRoom());
                //处理 daily_room_details & total_room
                roomHandle.openRoom(block, seerRoom, feeBo, time, roomVo);
                //处理 house_total,更新房主总房间数
                houseTotalHandle.updateBy46(roomVo.getIssuer(), block, isCheck);
                //更新 house_total的adv_fees
                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, false, isCheck);
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, true, isCheck);
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_house 新增房间数
//                dataHousesHandle.updateNewRooms(object.getString("issuer"), seerRoom, time, block, true, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                //daily_data_houses 房间手续费
//                dataHousesHandle.updateFees(seerRoom, feeBo, block, false, time, isCheck);
                dailyHouseRoomHandle.updateBy46(object.getString("issuer"), seerRoom, time, block, true, isCheck);
                dailyTypeRoomHandle.updateFees(seerRoom, feeBo, block, time, true, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 47:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //处理 total_room
                roomHandle.updateTotalRoomDapp(type, object, block, time, result.getJSONObject(1), true, isCheck);
                roomHandle.stopRoom(object.getString("room"));
                //更新 house_total的adv_fees
                seerRoom = clientFunService.getSeerRoom(object.getString("room"));
                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, true, isCheck);
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, true, isCheck);
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                //daily_data_houses 房间手续费
//                dataHousesHandle.updateFees(seerRoom, feeBo, block, false, time, isCheck);
                dailyTypeRoomHandle.updateFees(seerRoom, feeBo, block, time, true, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 48:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //处理 total_room
                roomHandle.updateTotalRoomDapp(type, object, block, time, result.getJSONObject(1), true, isCheck);
                //更新 house_total的adv_fees
                seerRoom = clientFunService.getSeerRoom(object.getString("room"));
//                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, true, isCheck);
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, true, isCheck);
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                //daily_data_houses 房间手续费
//                dataHousesHandle.updateFees(seerRoom, feeBo, block, false, time, isCheck);
                dailyTypeRoomHandle.updateFees(seerRoom, feeBo, block, time, true, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 49:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //处理 total_room
                roomHandle.updateTotalRoomDapp(type, object, block, time, result.getJSONObject(1), true, isCheck);
                //更新 house_total的adv_fees
                seerRoom = clientFunService.getSeerRoom(object.getString("room"));
                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, true, isCheck);
                //更新 house_total 房主总收入，总派奖
                houseTotalHandle.updateBy49(seerRoom, result.getJSONObject(1), block, false, isCheck);
                //update house_type 派奖操作相关更新
                houseTypeHandle.updateBy49(seerRoom, result.getJSONObject(1), block, isCheck);
                //update house_type pvd pvp手续费
                houseTypeHandle.updateFees(feeBo, seerRoom, block, false, isCheck);
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //update daily_data_total:fees
//                dailyDataTotalHandle.updateFee(seerRoom, feeBo, block, true, isCheck, time);
                //update daily_data_total:settle
//                dailyDataTotalHandle.updateSettle(seerRoom, result.getJSONObject(1), block, false, isCheck, time);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses 补贴
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                //daily_data_houses 派奖操作
//                dataHousesHandle.updateSettle(seerRoom, feeBo, result.getJSONObject(1), block, false, isCheck, time);
                dailyTypeRoomHandle.updateBy49(seerRoom, feeBo, result.getJSONObject(1), block, true, isCheck, time);
                dailyProfitHandle.updateBy49(object.getString("issuer"), seerRoom, result.getJSONObject(1), feeBo
                        , block, time, true, isCheck);
                break;
            case 50:
                //处理 daily_account_details
                PredictionVo predictionVo = new PredictionVo(object);
                if (checkWhiteAccount(predictionVo.getIssuer())) {
                    return;
                }
                feeBo = clientFunService.getFee(predictionVo.getFeeAssetId(), predictionVo.getFeeAmount());
                seerRoom = clientFunService.getSeerRoom(predictionVo.getRoom());
                String deltasAsset = result.getJSONObject(1).getString("asset_id");
                String deltasAmount50 = result.getJSONObject(1).getJSONArray("deltas").getJSONArray(0).getString(1);
                if (deltasAmount50.startsWith("-")) {
                    deltasAmount50 = deltasAmount50.split("-")[1];
                }
                BigDecimal amount50 = new BigDecimal(deltasAmount50).divide(SEER_DECIMALS).setScale(5);
                predictionVo.setDeltasAsset(deltasAsset);
                predictionVo.setDeltasAmount(amount50);
                predictionVo.setFeeBo(feeBo);
                dailyAccountHandle.prediction(block, time, predictionVo, feeBo, result.getJSONObject(1), isCheck);
                //处理daily_room
                roomHandle.updateDailyRoom(predictionVo, block, time, seerRoom, isCheck);
                //处理 total_room
                roomHandle.updateTotalRoomDapp(type, object, block, time, result.getJSONObject(1), false, isCheck);
                //处理 account_base
                baseHandle.updateBy50(block, predictionVo, feeBo, result.getJSONObject(1), isCheck);
                //处理 account_register
                registerHandle.updateBy50(predictionVo, feeBo, time, block, isCheck);
                //处理 house_total 房主高级投注总额
                houseTotalHandle.updateAdvPlayAmount(seerRoom, deltasAsset, amount50, block, isCheck);
                //house_total 更新操作
                houseTotalHandle.updateBy50(predictionVo, seerRoom, feeBo, deltasAsset, amount50, block);
                //house_type 更新PVD投注额
                houseTypeHandle.updatePvdPlayAmount(seerRoom, deltasAsset, amount50, block, true, isCheck);
                //house_type 更新50相关操作
                houseTypeHandle.updateBy50(predictionVo, seerRoom, feeBo, deltasAsset, amount50, block, false, isCheck);
                //处理daily_data_faucet的活跃投注用户
                dailyDataFaucetHandle.updateDailyFaucetActivePlayer(block, predictionVo.getIssuer(),
                        clientFunService.getAccountRegistrar(predictionVo.getIssuer()), time, isCheck);
                //处理 daily_data_faucet 新增注册且投注用户等
                dailyDataFaucetHandle.updateFaucetTrueAndPlayers(block, time, predictionVo.getIssuer(),
                        clientFunService.getAccountRegistrar(predictionVo.getIssuer()), time, isCheck, false);
                //更新 daily_data_total 相关的投注
//                dailyDataTotalHandle.operate50(predictionVo, seerRoom, block, isCheck, time);
                //更新data_house的真人参与房间
//                dataHousesHandle.updateActiveRooms(predictionVo.getIssuer(), seerRoom, block, true, time, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, seerRoom.getOwner(), clientFunService.getAccountRegistrar(predictionVo.getIssuer()), predictionVo, block, false, time, isCheck);
                //daily_data_houses 投注操作
//                dataHousesHandle.updateHouseBattle(seerRoom, feeBo, predictionVo, time, isCheck, false, block);
                dailyHouseRoomHandle.updateBy50(predictionVo, seerRoom, time, block, true, isCheck);
                dailyTypeRoomHandle.updateBy50(predictionVo, feeBo, seerRoom, time, block, true, isCheck);
                dailyProfitHandle.updateBy50(predictionVo, clientFunService.getAccountRegistrar(predictionVo.getIssuer()),
                        seerRoom, feeBo, block, time, true, isCheck);
                break;
            case 52:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                seerRoom = clientFunService.getSeerRoom(object.getString("room"));
                //处理 house_total 房主高级投注总额
                BigInteger amount52 = object.getJSONObject("amount").getBigInteger("amount");
                BigDecimal amount52Dec = new BigDecimal(amount52).divide(SEER_DECIMALS).setScale(5);
                String asset52 = object.getJSONObject("amount").getString("asset_id");
                houseTotalHandle.updateAdvPlayAmount(seerRoom, asset52, amount52Dec, block, isCheck);
                //更新 house_total的adv_fees
                houseTotalHandle.updateAdvFees(seerRoom, feeBo, block, false, isCheck);
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
//                dataHousesHandle.updateAdvPlayAmount(seerRoom, asset52, amount52Dec, block, time, false, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 53:
                if (checkWhiteAccount(object.getString("issuer"))) {
                    return;
                }
                fee = object.getJSONObject("fee");
                feeBo = clientFunService.getFee(fee.getString("asset_id"), fee.getBigInteger("amount"));
                //account_base 手续费总耗
                baseHandle.updateTotalFees(block, object.getString("issuer"), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateSubsidy(object.getString("issuer"), feeBo, block, time, true, isCheck);
                break;
            case 54:
                //处理 account_base
                Opcode54Vo opcode54Vo = new Opcode54Vo(object);
                if (checkWhiteAccount(opcode54Vo.getIssuer())) {
                    return;
                }
                baseHandle.updateBy54(opcode54Vo, block, isCheck);
                //account_base 手续费总耗
                feeBo = clientFunService.getFee(opcode54Vo.getFeeAssetId(), opcode54Vo.getFeeAmount());
                baseHandle.updateTotalFees(block, opcode54Vo.getIssuer(), feeBo, isCheck);
                //daily_account fee
                dailyAccountHandle.updateFee(block, time, object.getString("issuer"), feeBo, isCheck);
                //daily_data_houses
//                dataHousesHandle.updateSubsidy(feeBo, object.getString("issuer"), null, null, block, true, time, isCheck);
                dailyProfitHandle.updateBy54(opcode54Vo, feeBo, block, time, true, isCheck);
                break;
        }
    }

    public boolean checkWhiteAccount(String account) {
        UserConfigPo configPo = cacheManager.getConfigClient(USER_CONFIG_ID);
        if (null == configPo) {
            return false;
        }
        if (StringUtils.isBlank(configPo.getWhiteAccounts())) {
            return false;
        }
        if (configPo.getWhiteAccounts().contains(account)) {
            return true;
        }
        return false;
    }
}
