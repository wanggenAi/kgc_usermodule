package com.zb.util.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zb.entity.PageInfo;
import com.zb.util.general.DateConvert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JDBCTest {
    public static void main(String[] args) {
        Connection conn = JDBCUtil.getConn();
        ResultSet rs = null;
        PreparedStatement pst = null;
        BaseDao<Object> baseDao = new BaseDao<>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrentPage(2);
        String sql = "select * from levelrule order by id desc";
        baseDao.pagedQuery(pageInfo,sql);
        String jsonStr = JSONObject.toJSONString(pageInfo);
        JSONObject jb = JSONObject.parseObject(jsonStr);
        List<Map<String,Object>> list = jb.getObject("list", List.class);
        System.out.println(list.toString());

    }
}
