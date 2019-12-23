package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.dao.imp.TaskDaoImp;
import com.zb.entity.District;
import com.zb.entity.KgcUser;
import com.zb.entity.TaskForUser;
import com.zb.entity.TbSignIn;
import com.zb.service.inter.UserService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;
import com.zb.util.general.EmptyUtils;
import com.zb.util.general.SignUtils;
import com.zb.util.general.StringUtil;
import com.zb.util.jwt.JwtHelper;
import com.zb.util.jwt.SecretConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImp implements UserService {
    KgcUserDaoImp kud = new KgcUserDaoImp();
    TaskDaoImp taskDaoImp = new TaskDaoImp();
    /**
     * 验证用户密码是否正确
     *
     * @param req
     * @param resp
     * @return
     */
    public long authUserLogin(HttpServletRequest req, HttpServletResponse resp) {
        String name = req.getParameter("userName");
        String pwd = req.getParameter("password");
        long uid = kud.getUser(name, pwd);
        if (uid == Constant.NOT_FOUND_UID) { // 用户名或密码错误
            return uid;
        }
        // 要生成token 并且将token/uid 存入到redis中，并设置过期时间
        String jsonKey = saveUid2Redis(uid);
        resp.addHeader("User-Token", jsonKey);
        return uid; // 返回给控制层
    }

    /**
     * 将键值保存到redis数据库中
     * @param uid
     * @return
     */
    private String saveUid2Redis(long uid) {
        String jsonKey = JwtHelper.generateJWT(String.valueOf(uid));
        JedisUtils.setex(jsonKey, SecretConstant.EXPIRESSECOND, String.valueOf(uid));
        return jsonKey;
    }

    /**
     * 返回客户端用户实体相关的数据
     */
    public Map<String, Object> getUserById(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, Object> map = new HashMap<>();
        long uid = Long.parseLong(req.getParameter("uid"));
        // 获得用户实体信息
        KgcUser kgcUser = kud.getUserById(uid);
        // 根据用户的地区信息返回省份和市区信息
        int address_city = kgcUser.getAddress_city();
        // 判断用户是否填过地区信息
        if (!(address_city == 0)) {
            // 查找用户的地区信息
            kgcUser.setDistrict(kud.getDistrictByCity(address_city));
        }
        // 获取所有的省份集合
        List<District> districts = kud.getAllProvince();
        map.put("kgcUser", kgcUser);
        map.put("districts", districts);
        return map;
    }

    /**
     * 根据省份的id返回对应的市信息
     * @param req
     * @return
     */
    public List<District> getCityByProv(HttpServletRequest req) {
        Short provId = Short.parseShort(req.getParameter("provId"));
        return kud.getCityByProvince(provId);
    }

    /**
     * 获取用户签到数据
     * @param req
     * @return
     */
    public TbSignIn getUserSignById(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        // 首先查询今天是否有签到
        TbSignIn ts = kud.getSignById(uid);
        byte[] data = SignUtils.signHistoryToByte(ts.getSign_history());
        int dayOfYear = LocalDate.now().getDayOfYear();
         ts.setSign(SignUtils.isSign(data, dayOfYear));
        // 获取当前月签到的日期列表
        ts.setSignList(SignUtils.getSignHistoryByMonth(ts.getSign_history(), LocalDate.now().getMonthValue()));
        return ts;
    }

    /**
     * 用户签到的方法,如果成则直接返回用户签到表
     *
     * @param req
     * @return 用户对象
     */
    synchronized public TbSignIn sign(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        // 查询用户的签到历史
        TbSignIn ts = kud.getSignById(uid);
        /* 首先判断今天是否已经签到过，避免通过接口的方式增加redis的后台总数
        * 假如今天已经签到过了，那么就不做redis的后台自增长操作
        * */
        byte[] data = SignUtils.signHistoryToByte(ts.getSign_history());
        // 用户今天已经签到过
        if (SignUtils.isSign(data, LocalDate.now().getDayOfYear())) {
            // 获取签到日期集合
            ts.setSignList(SignUtils.getSignHistoryByMonth(ts.getSign_history(), LocalDate.now().getMonthValue()));
            ts.setSign(true);
            return ts;
        }
        // 修改签到历史，今天签到
        String signHistory = SignUtils.sign(ts.getSign_history(), LocalDate.now().getDayOfYear());
        ts.setSign_history(signHistory);
        // 获取用户的连续签到次数
        int continue_sign = SignUtils.getMaxContinuitySingDay(ts.getSign_history());
        ts.setContinue_sign(continue_sign);
        // 获取当前的签到时间
        long curTime = System.currentTimeMillis(); // 当前的系统时间毫秒数
        // 获取签到日期集合
        ts.setSignList(SignUtils.getSignHistoryByMonth(signHistory, LocalDate.now().getMonthValue()));
        // 修改用户签到数据表
        if (kud.upSignAfterSuccess(continue_sign, curTime, uid, signHistory)) {
            ts.setSign(true);
            setTotalSignCount();
            return ts;
        }
        return null;
    }

    /**
     * 获取今天签到的总人数
     * @return long
     */
    public long getTotalSignCount() {
        String count = JedisUtils.get(StringUtil.getSignNameForRedis());
        count = count == null ? "0" : count;
        return Long.parseLong(count);
    }


    /**
     * 将redis中当天的总签到人数+1
     */
    private void setTotalSignCount() {
        String key = StringUtil.getSignNameForRedis();
        if (EmptyUtils.isEmpty(JedisUtils.get(key))) {
            JedisUtils.setex(key, Constant.SING_TOTAL_TIME, String.valueOf(1));
        } else {
            JedisUtils.incr(key);
        }
    }

    @Override
    public List<TaskForUser> getTaskList(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return taskDaoImp.getTaskList(uid);
    }

    @Override
    public boolean updateTaskStatus(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        short taskId = Short.parseShort(req.getParameter("taskId"));
        short taskStatus = Short.parseShort(req.getParameter("taskStatus"));
        return taskDaoImp.updateTaskStatus(uid, taskId, taskStatus);
    }

    @Override
    public boolean initTaskForUser(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return taskDaoImp.initTaskForUser(uid);
    }

    /**
     * 上传图片到服务器
     * @param req
     * @return
     */
    public boolean uploadFile(HttpServletRequest req) {
        return false;
    }
}
