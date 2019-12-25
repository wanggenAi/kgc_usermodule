package com.zb.service.inter;

public interface SmsService {

    public boolean sendPhoneCode(String userName);

    public boolean sendEmailCode(String userName);
}
