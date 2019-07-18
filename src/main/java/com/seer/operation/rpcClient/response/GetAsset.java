package com.seer.operation.rpcClient.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class GetAsset implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String symbol;
    private Integer precision;
    private String issuer;
    private BigInteger maxSupply;
    private BigInteger marketFeePercent;
    private BigInteger maxMarketFee;
    private BigInteger issuerPermissions;
    private Integer flags;
    private String coreExchangeRateBaseAssetId;
    private BigInteger coreExchangeRateBaseAmount;
    private String coreExchangeRateQuoteAssetId;
    private BigInteger coreExchangeRateQuoteAmount;
    private List whitelistAuthorities;
    private List blacklistAuthorities;
    private List whitelistMarkets;
    private List blacklistMarkets;
    private String description;
    private List extensions;
    private String dynamicAssetDataId;

    public GetAsset(JSONObject object) {
        this.id = object.getString("id");
        this.symbol = object.getString("symbol");
        this.precision = object.getInteger("precision");
        this.issuer = object.getString("issuer");
        JSONObject options = object.getJSONObject("options");
        this.maxSupply = options.getBigInteger("max_supply");
        this.marketFeePercent = options.getBigInteger("market_fee_percent");
        this.issuerPermissions = options.getBigInteger("issuer_permissions");
        this.flags = options.getInteger("flags");
        JSONObject coreExchangeRate = options.getJSONObject("core_exchange_rate");
        this.coreExchangeRateBaseAssetId = coreExchangeRate.getJSONObject("base").getString("asset_id");
        this.coreExchangeRateBaseAmount = coreExchangeRate.getJSONObject("base").getBigInteger("amount");
        this.coreExchangeRateQuoteAssetId = coreExchangeRate.getJSONObject("quote").getString("asset_id");
        this.coreExchangeRateQuoteAmount = coreExchangeRate.getJSONObject("quote").getBigInteger("amount");
        this.whitelistAuthorities = options.getJSONArray("whitelist_authorities");
        this.blacklistAuthorities = options.getJSONArray("blacklist_authorities");
        this.whitelistMarkets = options.getJSONArray("whitelist_markets");
        this.blacklistMarkets = options.getJSONArray("blacklist_markets");
        this.description = options.getString("description");
        this.extensions = options.getJSONArray("extensions");
        this.dynamicAssetDataId = object.getString("dynamic_asset_data_id");
    }
}
