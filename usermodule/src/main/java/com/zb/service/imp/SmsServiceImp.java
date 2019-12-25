package com.zb.service.imp;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.zb.service.inter.SmsService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;

import java.util.HashMap;


public class SmsServiceImp implements SmsService {

    /**
     * 向用户手机发送验证码
     *
     * @param userName
     * @return
     */
    public boolean sendPhoneCode(String userName) {
        //https://www.yuntongxun.com/member/main
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init("app.cloopen.com", "8883");
        sdk.setAccount("8aaf07086f17620f016f322ee0cb1239", "d17fcce703144a2693e2f0586b72ae30");
        sdk.setAppId("8aaf07086f17620f016f322ee12d1240");
        int rcode = randomCode();
        HashMap result = sdk.sendTemplateSMS(userName, "1", new String[]{rcode + "", "1"});
        if ("000000".equals(result.get("statusCode"))) {
            // 发送成功后添加到redis数据库中并设置过期时间
            JedisUtils.setnx(userName, rcode+"", Constant.VERIFY_CODE_EXPIRE);
            return true;
        }
        return false;
    }

    /**
     * 向用户的邮箱发送验证码
     *
     * @param userName
     * @return
     */
    public boolean sendEmailCode(String userName) {

        return true;
    }

    private int randomCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    public static void main(String[] args) {
        SmsServiceImp smsServiceImp = new SmsServiceImp();
        if (smsServiceImp.sendPhoneCode("18652154225")) {
            System.out.println("发送成功");
        }else
            System.out.println("发送失败");
    }
}
