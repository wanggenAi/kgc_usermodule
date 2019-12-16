package com.zb.servlet.login;

import com.alibaba.fastjson.JSON;
import com.zb.entity.respentity.ResultData;
import com.zb.service.imp.UserServiceImp;
import com.zb.service.inter.UserService;
import com.zb.util.database.JDBCUtil;
import com.zb.util.general.Constant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class ServletUserLogin extends HttpServlet {
    private UserService userService = new UserServiceImp();
    private String SESSION_CUSTOMER_NO_KEY = "session_customer_no_key";
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long uid = userService.authUserLogin(req);
        if (uid == Constant.NOT_FOUND_UID) { // 验证失败
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("用户或密码错误")));
            return;
        }
        req.getSession().setAttribute(SESSION_CUSTOMER_NO_KEY, uid);
        resp.sendRedirect("index.jsp");
    }
}
