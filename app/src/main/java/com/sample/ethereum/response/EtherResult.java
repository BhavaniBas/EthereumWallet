package com.sample.ethereum.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EtherResult implements Parcelable {

    @SerializedName("blockNumber")
    @Expose
    private String blockNumber;
    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("nonce")
    @Expose
    private String nonce;
    @SerializedName("blockHash")
    @Expose
    private String blockHash;
    @SerializedName("transactionIndex")
    @Expose
    private String transactionIndex;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("gas")
    @Expose
    private long gas;
    @SerializedName("gasPrice")
    @Expose
    private long gasPrice;
    @SerializedName("isError")
    @Expose
    private String isError;
    @SerializedName("txreceipt_status")
    @Expose
    private String txreceiptStatus;
    @SerializedName("input")
    @Expose
    private String input;
    @SerializedName("contractAddress")
    @Expose
    private String contractAddress;
    @SerializedName("cumulativeGasUsed")
    @Expose
    private String cumulativeGasUsed;
    @SerializedName("gasUsed")
    @Expose
    private String gasUsed;
    @SerializedName("confirmations")
    @Expose
    private String confirmations;

    private EtherResult(Parcel in) {
        blockNumber = in.readString();
        timeStamp = in.readString();
        hash = in.readString();
        nonce = in.readString();
        blockHash = in.readString();
        transactionIndex = in.readString();
        from = in.readString();
        to = in.readString();
        value = in.readString();
        gas = in.readLong();
        gasPrice = in.readLong();
        isError = in.readString();
        txreceiptStatus = in.readString();
        input = in.readString();
        contractAddress = in.readString();
        cumulativeGasUsed = in.readString();
        gasUsed = in.readString();
        confirmations = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(blockNumber);
        dest.writeString(timeStamp);
        dest.writeString(hash);
        dest.writeString(nonce);
        dest.writeString(blockHash);
        dest.writeString(transactionIndex);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(value);
        dest.writeLong(gas);
        dest.writeLong(gasPrice);
        dest.writeString(isError);
        dest.writeString(txreceiptStatus);
        dest.writeString(input);
        dest.writeString(contractAddress);
        dest.writeString(cumulativeGasUsed);
        dest.writeString(gasUsed);
        dest.writeString(confirmations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EtherResult> CREATOR = new Creator<EtherResult>() {
        @Override
        public EtherResult createFromParcel(Parcel in) {
            return new EtherResult(in);
        }

        @Override
        public EtherResult[] newArray(int size) {
            return new EtherResult[size];
        }
    };

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getGas() {
        return gas;
    }

    public void setGas(long gas) {
        this.gas = gas;
    }

    public long getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(long gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getIsError() {
        return isError;
    }

    public void setIsError(String isError) {
        this.isError = isError;
    }

    public String getTxreceiptStatus() {
        return txreceiptStatus;
    }

    public void setTxreceiptStatus(String txreceiptStatus) {
        this.txreceiptStatus = txreceiptStatus;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public void setCumulativeGasUsed(String cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }
}
