package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.entity.KgcUser;
import com.zb.service.inter.UserService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;
import com.zb.util.jwt.JwtHelper;
import com.zb.util.jwt.SecretConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserServiceImp implements UserService {
    KgcUserDaoImp kud = new KgcUserDaoImp();

    public long authUserLogin(HttpServletRequest req, HttpServletResponse resp) {
        String name = req.getParameter("userName");
        String pwd = req.getParameter("password");
        long uid = kud.getUser(name, pwd);
        if (uid == Constant.NOT_FOUND_UID) { // 用户名或密码错误
            return uid;
        }
        // 要生成token 并且将token/uid 存入到redis中，并设置过期时间
        String jsonKey = saveUid2Redis(uid);
        resp.addHeader("User-Token", jsonKey);
        return uid; // 返回给控制层
    }

    private String saveUid2Redis(long uid) {
        String jsonKey = JwtHelper.generateJWT(String.valueOf(uid));
        JedisUtils.setex(jsonKey, SecretConstant.EXPIRESSECOND, String.valueOf(uid));
        return jsonKey;
    }

    /**
     * 返回客户端用户实体
     */
    public KgcUser getUserById(HttpServletRequest req, HttpServletResponse resp) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return kud.getUserById(uid);
    }
}
