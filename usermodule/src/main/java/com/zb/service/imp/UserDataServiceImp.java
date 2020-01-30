package com.zb.service.imp;

import com.zb.dao.imp.UserDataDaoImp;
import com.zb.entity.KgcUser;
import com.zb.entity.UserData;
import com.zb.service.inter.UserDataService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用来频繁修改用户本身数据的业务类
 */
public class UserDataServiceImp implements UserDataService {
    private UserDataDaoImp userDataDaoImp = new UserDataDaoImp();

    @Override
    public int getMaxLevelScore(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        return userDataDaoImp.getMaxVal2Level(id);
    }

    @Override
    public boolean updateUser(HttpServletRequest req) {
        KgcUser kgcUser = new KgcUser();
        kgcUser.setRealname(req.getParameter("realname"));
        kgcUser.setSex(Integer.parseInt(req.getParameter("sex")));
        kgcUser.setBirthday(Long.parseLong(req.getParameter("birthday")));
        kgcUser.setSchool(req.getParameter("school"));
        kgcUser.setProfessional(req.getParameter("professional"));
        kgcUser.setAddress_city(Integer.parseInt(req.getParameter("address_city")));
        kgcUser.setSignname(req.getParameter("signname"));
        kgcUser.setId(Long.parseLong(req.getParameter("id")));
        return userDataDaoImp.updateUser(kgcUser);
    }

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

    /**
     * 给用户升级
     *
     * @param req
     * @return
     */
    private boolean updateUserLevel(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        UserData userData = userDataDaoImp.getUserData(id);
        // 判断用户当前的经验值是否超过了当前等级的最大值
        int maxVal = userDataDaoImp.getMaxVal2Level(userData.getLevel_id());
        if (userData.getCur_score() > maxVal) {
            userDataDaoImp.updateUserLevel(id);
            return true;
        }
        return false;
    }

    /**
     * 给用户增加积分
     *
     * @param req
     * @return
     */
    public void userAddScore(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        short num = Short.parseShort(req.getParameter("num"));
        // 给用户增加经验值
        userDataDaoImp.updateUserScore(id, num);
        updateUserLevel(req);
    }

    /**
     * 返回用户基本数据
     *
     * @param req
     * @return
     */
    public UserData getUserData(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        return userDataDaoImp.getUserData(id);
    }

    /**
     * 修改用户的昵称 uid nickName
     * @param req
     * @return
     */
    public boolean updateNickName(HttpServletRequest req){
        long id = Long.parseLong(req.getParameter("uid"));
        String nickName = req.getParameter("nickName");
        return userDataDaoImp.updateNickName(id, nickName);
    }
}
