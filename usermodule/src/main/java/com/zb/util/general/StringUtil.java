package com.zb.util.general;

import java.time.LocalDate;

public class StringUtil {

    /**
     * 将字符串首字母转为大写
     *
     * @param str 要转的字符串
     * @return 返回转换后的首字母大写的字符串
     */
    public static String toUpper(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 根据当前时间生成redis的键名
     */
    public static String getSignNameForRedis() {
        LocalDate localDate = LocalDate.now();
        return localDate.toString() + Constant.SIGN_TOTAL_PREFIX; // 返回当天的日期
    }
}
