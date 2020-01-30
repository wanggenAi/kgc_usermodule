package com.zb.service.imp;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.zb.service.inter.SmsService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;
import com.zb.util.general.MailUtil;

import java.util.HashMap;


public class SmsServiceImp implements SmsService {

    /**
     * 向用户手机发送验证码
     *
     * @param userName
     * @return
     */
    public String sendPhoneCode(String userName) {
        //https://www.yuntongxun.com/member/main
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init("app.cloopen.com", "8883");
        sdk.setAccount("8aaf07086f17620f016f322ee0cb1239", "d17fcce703144a2693e2f0586b72ae30");
        sdk.setAppId("8a216da86f9cc12f016ff5c2a0742043");
        int rcode = randomCode();
        HashMap result = sdk.sendTemplateSMS(userName, "1", new String[]{rcode + "", "1"});
        if ("000000".equals(result.get("statusCode"))) {
            // 发送成功后添加到redis数据库中并设置过期时间
            JedisUtils.setex(userName, Constant.VERIFY_CODE_EXPIRE, rcode+"");
            return Integer.toString(rcode);
        }
        return null;
    }

    /**
     * 向用户的邮箱发送验证码
     *
     * @param userName
     * @return
     */
    public String sendEmailCode(String userName) {
        String rcode = randomCode()+"";
        MailUtil mailUtil = new MailUtil(userName, rcode);
        // 单独启动线程去发送邮件
        new Thread(() -> mailUtil.run()).start();
        // 将数据添加到redis中
        JedisUtils.setex(userName, Constant.VERIFY_CODE_EXPIRE, rcode+"");
        return rcode;
    }

    private int randomCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    public static void main(String[] args) {
        SmsServiceImp smsServiceImp = new SmsServiceImp();
        if (smsServiceImp.sendPhoneCode("18652154225")!=null) {
            System.out.println("发送成功");
        }else
            System.out.println("发送失败");
    }
}
