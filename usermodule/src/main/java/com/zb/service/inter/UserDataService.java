package com.zb.service.inter;

import com.zb.entity.PageInfo;
import com.zb.entity.UserData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserDataService {
    public boolean chgFollowC(HttpServletRequest req);
    public boolean chgFansC(HttpServletRequest req);
    public boolean chgSupportC(HttpServletRequest req);
    public boolean chgInvitationC(HttpServletRequest req);
    public boolean chgKbC(HttpServletRequest req);
    public boolean chgMoneyC(HttpServletRequest req);
    public void userAddScore(HttpServletRequest req);
    public UserData getUserData(HttpServletRequest req);
    public int getMaxLevelScore(HttpServletRequest req);
    public boolean updateUser(HttpServletRequest req);
    public boolean updateNickName(HttpServletRequest req);
    public PageInfo getKbRecord(HttpServletRequest req);

    /**
     * 存储K币操作记录
     * 返回整型 100 成功 101数据库操作失败  102 K币不足
     * @param req
     * @return
     */
    public int setKbRecord(HttpServletRequest req);
}
