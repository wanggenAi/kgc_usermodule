package com.zb.dao.imp;

import com.zb.entity.UserData;
import com.zb.util.database.BaseDao;

import java.util.List;

public class UserDataDaoImp extends BaseDao {
    // 用户初始化一条数据
    public boolean insertUData(long uid) {
        String sql = "insert into userdata(id) values(?)";
        if (executeUpdate(sql, uid) > 0) {
            return true; // 插入成功
        }
        return false;
    }

    // 修改粉丝值字段
    public boolean updateFansCount(long id, short num) {
        String sql = "update userdata set fans_count = fans_count+? where id = ?";
        return changeCount(sql, id, num);
    }

    // 修改关注数
    public boolean updateFollowCount(long id, short num) {
        String sql = "update userdata set follow_count = follow_count+? where id = ?";
        return changeCount(sql, id, num);
    }

    // 修改点赞字段值
    public boolean updateSupportCount(long id, short num) {
        String sql = "update userdata set support_count = support_count+? where id = ?";
        return changeCount(sql, id, num);
    }

    // 修改帖子数量
    public boolean updateInvitationCount(long id, short num) {
        String sql = "update userdata set invitation_count = invitation_count+? where id = ?";
        return changeCount(sql, id, num);
    }

    // 修改kb金币值
    public boolean updateKbCount(long id, short num) {
        String sql = "update userdata set kb_count = kb_count+? where id = ?";
        return changeCount(sql, id, num);
    }

    // 修改金额的值
    public boolean updateMoney(long id, double money) {
        String sql = "update userdata set money = money + ? where id = ?";
        return changeCount(sql, id, money);
    }

    // 返回执行结果
    public boolean changeCount(String sql, long id, short num) {
        if (executeUpdate(sql, num, id) > 0) {
            return true;
        }
        return false;
    }

    public boolean changeCount(String sql, long id, double num) {
        if (executeUpdate(sql, num, id) > 0) {
            return true;
        }
        return false;
    }

    // 修改用户等级
    public boolean updateUserLevel(long id) {
        String sql = "update userdata set level_id = level_id + 1 where id = ?";
        return executeUpdate(sql, id) > 0 ? true : false;
    }

    // 修改用户的积分
    public boolean updateUserScore(long id, int num) {
        String sql = "update userdata set cur_score = cur_score+? where id = ?";
        return changeCount(sql, id, num);
    }

    /**
     * 获取用户数据
     *
     * @param id
     * @return
     */
    public UserData getUserData(long id) {
        String sql = "select * from userdata where id = ?";
        return selectOne(sql, UserData.class, id);
    }

    /**
     * 根据等级查找当前经验最大值
     *
     * @param id
     * @return
     */
    public int getMaxVal2Level(int id) {
        String sql = "select maxval from levelrule where id = ?";
        return selectCount(sql, id);
    }

    public static void main(String[] args) {
        UserDataDaoImp userDataDaoImp = new UserDataDaoImp();
        System.out.println(userDataDaoImp.getMaxVal2Level(1));
    }
}
