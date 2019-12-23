package com.zb.dao.imp;

import com.zb.entity.TaskForUser;
import com.zb.util.database.BaseDao;

import java.util.List;

public class TaskDaoImp extends BaseDao {

    /**
     * 查询用户的任务列表
     *
     * @param id 用户id
     * @return
     */
    public List<TaskForUser> getTaskList(long id) {
        String sql = "select l.*,u.taskstatus " +
                "from taskforuser u inner join tasklist l " +
                "on u.taskid = l.id and u.uid = ?";
        return selectMany(sql, TaskForUser.class, id);
    }

    /**
     * 修改用户该任务的状态
     *
     * @param id     用户id
     * @param taskId 任务的id
     * @param status 任务状态值
     * @return
     */
    public boolean updateTaskStatus(long id, short taskId, short status) {
        String sql = "update taskforuser set taskstatus = ? where uid = ? and taskid = ?";
        return executeUpdate(sql, status, id, taskId) > 0 ? true : false;
    }

    /**
     * 新建用户要给用户初始化任务列表
     * @param id 用户id
     * @return
     */
    public boolean initTaskForUser(long id) {
        String sql = "insert into taskforuser select ?,t.id,0  from tasklist t";
        return executeUpdate(sql, id) > 0 ? true : false;
    }

    public static void main(String[] args) {
        TaskDaoImp taskDaoImp = new TaskDaoImp();
        taskDaoImp.initTaskForUser(312312412L);
    }


}
