package com.zb.service.inter;

import com.zb.entity.District;
import com.zb.entity.KgcUser;
import com.zb.entity.TaskForUser;
import com.zb.entity.TbSignIn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    long authUserLogin(HttpServletRequest req, HttpServletResponse resp);

    Map<String, Object> getUserById(HttpServletRequest req, HttpServletResponse resp);
    public long getTotalSignCount();
    public TbSignIn getUserSignById(HttpServletRequest req);
    public TbSignIn sign(HttpServletRequest req);

    /**
     * 根据省id获取城市集合
     * @param req
     * @return
     */
    public List<District> getCityByProv(HttpServletRequest req);

    /**
     * 查询用户任务列表
     * @param req
     * @return
     */
    public List<TaskForUser> getTaskList(HttpServletRequest req);

    /**
     * 当用户完成一个任务后，修改任务的状态
     * @param req
     * @return
     */
    public boolean updateTaskStatus(HttpServletRequest req);

    /**
     * 判断用户名是否存在
     * @param req
     * @return
     */
    public boolean isExistUser(HttpServletRequest req);

    /**
     * 当用户注册后，初始化他的新手任务列表
     * @param req
     * @return
     */
    public boolean initTaskForUser(HttpServletRequest req);

    public String getUserHeadImg(HttpServletRequest req);

    public boolean uploadFile(HttpServletRequest req) throws IOException, ServletException;

    public boolean registerUser(HttpServletRequest req, Map<String, Object> map);

    public boolean sendVerifyCodeByEmailOrCell(HttpServletRequest req);

}
