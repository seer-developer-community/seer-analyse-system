package com.seer.operation.rpcClient.operation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class GlobalParametersVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private JSONObject CurrentFees;
    private Integer BlocKInterval;
    private Integer MainTenanceInterval;
    private Integer MainTenanceSkipSlots;
    private Integer CommitteeProposalReviewPeriod;
    private Integer MaxiMumTransactionSize;
    private Integer MaxiMumBlockSize;
    private Integer MaxiMumTimeUntilExpiration;
    private BigInteger MAximumProposalLifetime;
    private Integer MaxiMumAssetWhitelistAuthorities;
    private Integer MaximumAuthenticatorCount;
    private Integer MaximumCommitteeCount;
    private Integer MaximumAuthorityMembership;
    private Integer NetworkPercentOfFee;
    private Integer LifetimeReferrerPercentOfFee;
    private BigInteger CashbackVestingPeriodSeconds;
    private BigInteger CashbackVestingThreshold;
    private Boolean CountNonMemberVotes;
    private Boolean AllowNonMemberWhitelists;
    private Integer WitnessPayPerBlock;
    private Integer MaxPredicateOpcode;
    private BigInteger FeeLiquidationThreshold;
    private Integer AccountsPerFeeScale;
    private Integer AccountFeeScaleBitshifts;
    private Integer MaxAuthorityDepth;
    private BigInteger MinGuarantyPerRoom;
    private BigInteger MaxOracleReward;
    private Integer FixedWitnessCount;
    private Integer MaximumProfitWitnessCount;
    private Integer MaximunSeerSettlesPerBlock;
    private Integer SupportedAuthenticateTypes;
    private List extensions;
}
