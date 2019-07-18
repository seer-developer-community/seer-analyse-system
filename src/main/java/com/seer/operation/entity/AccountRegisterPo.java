package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("account_register")
public class AccountRegisterPo implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String name;
    private String registrar;
    private Long regTime;
    private Integer players;
    private Integer botPlayers;
    private Integer truePlayers;
    private Integer botTruePlayers;
    private String depositAmount;
    private String botDepositAmount;
    private String depositCount;
    private String botDepositCount;
    private String transferAmount;
    private String botTransferAmount;
    private String transferCount;
    private String botTransferCount;
    private BigDecimal seerbotFees;
    private BigDecimal totalRegisteredFees;
    private BigInteger lastBlock;
    private Long createTime;
    private Long updateTime;
}
