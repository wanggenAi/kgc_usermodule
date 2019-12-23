package com.zb.dao.imp;

import com.zb.dao.inter.KgcUserDao;
import com.zb.entity.District;
import com.zb.entity.KgcUser;
import com.zb.entity.TbSignIn;
import com.zb.util.database.BaseDao;

import java.util.List;


public class KgcUserDaoImp extends BaseDao implements KgcUserDao {

    /**
     * 判断用户名是否存在
     *
     * @param name
     * @return
     */
    public boolean isExistUser(String name) {
        String sql = "select username from kgc_user where username = ?";
        if (isExist(sql, name)) {
            return true;
        }
        return false;
    }

    /**
     * 验证用户密码是否正确，返回用户UID
     *
     * @param name
     * @param pwd
     * @return KgcUser
     */
    public long getUser(String name, String pwd) {
        String sql = "select id from kgc_user where username = ? and password = ?";
        return getUid(sql, name, pwd);
    }

    /**
     * 根据uid查询用户实体
     */
    public KgcUser getUserById(long uid) {
        String sql = "select * from kgc_user where id = ?";
        return selectOne(sql, KgcUser.class, uid);
    }

    /**
     * 查询用户签到信息
     */
    public TbSignIn getSignById(long uid) {
        String sql = "select * from tb_signin where uid = ?";
        return selectOne(sql, TbSignIn.class, uid);
    }

    /**
     * 当签到成功后，自动修改用户签到表数据
     *
     * @param continue_sign
     * @param last_sign_time
     * @param uid
     * @return
     */
    public boolean upSignAfterSuccess(int continue_sign, long last_sign_time, long uid, String signHistory) {
        String sql = "update tb_signin set sign_count = sign_count+1,continue_sign=?,last_sign_time=?,sign_history=? where uid = ?";
        return executeUpdate(sql, continue_sign, last_sign_time, signHistory, uid) > 0 ? true : false;
    }

    /**
     * 用户签到
     * @param signHistory
     * @param uid
     * @return
     */
    public boolean userSign(String signHistory, long uid) {
        String sql = "update tb_signin set sign_history = ? where uid = ?";
        return executeUpdate(sql, signHistory, uid) > 0 ? true : false;
    }

    /**
     * 根据城市id获取地区记录
     * @param cityId
     * @return
     */
    public District getDistrictByCity(int cityId) {
        String sql = "select * from district where id = ?";
        return selectOne(sql, District.class, cityId);
    }

    /**
     * 根据省份id获取子市区的信息
     * @param provId
     * @return
     */
    public List<District> getCityByProvince(int provId) {
        String sql = "select * from district where pid = ?";
        return selectMany(sql, District.class, provId);
    }

    /**
     * 获取所有的省份的记录信息
     * @return
     */
    public List<District> getAllProvince() {
        String sql = "select * from district where type = 1";
        return selectMany(sql, District.class);
    }

    public static void main(String[] args) {

    }

}




