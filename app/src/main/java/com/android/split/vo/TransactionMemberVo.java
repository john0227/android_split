package com.android.split.vo;

import androidx.annotation.NonNull;

import java.util.Locale;

public class TransactionMemberVo {

    private String sender;
    private String rcver;
    private double amount;
    private boolean replace;

    public TransactionMemberVo(String sender, String rcver, double amount, boolean replace) {
        this.sender = sender;
        this.rcver = rcver;
        this.amount = amount;
        this.replace = replace;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRcver() {
        return rcver;
    }

    public void setRcver(String rcver) {
        this.rcver = rcver;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean shouldReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US, "%s should send %s %f", sender, rcver, amount);
    }

}
