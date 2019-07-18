package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("account_auth")
public class AccountAuthPo implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String name;
    private Integer ownerWeightThreshold;
    private String ownerAccountAuths;
    private String ownerKeyAuths;
    private String ownerAddressAuths;
    private Integer activeWeightThreshold;
    private String activeKeyAuths;
    private String activeAddressAuths;
    private String memoKey;
    private String votingAccount;
    private Integer numCommittee;
    private Integer numAuthenticator;
    private Integer numSupervisor;
    private String votes;
    private Long createTime;
    private Long updateTime;
}
