package com.zb.servlet.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.zb.entity.DayScoreRule;
import com.zb.entity.LevelRule;
import com.zb.entity.respentity.ResultData;
import com.zb.service.imp.ScoreRuleServiceImp;
import com.zb.service.inter.ScoreRuleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@WebServlet("*.rule")
public class ServletRule extends HttpServlet {

    private ScoreRuleService scoreRuleService = new ScoreRuleServiceImp();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI(); // 获取uri路径
        String methodName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        // 利用反射调用请求的方法
        try {
            Method method = getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        } catch (Exception e) {
            resp.getWriter().write(JSON.toJSONString(ResultData.fail(e.toString())));
        }
    }

    /**
     * 获取每日用户得分规则数据
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getDayScoreRule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<DayScoreRule> list = scoreRuleService.getDayScoreRule();
        resp.getWriter().write(JSON.toJSONString(ResultData.success(list)));
    }

    /**
     * 获取用户等级规则数据
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void getLevelRule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<LevelRule> list = scoreRuleService.getLevelRule();
        resp.getWriter().write(JSON.toJSONString(ResultData.success(list)));
    }
}
