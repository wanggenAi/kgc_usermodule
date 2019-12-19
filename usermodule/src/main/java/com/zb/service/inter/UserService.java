package com.zb.service.inter;

import com.zb.entity.KgcUser;
import com.zb.entity.TbSignIn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    long authUserLogin(HttpServletRequest req, HttpServletResponse resp);

    KgcUser getUserById(HttpServletRequest req, HttpServletResponse resp);
    public long getTotalSignCount();
    public TbSignIn getUserSignById(HttpServletRequest req);
    public TbSignIn sign(HttpServletRequest req);
}
