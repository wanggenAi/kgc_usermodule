package com.zb.util.general;

public class Constant {
    public final static int PAGE_SIZE = 2;
    public final static long NOT_FOUND_UID = 0L;
    public final static String TOKEN_ERROR = "100";
    public final static int SING_TOTAL_TIME = 86400; // 今天总签到的时间限制
    public final static String SIGN_TOTAL_PREFIX = "signCount"; // 当天签到总人数键的后缀
    public final static String COUPON_REDIS_PREFIX = "coupon_";
    public final static int COUPON_REDIS_INDEX = 1; // 优惠券所在的redis数据库编号
    // 用户默认头像
    public final static String USER_DEFAULT_HEADER = "headerdefault.jpg";
    // 16位的MD5加密密码
    public final static int PWD_MD5_LENGTH16 = 16;
    /**
     * 验证码过期时间，默认是300秒
     */
    public final static int VERIFY_CODE_EXPIRE = 300;
    /**
     * 验证码可重新设置的时间，默认60秒
     */
    public final static int VERIFY_CODE_UPDATE = 60;

    /**
     * kb不足
     */
    public final static int KB_NOT_ENOUGH = 102;

    /**
     * 数据库操作异常
     */
    public final static int DB_ERROR = 101;
    public final static int DB_SUCCESS = 100;

}
