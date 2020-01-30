package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.dao.imp.TaskDaoImp;
import com.zb.dao.imp.UserDataDaoImp;
import com.zb.entity.District;
import com.zb.entity.KgcUser;
import com.zb.entity.TaskForUser;
import com.zb.entity.TbSignIn;
import com.zb.service.inter.SmsService;
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
import java.util.*;
import java.util.stream.Stream;

public class UserServiceImp implements UserService {
    KgcUserDaoImp kud = new KgcUserDaoImp();
    TaskDaoImp taskDao = new TaskDaoImp();
    UserDataDaoImp userDataDao = new UserDataDaoImp();
    SmsService smsService = new SmsServiceImp();

    /**
     * 验证用户密码是否正确
     *
     * @param req
     * @return
     */
    public Map<String, Object> authUserLogin(HttpServletRequest req) {
        Map<String, Object> map = new HashMap<>();
        String name = req.getParameter("userName");
        String pwd = req.getParameter("password");
        pwd = MD5.getMd5(pwd, Constant.PWD_MD5_LENGTH16);
        long uid = kud.getUser(name, pwd);
        if (uid == Constant.NOT_FOUND_UID) { // 用户名或密码错误
            map.put("uid", uid);
            return map;
        }
        // 要生成token 并且将token/uid 存入到redis中，并设置过期时间
        String jsonKey = saveUid2Redis(uid);
        map.put("uid", uid);
        map.put("jwtToken", jsonKey);
        return map; // 返回给控制层
    }

    /**
     * 将键值保存到redis数据库中
     *
     * @param uid
     * @return
     */
    private String saveUid2Redis(long uid) {
        String jsonKey = JwtHelper.generateJWT(String.valueOf(uid), String.valueOf(System.currentTimeMillis()));
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
        } else {
            kgcUser.setDistrict(new District());
        }
        map.put("kgcUser", kgcUser);
        return map;
    }

    /**
     * 获取所有省份的集合
     *
     * @return
     */
    public List<District> getAllProvince() {
        return kud.getAllProvince();
    }

    /**
     * 根据省份的id返回对应的市信息
     *
     * @param req
     * @return
     */
    public List<District> getCityByProv(HttpServletRequest req) {
        Short provId = Short.parseShort(req.getParameter("provId"));
        return kud.getCityByProvince(provId);
    }

    /**
     * 用户签到的方法,只负责签到
     *
     * @param req
     * @return 用户对象
     */
    synchronized public boolean sign(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        // 查询用户的签到历史
        int year = LocalDate.now().getYear();
        TbSignIn ts = kud.getSignByIdAndYear(uid, year);
        // 判断今年用户是否有签到过
        if (EmptyUtils.isEmpty(ts)) {
            kud.initUserSign(uid, SignUtils.defaultSignHistory(), year);
            ts = kud.getSignByIdAndYear(uid, year);
        } else {
            /* 首先判断今天是否已经签到过，避免通过接口的方式增加redis的后台总数
             * 假如今天已经签到过了，那么就不做redis的后台自增长操作
             * */
            byte[] data = SignUtils.signHistoryToByte(ts.getSign_history());
            // 用户今天已经签到过
            if (SignUtils.isSign(data, LocalDate.now().getDayOfYear())) {
                // 获取签到日期集合
//            ts.setSignList(SignUtils.getSignHistoryByMonth(ts.getSign_history(), LocalDate.now().getMonthValue()));
                return false;
            }
        }
        // 修改签到历史，今天签到
        String signHistory = SignUtils.sign(ts.getSign_history(), LocalDate.now().getDayOfYear());
        // 获取用户的连续签到次数
        int continue_sign = SignUtils.getMaxContinuitySingDay(signHistory);
        // 获取当前的签到时间
        long curTime = System.currentTimeMillis(); // 当前的系统时间毫秒数
        if (kud.upSignAfterSuccess(continue_sign, curTime, uid, signHistory, year)) {
            setTotalSignCount();
            return true;
        }
        return false;
    }

    /**
     * 用户签到的方法,只负责签到
     *
     * @param req
     * @return 用户对象
     */
    public TbSignIn getSign(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
//        long uid = 1231231233214l;
        List<TbSignIn> signInList = kud.getSignById(uid);
        Collections.sort(signInList, Comparator.comparing(TbSignIn::getSign_year).reversed());
        TbSignIn ts = signInList.get(0);
        byte[] data = SignUtils.signHistoryToByte(ts.getSign_history());
        // 用户今天是否已经签到过
        ts.setSign(SignUtils.isSign(data, LocalDate.now().getDayOfYear()));
        // 获取签到历史日期字符串
        ts.setSignList(SignUtils.getSignHistoryAll(signInList));
        return ts;
    }

    /**
     * 获取今天签到的总人数
     *
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
     *
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
     *
     * @param req
     * @return
     */
    public String getUserHeadImg(HttpServletRequest req) {
        long uid = Long.parseLong(req.getParameter("uid"));
        return kud.getUserHeaderImg(uid);
    }

    /**
     * 当用户注册成功后，初始化用户的一些数据表
     *
     * @date 2019-12-24 10:31:16
     */
    private boolean initUserData(long uid) {
        // 初始化用户任务列表
        if (!taskDao.initTaskForUser(uid)) {
            return false;
        }
        // 初始化用户数据信息
        if (!userDataDao.insertUData(uid)) {
            return false;
        }
        // 初始化用户签到表
        int year = LocalDate.now().getYear();
        if (!kud.initUserSign(uid, SignUtils.defaultSignHistory(), year)) {
            return false;
        }
        return true;
    }

    /**
     * 将kgc用户添加到后台数据库中
     *
     * @param kgcUser
     * @return
     */
    private boolean addKgcUserToDb(KgcUser kgcUser) {
        // 初始化属性 id, username, password, usertype, email, phone, head_url, createtime, updatetime
        // 初始化用户类型1学生2老师
        kgcUser.setUsertype(1);
        // 给用户手机或邮箱字段赋值
        if (RegexValidateUtil.isPhone(kgcUser.getUsername())) {
            kgcUser.setPhone(kgcUser.getUsername());
        } else {
            kgcUser.setEmail(kgcUser.getUsername());
        }
        // 设置用户的初始化默认头像
        kgcUser.setHead_url(Constant.USER_DEFAULT_HEADER);
        kgcUser.setCreatetime(System.currentTimeMillis());
        kgcUser.setUpdatetime(System.currentTimeMillis());
        if (kud.addKgcUser(kgcUser)) {
            if (initUserData(kgcUser.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 用户注册
     *
     * @param req userName 用户名 password 用户密码 verifyCode 验证码
     * @return map集合，程序执行的结果
     * @date 2019-12-25 10:06:50
     */
    public boolean registerUser(HttpServletRequest req, Map<String, Object> map) {
        KgcUser kgcUser = new KgcUser();
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String verifyCode = req.getParameter("verifyCode");
        kgcUser.setUsername(userName);
        kgcUser.setPassword(password);
        // 验证注册步骤是否成功
        return rexUserAll(kgcUser, map, verifyCode);
    }

    /**
     * 向redis数据库查询用户对应的验证码是否输入的正确
     *
     * @param userName
     * @param verifyCode
     * @return
     */
    private boolean verifyCodeReg(String userName, String verifyCode) {
        String jv = JedisUtils.get(userName);
        return verifyCode.equals(jv);
    }

    /**
     * 根据前端用户名和验证码判断输入是否正确
     *
     * @param req params: userName verifyCode
     */
    public boolean checkVerfyCodeInput(HttpServletRequest req) {
        String userName = req.getParameter("userName");
        String vcode = req.getParameter("verifyCode");
        return verifyCodeReg(userName, vcode);
    }

    /**
     * 验证用户输入的内容准确性
     *
     * @param kgcUser
     * @param map
     * @return
     */
    private boolean rexUserInput(KgcUser kgcUser, Map<String, Object> map) {
        if (RegexValidateUtil.isPhone(kgcUser.getUsername())) {
            if (!RegexValidateUtil.checkCellphone(kgcUser.getUsername())) {
                map.put("username", "手机号不正确");
                return false;
            }
        } else {
            if (!RegexValidateUtil.checkEmail(kgcUser.getUsername())) {
                map.put("username", "邮箱格式不正确");
                return false;
            }
        }
        // 判断密码是否为空，不作长度校验
        if (EmptyUtils.isEmpty(kgcUser.getPassword())) {
            map.put("password", "密码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 验证用户注册时每一个流程的正确性，保证一个原子性操作
     * 有一个地方出错则直接返回控制层false
     *
     * @param kgcUser
     * @param map
     * @param verifyCode
     * @return
     */
    private boolean rexUserAll(KgcUser kgcUser, Map<String, Object> map, String verifyCode) {
        // 用户输入错误返回false
        if (!rexUserInput(kgcUser, map)) {
            return false;
        }
        // 校验验证码是否正确
        if (EmptyUtils.isEmpty(verifyCode) || (!verifyCodeReg(kgcUser.getUsername(), verifyCode))) {
            map.put("verifyCode", "验证码输入不正确或已失效");
            return false;
        }
        // 校验用户名是否已经存在
        if (kud.isExistUser(kgcUser.getUsername())) {
            map.put("userName", "用户名已存在");
            return false;
        }
        long uid = 0;
        try {
            // 生成用户的唯一uid编号
            uid = IdWorker.getFlowIdWorkerInstance().nextId();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("uid", "用户uid创建失败");
            return false;
        }
        kgcUser.setId(uid);
        // 使用Md5 16位方式加密密码
        String password = MD5.getMd5(kgcUser.getPassword(), Constant.PWD_MD5_LENGTH16);
        kgcUser.setPassword(password);
        if (addKgcUserToDb(kgcUser)) {
            map.put("uid", kgcUser.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean changePwd(HttpServletRequest req) {
        // 判断redis中用户名和验证码是否正确
        String userName = req.getParameter("userName");
        if (EmptyUtils.isEmpty(userName)) return false;
        String verifyCode = req.getParameter("verifyCode");
        if (EmptyUtils.isEmpty(verifyCode)) return false;
        String vcode = JedisUtils.get(userName);
        if (!verifyCode.equals(vcode)) return false;
        String pwd = req.getParameter("password");
        if (EmptyUtils.isEmpty(pwd)) return false;

        // 调用数据层修改密码的方法
        pwd = MD5.getMd5(pwd, Constant.PWD_MD5_LENGTH16);
        return kud.changePwd(userName, pwd);

    }

    /**
     * 给用户发送手机或是邮件验证码
     * userName 用户名
     *
     * @return
     */
    public String sendVerifyCodeByEmailOrCell(HttpServletRequest req) {
        String userName = req.getParameter("userName");
        // 用户名为空
        if (EmptyUtils.isEmpty(userName)) {
            return null;
        }
        // 校验该用户是否已经发送过了验证码且还没有到修改时间
        if (EmptyUtils.isNotEmpty(JedisUtils.get(userName))) {
            if (Constant.VERIFY_CODE_EXPIRE - JedisUtils.ttl(userName) < Constant.VERIFY_CODE_UPDATE) {
                return null;
            }
        }
        if (RegexValidateUtil.isPhone(userName)) {
            return smsService.sendPhoneCode(userName);
        }
        return smsService.sendEmailCode(userName);
    }

    public boolean isExistUser(HttpServletRequest req) {
        String userName = req.getParameter("userName");
        return kud.isExistUser(userName);
    }

    public static void main(String[] args) {
//        UserServiceImp usi = new UserServiceImp();
//        TbSignIn tbi = usi.getSign(null);
//        System.out.println(tbi.getSignList().toString());
        String signHistory =
                SignUtils.sign("AAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
                        28);
        System.out.println(signHistory);
    }

}


