package com.zb.entity;

import com.zb.util.general.StringUtil;

public class TaskForUser {
    private int id; // 任务的Id
    private String tasktype;
    private String taskinfo;

    public String getTaskinfo() {
        return taskinfo;
    }

    public void setTaskinfo(String taskinfo) {
        this.taskinfo = taskinfo;
    }

    private String taskdesc;
    private int taskmoney;
    private String taskurl;
    private String taskicon;
    private int taskstatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getTaskdesc() {
        return taskdesc;
    }

    public void setTaskdesc(String taskdesc) {
        this.taskdesc = taskdesc;
    }

    public int getTaskmoney() {
        return taskmoney;
    }

    public void setTaskmoney(int taskmoney) {
        this.taskmoney = taskmoney;
    }

    public String getTaskurl() {
        return taskurl;
    }

    public void setTaskurl(String taskurl) {
        this.taskurl = taskurl;
    }

    public String getTaskicon() {
        return taskicon;
    }

    public void setTaskicon(String taskicon) {
        this.taskicon = taskicon;
    }

    public int getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(int taskstatus) {
        this.taskstatus = taskstatus;
    }
}
