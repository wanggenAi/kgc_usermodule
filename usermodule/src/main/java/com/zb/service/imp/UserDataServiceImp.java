package com.zb.service.imp;

import com.zb.dao.imp.UserDataDaoImp;
import com.zb.service.inter.UserDataService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用来频繁修改用户本身数据的业务类
 */
public class UserDataServiceImp implements UserDataService {
    private UserDataDaoImp userDataDaoImp = new UserDataDaoImp();

    // 关注
    public boolean chgFollowC(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        return userDataDaoImp.updateFollowCount(id, num);
    }

    // 粉丝
    public boolean chgFansC(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        return userDataDaoImp.updateFansCount(id, num);
    }

    // 点赞
    public boolean chgSupportC(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        return userDataDaoImp.updateSupportCount(id, num);
    }

    // 帖子
    public boolean chgInvitationC(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        return userDataDaoImp.updateInvitationCount(id, num);
    }

    // kb
    public boolean chgKbC(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        return userDataDaoImp.updateKbCount(id, num);
    }

    // 金额
    public boolean chgMoneyC(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        return userDataDaoImp.updateMoney(id, num);
    }
}
