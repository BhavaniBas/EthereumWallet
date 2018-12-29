package com.sample.ethereum.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("wallet")
    @Expose
    private String wallet;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("eth_balance")
    @Expose
    private String ethBalance;
    @SerializedName("decimals")
    @Expose
    private Integer decimals;
    @SerializedName("block")
    @Expose
    private Integer block;
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getEthBalance() {
        return ethBalance;
    }

    public void setEthBalance(String ethBalance) {
        this.ethBalance = ethBalance;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Integer getBlock() {
        return block;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
