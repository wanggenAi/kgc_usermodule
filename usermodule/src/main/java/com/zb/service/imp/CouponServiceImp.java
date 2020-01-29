package com.zb.service.imp;

import com.zb.dao.imp.CouponDaoImp;
import com.zb.service.inter.CouponService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @date 2019-12-22 19:15:39
 * 优惠券操作业务类，提供给优惠券相关的定时任务来使用
 */
public class CouponServiceImp implements CouponService {

    private CouponDaoImp couponDaoImp = new CouponDaoImp();

    /**
     * 假设每天会定时更新优惠券到coupo
     * n表中，新数据标记为isnew=1
     * 要做两件事情，采用多线程方式执行
     * 1.将新数据插入到userforcoupon表中
     * 2.将新数据id值计算出过期时间批量插入到redis中
     */
    public void addCouToRM() {
        // 开启多线程
        Thread threadMysql = new Thread(() -> couponDaoImp.insertUserForCoupon());
        Thread threadRedis = new Thread(new Coupon2Redis());
        threadMysql.start();
        threadRedis.start();
    }

    class Coupon2Redis implements Runnable {
        @Override
        public void run() {
            // 查出需要新增的优惠券id
            List<Map<String, Object>> listCoupon = couponDaoImp.getNewCoupon();
            Jedis jedis = JedisUtils.getJedis();
            // 选择优惠券所在的数据库
            jedis.select(Constant.COUPON_REDIS_INDEX);
            Pipeline p = jedis.pipelined();
            try {
                for (Map<String, Object> stringObjectMap : listCoupon) {
                    int second = (int) (((long) stringObjectMap.get("expiretime") - (long) stringObjectMap.get("createtime")) / 1000);
                    p.setex(Constant.COUPON_REDIS_PREFIX + stringObjectMap.get("id"), second, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                p.sync();
                JedisUtils.close(jedis);
            }
        }
    }

    /**
     * 根据redis监听到的过期优惠券id修改数据库优惠券关系表设置为已过期
     *
     * @param cid
     * @return
     */
    public boolean updateCouponExp(int cid) {
        return couponDaoImp.updateCouponExp(cid);
    }

    /**
     * 获取某用户所有优惠券们
     *
     * @param req
     * @return List<Map<String,Object>>
     */
    public List<Map<String, Object>> getCouponFromUser(@NotNull HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return couponDaoImp.getCouponFromUser(uid);
    }

    /**
     * 修改某用户某张优惠券的使用状态
     *
     * @param req
     * @return boolean
     */
    public boolean updateCouponUsed(@NotNull HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        int cid = Integer.parseInt(req.getParameter("cid"));
        return couponDaoImp.updateCouponUsed(uid, cid);
    }

    /**
     * 插入优惠券主数据表
     * HttpServletRequest req
     */
    public void insertCoupon() {
        /*String title_name = req.getParameter("title_name");
        double save_money = Double.parseDouble(req.getParameter("save_money"));
        long createtime = Long.parseLong(req.getParameter("createtime"));
        long expiretime = Long.parseLong(req.getParameter("expiretime"));*/
        long createtime = System.currentTimeMillis();
        long expiretime = createtime + 60000;
        couponDaoImp.insertCoupon("Java测试" + Math.random(), 30, createtime, expiretime);
    }

    /**
     * 设置优惠券的isnew状态为0
     *
     * @return
     */
    public boolean updateIsNew() {
        return couponDaoImp.updateIsNew();
    }
}

