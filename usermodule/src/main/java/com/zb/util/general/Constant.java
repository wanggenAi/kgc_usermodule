package com.zb.util.general;

public class Constant {
    public final static int PAGE_SIZE = 2;
    public final static long NOT_FOUND_UID = 0L;
    public final static String TOKEN_ERROR = "100";
    public final static int SING_TOTAL_TIME = 86400; // 今天总签到的时间限制
    public final static String SIGN_TOTAL_PREFIX = "signCount"; // 当天签到总人数键的后缀
    public final static String COUPON_REDIS_PREFIX = "coupon_";
    public final static int COUPON_REDIS_INDEX = 1; // 优惠券所在的redis数据库编号
}
