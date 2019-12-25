package com.zb.dao.imp;

import com.zb.util.database.BaseDao;

import java.util.List;
import java.util.Map;


public class CouponDaoImp extends BaseDao {

    /**
     * 获取最新增加的优惠券们
     *
     * @return
     */
    public List<Map<String, Object>> getNewCoupon() {
        String sql = "select * from coupon where isnew = 1";
        return selectMany(sql);
    }

    /**
     * 设置优惠券的isnew状态为0
     *
     * @return
     */
    public boolean updateIsNew() {
        String sql = "update coupon set isnew = 0";
        return executeUpdate(sql) > 0 ? true : false;
    }


    /**
     * 返回某个用户的所有优惠券们
     *
     * @param uid
     * @return
     */
    public List<Map<String, Object>> getCouponFromUser(long uid) {
        String sql = "select * from userforcoupon u inner join coupon c on u.cid = c.id and uid = ?";
        return selectMany(sql, uid);
    }

    /**
     * 修改用户某张优惠券的使用状态
     *
     * @param uid
     * @param cid
     * @return
     */
    public boolean updateCouponUsed(long uid, int cid) {
        String sql = "update userforcoupon set bool_used = 1 where uid = ? and cid = ?";
        return executeUpdate(sql, uid, cid) > 0 ? true : false;
    }

    /**
     * 修改用户优惠券们的过期状态
     *
     * @param cid
     * @return
     */
    public boolean updateCouponExp(int cid) {
        String sql = "update userforcoupon set bool_expired = 1 where cid = ?";
        return executeUpdate(sql, cid) > 0 ? true : false;
    }

    /**
     * 将新增的优惠券插入到用户与优惠券关系表中
     * @return
     */
    public boolean insertUserForCoupon() {
        String sql = "insert into userforcoupon(cid,uid) " +
                "select c.id cid,k.id uid from kgc_user k " +
                "inner join coupon c on c.isnew = 1;";
        return executeUpdate(sql) > 0 ? true : false;
    }

    /**
     * 用户初始化优惠券
     * @param uid
     * @return
     */
    public boolean initUserForCoupon(long uid) {
        String sql = "insert into userforcoupon(cid,uid) " +
                "select c.id cid,k.id uid from kgc_user k " +
                "inner join coupon c on c.isnew = 1 and k.id = ?";
        return executeUpdate(sql) > 0 ? true : false;
    }

    /**
     * 插入优惠券主表
     * @param title_name
     * @param save_money
     * @param createtime
     * @param expiretime
     */
    public void insertCoupon(String title_name,double save_money, long createtime, long expiretime) {
        String sql = "insert into coupon(title_name, save_money, createtime, expiretime)" +
                "values(?,?,?,?)";
        executeUpdate(sql, title_name, save_money, createtime, expiretime);
    }
}
