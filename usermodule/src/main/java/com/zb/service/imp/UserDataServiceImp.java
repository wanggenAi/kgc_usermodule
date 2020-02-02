package com.zb.service.imp;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zb.dao.imp.UserDataDaoImp;
import com.zb.entity.KgcUser;
import com.zb.entity.PageInfo;
import com.zb.entity.UserData;
import com.zb.service.inter.UserDataService;
import com.zb.util.general.Constant;

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

    @Override
    //100 成功 101数据库操作失败  102 K币不足
    public int setKbRecord(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        String operaname = req.getParameter("operaname");
        String detail = req.getParameter("detail");
        short changekb = Short.parseShort(req.getParameter("changekb"));

        if (changekb < 0) {
            int dbkb = userDataDaoImp.getKbCount(uid);
            if (dbkb - changekb < 0) {
                return Constant.KB_NOT_ENOUGH;
            }
            if ((!userDataDaoImp.updateKbCount(uid, changekb)) || (!userDataDaoImp.setKbRecord(uid, operaname, detail, changekb))) {
                return Constant.DB_ERROR;
            }
            return Constant.DB_SUCCESS;
        }
        return (userDataDaoImp.setKbRecord(uid, operaname, detail, changekb) &&
                userDataDaoImp.updateKbCount(uid, changekb)) ? Constant.DB_SUCCESS : Constant.DB_ERROR;
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
     *
     * @param req
     * @return
     */
    public boolean updateNickName(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter("uid"));
        String nickName = req.getParameter("nickName");
        return userDataDaoImp.updateNickName(id, nickName);
    }

    /**
     * 获取K币记录分页数据，传入参数
     * pagenum pagesize uid
     *
     * @param req
     * @return
     */
    public PageInfo getKbRecord(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        int pageNum = Integer.parseInt(req.getParameter("pagenum"));
        int pageSize = Integer.parseInt(req.getParameter("pagesize"));
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrentPage(pageNum);
        pageInfo.setPageSize(pageSize);
        return userDataDaoImp.getKbRecord(uid, pageInfo);
    }
}
