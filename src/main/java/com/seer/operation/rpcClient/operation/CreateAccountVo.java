package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class CreateAccountVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger feeAmount;
    private String feeAssetId;
    private String registrar;
    private String referrer;
    private BigInteger referrerPercent;
    private String name;
    private String id;//用户id
    /**
     * owner 账户权限
     */
    private BigInteger ownerWeightThreshold;
    private List ownerAccountAuths;
    private List ownerKeyAuths;
    private List ownerAddressAuths;
    /**
     * active 资金权限
     */
    private BigInteger activeWeightThreshold;
    private List activeAccountAuths;
    private List activeKeyAuths;
    private List activeAddressAuths;
    //memo权限
    private String optionsMemoKey;
    private String optionsVotingAccount;
    private BigInteger optionsNumCommittee;
    private BigInteger optionsNumAuthenticator;
    private BigInteger optionsNumSupervisor;
    private List optionsVotes;
    private List optionsExtensions;
    private JSONObject extensions;

    public CreateAccountVo(JSONObject object, String id) {
        this.feeAmount = object.getJSONObject("fee").getBigInteger("amount");
        this.feeAssetId = object.getJSONObject("fee").getString("asset_id");
        this.registrar = object.getString("registrar");
        this.referrer = object.getString("referrer");
        this.referrerPercent = object.getBigInteger("referrer_percent");
        this.name = object.getString("name");
        this.ownerWeightThreshold = object.getJSONObject("owner").getBigInteger("weight_threshold");
        this.ownerAccountAuths = object.getJSONObject("owner").getJSONArray("account_auths");
        this.ownerKeyAuths = object.getJSONObject("owner").getJSONArray("key_auths");
        this.ownerAddressAuths = object.getJSONObject("owner").getJSONArray("address_auths");
        this.activeWeightThreshold = object.getJSONObject("active").getBigInteger("weight_threshold");
        this.activeAccountAuths = object.getJSONObject("active").getJSONArray("account_auths");
        this.activeAddressAuths = object.getJSONObject("active").getJSONArray("address_auths");
        this.activeKeyAuths = object.getJSONObject("active").getJSONArray("key_auths");
        this.optionsMemoKey = object.getJSONObject("options").getString("memo_key");
        this.optionsVotingAccount = object.getJSONObject("options").getString("voting_account");
        this.optionsNumCommittee = object.getJSONObject("options").getBigInteger("num_committee");
        this.optionsNumAuthenticator = object.getJSONObject("options").getBigInteger("num_authenticator");
        this.optionsNumSupervisor = object.getJSONObject("options").getBigInteger("num_supervisor");
        this.optionsVotes = object.getJSONObject("options").getJSONArray("votes");
        this.optionsExtensions = object.getJSONObject("options").getJSONArray("extensions");
        this.extensions = object.getJSONObject("extensions");
        this.id = id;
    }
}
