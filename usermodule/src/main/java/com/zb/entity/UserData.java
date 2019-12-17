package com.zb.entity;

public class UserData {
    private long id; // 用户id
    private int follow_count;
    private int fans_count;
    private int support_count;
    private int invitation_count;
    private int kb_count;
    private double money;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public int getSupport_count() {
        return support_count;
    }

    public void setSupport_count(int support_count) {
        this.support_count = support_count;
    }

    public int getInvitation_count() {
        return invitation_count;
    }

    public void setInvitation_count(int invitation_count) {
        this.invitation_count = invitation_count;
    }

    public int getKb_count() {
        return kb_count;
    }

    public void setKb_count(int kb_count) {
        this.kb_count = kb_count;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public UserData() {
    }

    public UserData(long id, int follow_count, int fans_count, int support_count, int invitation_count, int kb_count, double money) {
        this.id = id;
        this.follow_count = follow_count;
        this.fans_count = fans_count;
        this.support_count = support_count;
        this.invitation_count = invitation_count;
        this.kb_count = kb_count;
        this.money = money;
    }
}
