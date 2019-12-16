package com.zb.entity.respentity;

public enum ResponseCode {
    SUCCESS("200", "成功"),
    ERROR("500", "操作失败");

    private String val;
    private String msg;
    private ResponseCode(String value, String msg) {
        this.val = value;
        this.msg = msg;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
