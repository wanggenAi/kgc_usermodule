package com.zb.servlet.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zb.entity.respentity.ResultData;
import com.zb.service.imp.UserServiceImp;
import com.zb.service.inter.UserService;
import com.zb.util.general.Constant;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@WebServlet(urlPatterns = "*.do")
@MultipartConfig
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

    /**
     * 给用户发送验证码，手机或邮箱
     * @param req
     * @param resp
     * userName 用户名
     * @throws ServletException
     * @throws IOException
     */
    private void sendVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userService.sendVerifyCodeByEmailOrCell(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success("验证码发送成功")));
            return;
        }
        resp.getWriter().write(JSON.toJSONString(ResultData.fail("验证码发送失败")));
    }

    /**
     * 注册用户
     * @param req
     * @param resp
     * userName 用户名 password 用户密码 verifyCode 验证码
     * @throws ServletException
     * @throws IOException
     */
    private void registUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> map = new HashMap<>();
        if (userService.registerUser(req, map)) {
            resp.getWriter().write(JSONObject.toJSONString(ResultData.success(map.get("uid"))));
            return;
        }
        resp.getWriter().write(JSONObject.toJSONString(ResultData.fail(map)));
    }

    /**
     * 判断用户名是否存在
     * userName
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void isExistUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("");
        if (userService.isExistUser(req)) {
            resp.getWriter().write(JSONObject.toJSONString(ResultData.success(null, "用户名已存在")));
        } else {
            resp.getWriter().write(JSONObject.toJSONString(ResultData.fail("用户名不存在")));
        }
    }

    /**
     * 用户登录方法
     * 传递的参数: username password
     * @throws ServletException
     * @throws IOException
     */
    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long uid = userService.authUserLogin(req, resp);
        if (uid == Constant.NOT_FOUND_UID) { // 验证失败
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("用户名或密码错误")));
            return;
        }
        resp.getWriter().write(JSONObject.toJSONString(ResultData.success(uid, "登录成功")));
    }
}

