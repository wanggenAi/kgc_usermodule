package com.zb.dao.imp;

import com.zb.dao.inter.KgcUserDao;
import com.zb.entity.KgcUser;
import com.zb.util.database.BaseDao;

public class KgcUserDaoImp extends BaseDao<KgcUser> implements KgcUserDao {

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
     * 根据uid查询关注、粉丝..表
     */

}

