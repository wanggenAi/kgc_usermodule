package com.zb.service.inter;

import com.zb.entity.UserData;

import javax.servlet.http.HttpServletRequest;

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
}
