package com.zb.servlet.login;

import com.zb.util.database.JDBCUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class ServletUserLogin extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = JDBCUtil.getConn();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String result = "";
        try {
            String sql = "select * from lj";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String mc = rs.getString(2);
                String dc = rs.getString(3);
                result += mc + ":" + dc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pst, conn);
        }
        resp.getWriter().write(result);
    }
}
