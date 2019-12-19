package com.zb.servlet.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zb.entity.KgcUser;
import com.zb.entity.TbSignIn;
import com.zb.entity.respentity.ResultData;
import com.zb.service.imp.UserServiceImp;
import com.zb.service.inter.UserService;
import com.zb.util.database.JDBCUtil;
import com.zb.util.general.Constant;
import com.zb.util.general.EmptyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "*.do")
public class ServletUserHandler extends HttpServlet {

    private UserService userService = new UserServiceImp();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI(); // 获取uri路径
        String methodName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        // 利用反射调用请求的方法
        try {
            Method method = getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        } catch (Exception e) {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail(e.toString())));
        }
    }

    // 获取用户实体(需要有uid参数)
    private void getUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        KgcUser kgcUser = userService.getUserById(req, resp);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(kgcUser)));
    }

    /**
     * 获取用户签到的数据
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getSign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TbSignIn tbSignIn = userService.getUserSignById(req);
        if (EmptyUtils.isEmpty(tbSignIn)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("该用户今天没有签到")));
            return;
        }
        resp.getWriter().write(JSON.toJSONString(ResultData.success(tbSignIn)));
    }

    /**
     * 用户签到的方法
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void doSign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TbSignIn tbSignIn = userService.sign(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(tbSignIn)));
    }

    /**
     * 获取总共签到的次数
     * 该接口就算被恶意重复调用，也不会修改redis后台中的总签到数，后台已经判断用户今天是否已经签到过
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getTotalSign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long signCount = userService.getTotalSignCount();
        resp.getWriter().write(JSON.toJSONString(ResultData.success(signCount)));
    }
}
