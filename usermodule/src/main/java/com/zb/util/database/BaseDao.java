package com.zb.util.database;


import com.zb.entity.PageInfo;
import com.zb.util.general.Constant;
import com.zb.util.general.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * 封装JDBC的增删改查功能
 *
 * @param <T>
 * @author Administrator
 */
public class BaseDao<T> {

    /**
     * 封装增删改功能
     *
     * @param sql  需要执行的sql语句
     * @param args 不定参数，是对sql语句的占位符传入的参数
     * @return 返回操作所影响到行数
     */
    protected int executeUpdate(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pst = null;
        int rows = 0;
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rows = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(null, pst, conn);
        }
        return rows;
    }

    /**
     * 查询一条记录
     *
     * @param sql 需要执行的sql
     * @param cls 由此Class对象建模的类的类型
     */
    protected T selectOne(String sql, Class<T> cls, Object... args) {
        List<T> list = this.selectMany(sql, cls, args);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取UID
     *
     * @param sql
     * @param args
     * @return
     */
    protected long getUid(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        long uid = Constant.NOT_FOUND_UID;
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                uid = rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
        return uid;
    }

    protected boolean isExist(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
        return false;
    }

    /**
     * 查询所有记录
     */
    protected List<T> selectMany(String sql, Class<T> cls, Object... args) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<T>();
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rs = pst.executeQuery();
            // 从结果中获取数据库表的相关信息
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                T obj = cls.newInstance(); // 创建cls实例
                // 获取数据库表的字段数
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnar = metaData.getColumnLabel(i); // 获取字段名称，一定要和实体类的属性名对应好，实体类中的属性名应当全部小写
                    String name = "set" + StringUtil.toUpper(columnar);
                    Field field = cls.getDeclaredField(columnar);
                    Method method = cls.getMethod(name, field.getType());
                    // 先获取改行此列的值
                    Object realParam = rs.getObject(columnar);
                    // 给这个对象赋值
                    method.invoke(obj, realParam);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
        return list;
    }

    /**
     * 重载查询，可以使用表关联查询，返回一个listmap的集合
     *
     * @param sql
     * @param args
     * @return
     */
    protected List<Map<String, Object>> selectMany(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = null;
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rs = pst.executeQuery();
            // 调用转换方法，获取List<Map>集合
            list = convertList(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
        return list;
    }

    /**
     * 将查询结果集转换成list<Map>
     *
     * @param rs
     * @return
     */
    protected List<Map<String, Object>> convertList(ResultSet rs) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();// 新建map集合用来存储当前行的所有字段
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询总记录数
     */
    protected int selectCount(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
        return count;
    }

    protected void pagedQuery(PageInfo pageInfo, String sql, Object... args) {
        String resultSql = sql + " limit " + pageInfo.getStartNum() + "," + pageInfo.getPageSize();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = null;
        try {
            conn = JDBCUtil.getConn();
            pst = conn.prepareStatement(resultSql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }
            rs = pst.executeQuery();
            // 调用转换方法，获取List<Map>集合
            list = convertList(rs);
            String countSql = "select count(*) from (" + sql + ") a";
            int count = selectCount(countSql, args);
            pageInfo.setList(list);
            pageInfo.setTotalCount(count);
            pageInfo.setTotalPage((int) Math.ceil(count / pageInfo.getPageSize())); // 计算总页数
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
    }
}

