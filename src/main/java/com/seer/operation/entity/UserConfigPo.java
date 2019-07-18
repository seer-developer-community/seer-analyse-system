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
@TableName("user_config")
public class UserConfigPo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private String faucetAccounts;
    private String gateways;
    private String assets;
    private String houses;
    private String seerBots;
    private String defaultAsset;
    private String defaultOwner;
    private String whiteAccounts;
}
