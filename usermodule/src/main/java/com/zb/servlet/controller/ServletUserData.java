package com.zb.servlet.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zb.entity.UserData;
import com.zb.entity.respentity.ResultData;
import com.zb.service.imp.UserDataServiceImp;
import com.zb.service.imp.UserServiceImp;
import com.zb.service.inter.UserDataService;
import com.zb.service.inter.UserService;
import com.zb.util.general.Constant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("*.calc")
public class ServletUserData extends HttpServlet {

    private UserDataService userDataService = new UserDataServiceImp();
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
     * 修改用户关注人数
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void chgFollowCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.chgFollowC(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改关注数成功")));
        } else {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
        }
    }

    /**
     * 修改用户粉丝数量
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void chgFansCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.chgFansC(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改粉丝数成功")));
        } else {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
        }
    }

    /**
     * 修改用户点赞数量
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void chgSupportCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.chgSupportC(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改点赞数成功")));
        } else {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
        }
    }

    /**
     * 修改用户帖子数量
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void chgInvitationCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.chgInvitationC(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改帖子数成功")));
        } else {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
        }
    }

    /**
     * 修改用户KB数量
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void chgKbCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.chgKbC(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改KB数量成功")));
        } else {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
        }
    }

    /**
     * 用户修改余额
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void chgMoneyCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.chgMoneyC(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改用户余额成功")));
        } else {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
        }
    }

    /**
     * 用户修改经验值
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void userAddScore(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        userDataService.userAddScore(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改经验值成功")));
    }

    /**
     * 返回用户基础数据
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getUserData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        UserData userData = userDataService.getUserData(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(userData)));
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
            resp.getWriter().write(JSON.toJSONString(ResultData.fail("用户或密码错误")));
            return;
        }
        resp.getWriter().write(JSONObject.toJSONString(ResultData.success(uid, "登录成功")));
    }
}
