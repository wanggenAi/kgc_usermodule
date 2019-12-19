package com.zb.entity;

import java.util.List;

public class TbSignIn {
    private long uid;
    private String sign_history;
    private int sign_count;
    private int continue_sign;
    private long last_sign_time;
    private List<String> signList;
    private boolean sign;

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public List<String> getSignList() {
        return signList;
    }

    public void setSignList(List<String> signList) {
        this.signList = signList;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getSign_history() {
        return sign_history;
    }

    public void setSign_history(String sign_history) {
        this.sign_history = sign_history;
    }

    public int getSign_count() {
        return sign_count;
    }

    public void setSign_count(int sign_count) {
        this.sign_count = sign_count;
    }

    public int getContinue_sign() {
        return continue_sign;
    }

    public void setContinue_sign(int continue_sign) {
        this.continue_sign = continue_sign;
    }

    public long getLast_sign_time() {
        return last_sign_time;
    }

    public void setLast_sign_time(long last_sign_time) {
        this.last_sign_time = last_sign_time;
    }
}
