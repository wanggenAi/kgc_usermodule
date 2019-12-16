package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.service.inter.UserService;
import com.zb.util.general.Constant;

public class UserServiceImp implements UserService {
    KgcUserDaoImp kud = new KgcUserDaoImp();

    @Override
    public long authUserLogin(String name, String pwd) {
        long uid = kud.getUser(name, pwd);
        if (uid == Constant.NOT_FOUND_UID) { // 用户名或密码错误
            return uid;
        }
        // 要生成token 并且将token/uid 存入到redis中，并设置过期时间
        return uid; // 返回给控制层
    }
}
