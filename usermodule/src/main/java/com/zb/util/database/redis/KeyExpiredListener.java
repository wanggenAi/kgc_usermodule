package com.zb.util.database.redis;

import com.zb.service.imp.CouponServiceImp;
import redis.clients.jedis.JedisPubSub;

public class KeyExpiredListener extends JedisPubSub {
    private CouponServiceImp couponService = new CouponServiceImp();
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
//        super.onPSubscribe(pattern, subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        System.out.println("redis传来消息：" + pattern + "有过期键:" + message);
        // 修改mysql数据库用户与优惠券关系表中优惠券的状态
        int id = Integer.parseInt(message.split("_")[1]);
        couponService.updateCouponExp(id);
    }
}
