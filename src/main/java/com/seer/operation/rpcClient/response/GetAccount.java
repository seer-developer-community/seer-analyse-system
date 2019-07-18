package com.seer.operation.rpcClient.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class GetAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String membershipExpirationDate;
    private String registrar;
    private String referrer;
    private String lifetimeReferrer;
    private BigInteger networkFeePercentage;
    private BigInteger lifetime_referrerFeePercentage;
    private BigInteger referrerRewardsPercentage;
    private String name;
    private JSONObject owner;
    private JSONObject active;
    private JSONObject options;
    private String statistics;
    private List whitelistingAccounts;
    private List blacklistingAccounts;
    private List whitelistedAccounts;
    private List blacklistedAccounts;
    private List ownerSpecialAuthority;
    private List activeSpecialAuthority;
    private Integer topNControlFlags;
    private Integer country;
    private Integer status;
    private List authentications;
}
