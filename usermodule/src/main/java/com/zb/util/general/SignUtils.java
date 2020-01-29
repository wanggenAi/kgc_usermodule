package com.zb.util.general;

import com.zb.entity.TbSignIn;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 签到表工具类
 *
 * @author 王根
 * @date 2019-12-17 20:52:03
 */
public class SignUtils {

    /**
     * 获取默认值，所有的bit都为0，也就是没有任何的签到数据
     *
     * @return
     */
    public static String defaultSignHistory() {
        byte[] encodeData = new byte[46];
        return new BASE64Encoder().encode(encodeData);
    }

    /**
     * 根据签到历史转换成字节数组
     *
     * @param signHistory
     * @return
     */
    public static byte[] signHistoryToByte(String signHistory) {
        try {
            return new BASE64Decoder().decodeBuffer(signHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查天是否超出范围 0-355|366
     *
     * @param dayOfYear
     * @throws Exception
     */
    private static void checkOutOfDay(int dayOfYear) throws Exception {
        LocalDate localDate = LocalDate.now();
        int maxDay = localDate.isLeapYear() ? 366 : 365;
        if (dayOfYear <= 0 || dayOfYear > maxDay) {
            throw new Exception("the param dayOfYear " + dayOfYear + " is out of [0-" + maxDay + "]");
        }
    }

    /**
     * 校验月份是否合法
     *
     * @param month
     * @throws Exception
     */
    private static void checkOutOfMonth(int month) throws Exception {
        if (month <= 0 || month > 12) {
            throw new Exception("month is not available");
        }
    }

    /**
     * 获取某月的数据
     */
    public static List<String> getSignHistoryByMonth(String signHistory, int month) {
        // checkOutOfMonth(month);
        // start 本月第一天是当年的第几天
        int year = LocalDate.now().getYear();
        LocalDate localDate = LocalDate.of(year, month, 1);
        int start = localDate.getDayOfYear();
        int dayOfMonth = localDate.lengthOfMonth(); // 本月总共有多少天
        localDate = localDate.withDayOfMonth(dayOfMonth);
        int end = localDate.getDayOfYear();
        byte[] data = signHistoryToByte(signHistory);
        List<String> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (isSign(data, i)) {
                list.add(LocalDate.ofYearDay(year, i).toString());
            }
        }
        return list;
    }

    /**
     * 获取所有签到日期
     *
     * @param signInList
     * @return
     */
    public static List<String> getSignHistoryAll(List<TbSignIn> signInList) {
        List<String> list = new ArrayList<>();
        for (TbSignIn tbSignIn : signInList) {
            byte[] data = signHistoryToByte(tbSignIn.getSign_history());
            LocalDate localDate = LocalDate.ofYearDay(tbSignIn.getSign_year(), 1);
            int endday = localDate.isLeapYear() ? 366 : 365;
            for (int i = 1; i <= endday; i++) {
                if (isSign(data, i)) {
                    list.add(LocalDate.ofYearDay(tbSignIn.getSign_year(), i).toString());
                }
            }
        }
        return list;
    }

    /**
     * 签到的方法
     *
     * @param signHistory
     * @param dayOfYear
     * @return
     */
    public static String sign(String signHistory, int dayOfYear) {
        byte[] decodeResult = signHistoryToByte(signHistory);
        // index 该天所在的数组的字节位置
        int index = dayOfYear / 8;
        int offset = dayOfYear % 8; // 第index个数组的第offset位置
        byte data = decodeResult[index];
        data = (byte) (data | (1 << (7 - offset)));
        decodeResult[index] = data;
        return new BASE64Encoder().encode(decodeResult);
    }

    /**
     * 验证某天是否签到
     *
     * @param data
     * @param dayOfYear
     * @return
     */
    public static boolean isSign(byte[] data, int dayOfYear) {
        int index = dayOfYear / 8;
        int offset = dayOfYear % 8;
        int flag = data[index] & (1 << (7 - offset));
        return flag == 0 ? false : true;
    }

    public static int getMaxContinuitySingDay(String signHistory) {
        int maxCount = 0;
        LocalDate localDate = LocalDate.now();
        int curDayOfYear = localDate.getDayOfYear();
        byte[] data = signHistoryToByte(signHistory);
        // 从今天开始往前遍历
        while (curDayOfYear > 0) {
            if (isSign(data, curDayOfYear)) {
                maxCount++;
                curDayOfYear--;
            } else {
                break;
            }
        }
        return maxCount;
    }
}
