package com.zb.entity.respentity;

public class ResultData {
    private String code;

    private String msg;

    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResultData success(Object data) {
        return resultData(ResponseCode.SUCCESS.getVal(), ResponseCode.SUCCESS.getMsg(), data);
    }

    public static ResultData success(Object data, String msg) {
        return resultData(ResponseCode.SUCCESS.getVal(), msg, data);
    }

    public static ResultData fail(String code, String msg) {
        return resultData(code, msg, null);
    }

    public static ResultData fail(String msg) {
        return resultData(ResponseCode.ERROR.getVal(), msg, null);
    }

    public static ResultData fail(String code, String msg, Object data) {
        return resultData(code, msg, data);
    }

    private static ResultData resultData(String code, String msg, Object data) {
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMsg(msg);
        resultData.setData(data);
        return resultData;
    }
}
