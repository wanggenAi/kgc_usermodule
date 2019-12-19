package com.zb.service.inter;

import javax.servlet.http.HttpServletRequest;

public interface UserDataService {
    public boolean chgFollowC(HttpServletRequest req);
    public boolean chgFansC(HttpServletRequest req);
    public boolean chgSupportC(HttpServletRequest req);
    public boolean chgInvitationC(HttpServletRequest req);
    public boolean chgKbC(HttpServletRequest req);
    public boolean chgMoneyC(HttpServletRequest req);
}
