package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.dao.imp.TaskDaoImp;
import com.zb.dao.imp.UserDataDaoImp;
import com.zb.entity.District;
import com.zb.entity.KgcUser;
import com.zb.entity.TaskForUser;
import com.zb.entity.TbSignIn;
import com.zb.service.inter.UserService;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.ftpclient.FtpUtil;
import com.zb.util.general.*;
import com.zb.util.jwt.JwtHelper;
import com.zb.util.jwt.SecretConstant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImp implements UserService {
    KgcUserDaoImp kud = new KgcUserDaoImp();
    TaskDaoImp taskDao = new TaskDaoImp();
    UserDataDaoImp userDataDao = new UserDataDaoImp();
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
        return taskDao.getTaskList(uid);
    }

    @Override
    public boolean updateTaskStatus(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        short taskId = Short.parseShort(req.getParameter("taskId"));
        short taskStatus = Short.parseShort(req.getParameter("taskStatus"));
        return taskDao.updateTaskStatus(uid, taskId, taskStatus);
    }

    @Override
    public boolean initTaskForUser(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return taskDao.initTaskForUser(uid);
    }

    /**
     * 上传图片到服务器
     * @param req
     * @return
     */
    public boolean uploadFile(HttpServletRequest req) throws IOException, ServletException {
        long uid = Long.parseLong(req.getParameter("uid"));
        Part filePart = req.getPart("headerImg");
        try {
            String imgName = FtpUtil.uploadHeaderImg(filePart);
            // 修改用户头像url,这里只保存一个文件名称，前面的地址要动态指定服务器地址
            return kud.updateHeaderImg(imgName, uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取用户头像url，用来给ajax异步更新头像图片
     * @param req
     * @return
     */
    public String getUserHeadImg(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return kud.getUserHeaderImg(uid);
    }

    /**
     * 当用户注册成功后，初始化用户的一些数据表
     * @date 2019-12-24 10:31:16
     */
    private boolean initUserData(long uid) {
        boolean isOk = true;
        // 初始化用户任务列表
        isOk = taskDao.initTaskForUser(uid);
        // 初始化用户数据信息
        isOk = userDataDao.insertUData(uid);
        // 初始化用户的优惠券

        // 初始化用户签到表
        
        return isOk;
    }

    /**
     * 用户注册
     * @param req
     * @return
     */
    public Map<String, Object> registerUser(HttpServletRequest req, Map<String, Object> map) {
        KgcUser kgcUser = new KgcUser();
        boolean isOk = true;
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        kgcUser.setUsername(userName);
        kgcUser.setPassword(password);
        // 用户输入错误，直接返回map，控制端判断isOK
        if (!rexUserInput(kgcUser, map)) {
            return map;
        }
        // 验证码 有可能是手机的或者是邮箱的
        String verifyCode = req.getParameter("verifyCode");
        // 校验验证码是否正确
        if (!verifyCodeReg(userName, verifyCode)) {
            map.put("verifyCode", "验证码输入不正确");
            map.put("isOk",false);
            return map;
        }
        // 使用Md5 16位方式加密密码
        password = MD5.getMd5(password, Constant.PWD_MD5_LENGTH16);
        kgcUser.setPassword(password);
        long uid = 0;
        try {
            // 生成用户的唯一uid编号
            uid = IdWorker.getFlowIdWorkerInstance().nextId();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("isOk", false);
            map.put("uid", "用户uid创建失败");
            return map;
        }
        kgcUser.setId(uid);
        // 添加用户到后台数据库中,这里面还要做用户初始化的一些操作

        // 标记前端是否合法，后太是否执行成功
        map.put("isOk", isOk);
        return map;
    }

    /**
     * 向redis数据库查询用户对应的验证码是否输入的正确
     * @param userName
     * @param verifyCode
     * @return
     */
    private boolean verifyCodeReg(String userName,String verifyCode) {

        return true;
    }

    /**
     * 验证用户输入的内容准确性
     * @param kgcUser
     * @param map
     * @return
     */
    private boolean rexUserInput(KgcUser kgcUser,Map<String, Object> map) {
        if (RegexValidateUtil.isPhone(kgcUser.getUsername())) {
            if (!RegexValidateUtil.checkTelephone(kgcUser.getUsername())) {
                map.put("username", "手机号不正确");
                map.put("isOk", false);
                return false;
            }
        } else {
            if (!RegexValidateUtil.checkEmail(kgcUser.getUsername())) {
                map.put("username", "邮箱格式不正确");
                map.put("isOk", false);
                return false;
            }
        }
        // 判断密码是否为空，不作长度校验
        if (EmptyUtils.isEmpty(kgcUser.getPassword())) {
            map.put("password", "密码不能为空");
            map.put("isOk", false);
            return false;
        }
        return true;
    }
}
