package com.zb.entity;

public class DayScoreRule {
    private int plate_id;
    private int opera_id;
    private int score;
    private int limitscore;
    private String platename;
    private String operaname;

    public int getPlate_id() {
        return plate_id;
    }

    public void setPlate_id(int plate_id) {
        this.plate_id = plate_id;
    }

    public int getOpera_id() {
        return opera_id;
    }

    public void setOpera_id(int opera_id) {
        this.opera_id = opera_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLimitscore() {
        return limitscore;
    }

    public void setLimitscore(int limitscore) {
        this.limitscore = limitscore;
    }

    public String getPlatename() {
        return platename;
    }

    public void setPlatename(String platename) {
        this.platename = platename;
    }

    public String getOperaname() {
        return operaname;
    }

    public void setOperaname(String operaname) {
        this.operaname = operaname;
    }
}
