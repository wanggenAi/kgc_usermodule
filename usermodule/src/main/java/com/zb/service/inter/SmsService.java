package com.zb.service.inter;

public interface SmsService {

    public String sendPhoneCode(String userName);

    public String sendEmailCode(String userName);
}
