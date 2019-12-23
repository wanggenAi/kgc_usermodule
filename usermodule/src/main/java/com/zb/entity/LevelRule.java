package com.zb.entity;

public class LevelRule {
    private int id;
    private int minval;
    private int maxval;
    private String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinval() {
        return minval;
    }

    public void setMinval(int minval) {
        this.minval = minval;
    }

    public int getMaxval() {
        return maxval;
    }

    public void setMaxval(int maxval) {
        this.maxval = maxval;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
