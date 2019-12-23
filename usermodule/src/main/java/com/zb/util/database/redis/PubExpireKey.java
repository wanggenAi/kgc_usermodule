package com.zb.util.database.redis;

import com.zb.util.general.Constant;
import redis.clients.jedis.Jedis;

public class PubExpireKey implements Runnable{

    @Override
    public void run() {
        Jedis jedis = JedisUtils.getJedis();
        jedis.psubscribe(new KeyExpiredListener(), "__key*@" + Constant.COUPON_REDIS_INDEX + "__:expired");
    }
}
