package com.zb.servlet.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zb.entity.respentity.ResultData;
import com.zb.util.database.redis.JedisUtils;
import com.zb.util.general.Constant;
import com.zb.util.jwt.JwtHelper;
import com.zb.util.jwt.SecretConstant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CheckJwtTokenFilter")
public class CheckJwtTokenFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        // 从请求头中获取jwt串，与前端约定好存放jwt的请求头属性名为User-Token
        HttpServletRequest hreq = (HttpServletRequest)req;
        HttpServletResponse hresp = (HttpServletResponse)resp;
        String jwt = hreq.getHeader("User-Token");
        // 判断jwt是否有效
        if (StringUtils.isNotBlank(jwt)) {
            // 校验这个token是否有效
            String retJson = JwtHelper.validateLogin(jwt);
            if (StringUtils.isNotBlank(retJson)) {
                JSONObject jsonObject = JSONObject.parseObject(retJson);
                // 校验客户端信息
                String userAgent = hreq.getHeader("User-Agent");
                if (userAgent.equals(jsonObject.getString("userAgent"))) {
                    // 校验该串是否存在于redis中
                    long expire = JedisUtils.ttl(jwt);
                    if (expire > 0) {
                        JedisUtils.expire(jwt, SecretConstant.EXPIRESSECOND);
                    } else {
                        hresp.getWriter().write(JSON.toJSONString(ResultData.fail(Constant.TOKEN_ERROR, "客户端验证异常")));
                        return;
                    }
                } else {
                    hresp.getWriter().write(JSON.toJSONString(ResultData.fail(Constant.TOKEN_ERROR, "客户端验证异常")));
                    return;
                }
            } else {
                hresp.getWriter().write(JSON.toJSONString(ResultData.fail(Constant.TOKEN_ERROR, "token验证无效！")));
                return;
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
