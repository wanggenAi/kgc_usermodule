package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.service.inter.UserService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;
import com.zb.util.jwt.JwtHelper;
import com.zb.util.jwt.SecretConstant;

import javax.servlet.http.HttpServletRequest;

public class UserServiceImp implements UserService {
    KgcUserDaoImp kud = new KgcUserDaoImp();

    public long authUserLogin(HttpServletRequest req) {
        String name = req.getParameter("userName");
        String pwd = req.getParameter("password");
        long uid = kud.getUser(name, pwd);
        if (uid == Constant.NOT_FOUND_UID) { // 用户名或密码错误
            return uid;
        }
        // 要生成token 并且将token/uid 存入到redis中，并设置过期时间
        saveUid2Redis(uid, req.getHeader("User-Agent"));
        return uid; // 返回给控制层
    }

    private void saveUid2Redis(long uid, String userAgent) {
        String jsonKey = JwtHelper.generateJWT(String.valueOf(uid), userAgent);
        JedisUtils.setex(jsonKey, SecretConstant.EXPIRESSECOND, String.valueOf(uid));
    }
}
