package com.zb.dao.imp;

import com.zb.dao.inter.KgcUserDao;
import com.zb.entity.District;
import com.zb.entity.KgcUser;
import com.zb.entity.TbSignIn;
import com.zb.util.database.BaseDao;

import java.util.List;
import java.util.Map;


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
     *
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
     *
     * @param cityId
     * @return
     */
    public District getDistrictByCity(int cityId) {
        String sql = "select * from district where id = ?";
        return selectOne(sql, District.class, cityId);
    }

    /**
     * 根据省份id获取子市区的信息
     *
     * @param provId
     * @return
     */
    public List<District> getCityByProvince(int provId) {
        String sql = "select * from district where pid = ?";
        return selectMany(sql, District.class, provId);
    }

    /**
     * 获取所有的省份的记录信息
     *
     * @return
     */
    public List<District> getAllProvince() {
        String sql = "select * from district where type = 1";
        return selectMany(sql, District.class);
    }

    /**
     * 修改用户的头像url
     *
     * @param url
     * @param uid
     * @return
     */
    public boolean updateHeaderImg(String url, long uid) {
        String sql = "update kgc_user set head_url = ? where id = ?";
        return executeUpdate(sql, url, uid) > 0 ? true : false;
    }

    /**
     * 查询用户头像信息
     *
     * @return
     */
    public String getUserHeaderImg(long uid) {
        String sql = "select head_url from kgc_user where id = ?";
        List<Map<String, Object>> list = selectMany(sql, uid);
        return (String) (list.get(0).get("head_url"));
    }

    /**
     * 添加用户到用户表中
     *
     * @param kgcUser
     * @return
     */
    public boolean addKgcUser(KgcUser kgcUser) {
        String sql = "insert into kgc_user(id, username, password, usertype, email, phone, head_url, createtime," +
                " updatetime) values(?,?,?,?,?,?,?,?,?)";
        return executeUpdate(sql, kgcUser.getId(), kgcUser.getUsername(), kgcUser.getPassword(),
                kgcUser.getUsertype(), kgcUser.getEmail(), kgcUser.getPhone(), kgcUser.getHead_url(),
                kgcUser.getCreatetime(), kgcUser.getUpdatetime()) > 0 ? true : false;
    }

    /**
     * 初始化一条用户的签到的签到信息
     *
     * @param uid
     * @param sign_history
     * @return
     */
    public boolean initUserSign(long uid, String sign_history) {
        String sql = "insert into tb_signin(uid, sign_history) values(?,?)";
        return executeUpdate(sql, uid, sign_history) > 0 ? true : false;
    }

    public static void main(String[] args) {

    }

}




