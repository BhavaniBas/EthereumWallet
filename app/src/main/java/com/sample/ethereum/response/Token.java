package com.sample.ethereum.response;

import android.widget.CheckBox;


public class Token {

    private String tokenName;
    private boolean isSelected;

    public Token(String tokenName,boolean isSelected) {
        this.tokenName = tokenName;
        this.isSelected = isSelected;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
