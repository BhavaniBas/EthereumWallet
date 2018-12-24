package com.sample.ethereum.response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EthereumBalance implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private String result;

    protected EthereumBalance(Parcel in) {
        status = in.readString();
        message = in.readString();
        result = in.readString();
    }

    public static final Creator<EthereumBalance> CREATOR = new Creator<EthereumBalance>() {
        @Override
        public EthereumBalance createFromParcel(Parcel in) {
            return new EthereumBalance(in);
        }

        @Override
        public EthereumBalance[] newArray(int size) {
            return new EthereumBalance[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
        dest.writeString(result);
    }
}
