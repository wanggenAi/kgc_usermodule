package com.zb.service.imp;

import com.zb.service.inter.SmsService;

public class SmsServiceImp implements SmsService {

    /**
     * 向用户手机发送验证码
     * @param userName
     * @return
     */
    public boolean sendPhoneCode(String userName) {

        return true;
    }

    /**
     * 向用户的邮箱发送验证码
     * @param userName
     * @return
     */
    public boolean sendEmailCode(String userName) {
        return true;
    }
}
