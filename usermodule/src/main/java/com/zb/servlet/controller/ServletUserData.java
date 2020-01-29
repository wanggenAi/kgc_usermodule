package com.zb.servlet.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zb.entity.District;
import com.zb.entity.TbSignIn;
import com.zb.entity.UserData;
import com.zb.entity.respentity.ResultData;
import com.zb.service.imp.UserDataServiceImp;
import com.zb.service.imp.UserServiceImp;
import com.zb.service.inter.UserDataService;
import com.zb.service.inter.UserService;
import com.zb.util.general.Constant;
import com.zb.util.general.EmptyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@WebServlet("*.calc")
@MultipartConfig
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getUserData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserData userData = userDataService.getUserData(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(userData)));
    }

    /**
     * 获取用户本身等级的最高分
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getMaxLevelScore(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int maxval = userDataService.getMaxLevelScore(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(maxval)));
    }

    /**
     * 获取用户相关的实体数据
     *
     * @param req
     * @param resp 传递 uid 参数
     * @throws ServletException
     * @throws IOException
     */
    private void getUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> map = userService.getUserById(req, resp);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(map)));
    }

    /**
     * 修改用户的基本信息
     * kgcUser
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userDataService.updateUser(req))
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改成功")));
    }

    /**
     * 根据 provId 获取城市集合
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getCityByProv(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<District> districts = userService.getCityByProv(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(districts)));
    }

    /**
     * 获取所有省份信息
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getAllProvince(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<District> districts = userService.getAllProvince();
        resp.getWriter().write(JSONObject.toJSONString(ResultData.success(districts)));
    }

    /**
     * 获取用户签到的数据
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getSign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TbSignIn tbSignIn = userService.getSign(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(tbSignIn, "签到成功")));
    }

    /**
     * 用户签到的方法
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void doSign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userService.sign(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "签到成功")));
            return;
        }
        resp.getWriter().write(JSON.toJSONString(ResultData.fail("签到失败")));
    }

    /**
     * 获取总共签到的次数
     * 该接口就算被恶意重复调用，也不会修改redis后台中的总签到数，后台已经判断用户今天是否已经签到过
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getTotalSign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long signCount = userService.getTotalSignCount();
        resp.getWriter().write(JSON.toJSONString(ResultData.success(signCount)));
    }

    /**
     * 获取用户的任务列表
     *
     * @param req
     * @param resp 传递参数： uid
     * @throws ServletException
     * @throws IOException
     */
    private void getTaskList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List list = userService.getTaskList(req);
        if (EmptyUtils.isNotEmpty(list)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(list)));
            return;
        }
        resp.getWriter().write(JSON.toJSONString(ResultData.fail("获取任务列表失败")));
    }

    /**
     * 修改用户做的任务的状态
     *
     * @param req
     * @param resp 传递参数: uid taskId taskStatus
     * @throws ServletException
     * @throws IOException
     */
    private void updateTaskStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (userService.updateTaskStatus(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null, "修改成功")));
            return;
        }
        resp.getWriter().write(JSON.toJSONString(ResultData.fail("修改失败")));
    }

    /**
     * 用户初始化任务列表
     *
     * @param req
     * @param resp 传递参数 uid
     * @throws ServletException
     * @throws IOException
     */
    private void initTaskList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        userService.initTaskForUser(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(null)));
    }

    /**
     * uid 用户id
     * headerImg 文件标签名称
     * realPath 项目的web路径用来存放图片的目录
     *
     * @throws ServletException
     * @throws IOException
     */
    private void uploadHeaderImg(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        Part part =req.getPart("headerImg");
        if (userService.uploadFile(req)) {
            resp.getWriter().write(JSON.toJSONString(ResultData.success(null)));
            return;
        }
        resp.getWriter().write(JSON.toJSONString(ResultData.fail("上传失败")));
    }

    /**
     * 获取用户的头像
     * 传入 uid
     *
     * @throws ServletException
     * @throws IOException
     */
    private void getUserHeadImg(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String headUrl = userService.getUserHeadImg(req);
        resp.getWriter().write(JSON.toJSONString(ResultData.success(headUrl)));
    }
}
