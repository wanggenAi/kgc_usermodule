package com.zb.entity;

public class DataResult {
    private String result;
    private Object list;

    public DataResult() {
    }

    public DataResult(String result, Object list) {
        this.result = result;
        this.list = list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getList() {
        return list;
    }

    public void setList(Object list) {
        this.list = list;
    }
}
