package com.zb.servlet.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long uid = userService.authUserLogin(req, resp);
        if (uid == Constant.NOT_FOUND_UID) { // 验证失败
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("用户或密码错误")));
            return;
        }
        resp.getWriter().write(JSONObject.toJSONString(ResultData.success(uid, "登录成功")));
    }
}
