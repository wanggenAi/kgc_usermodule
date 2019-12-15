package com.zb.entity;

import com.zb.util.general.Constant;

import java.util.List;
import java.util.Map;

public class PageInfo {
    private int startNum; // 查询开始的条数
    private int pageSize; // 一页显示的条数
    private int totalPage; // 总页数
    private int totalCount; // 总记录数
    private int currentPage; // 当前页
    private List<Map<String, Object>> list; // 结果集

    public PageInfo() {
        this.pageSize = Constant.PAGE_SIZE;
        this.currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getStartNum() {
        return (this.currentPage - 1) * this.pageSize;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }
}
