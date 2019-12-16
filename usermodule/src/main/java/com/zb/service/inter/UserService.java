package com.zb.service.inter;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    long authUserLogin(HttpServletRequest req);
}
