package com.zb.servlet.login.kgcuser;

import com.alibaba.fastjson.JSON;
import com.zb.entity.KgcUser;
import com.zb.service.imp.UserServiceImp;
import com.zb.service.inter.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/getUser")
public class ServletGetUser extends HttpServlet {
    private UserService userService = new UserServiceImp();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        KgcUser kgcUser = userService.getUserById(req, resp);
        resp.getWriter().write(JSON.toJSONString(kgcUser));
    }
}
