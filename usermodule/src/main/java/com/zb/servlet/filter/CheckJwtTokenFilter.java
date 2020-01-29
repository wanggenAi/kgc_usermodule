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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter(filterName = "CheckJwtTokenFilter")
public class CheckJwtTokenFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        // 从请求头中获取jwt串，与前端约定好存放jwt的请求头属性名为User-Token
        HttpServletRequest hreq = (HttpServletRequest)req;
        HttpServletResponse hresp = (HttpServletResponse)resp;
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
//        // 设置允许多个域名请求
//        String[] allowDomains = {"http://localhost:3333", "http://127.0.0.1:3333"};
//        Set allowOrigins = new HashSet(Arrays.asList(allowDomains));
//
//        String originHeads = hreq.getHeader("Origin");
//        if(allowOrigins.contains(originHeads)){
//            //设置允许跨域的配置
//            // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
//            hresp.setHeader("Access-Control-Allow-Origin", originHeads);
//        }
        hresp.setHeader("Access-Control-Allow-Origin", "*");
        hresp.setHeader("Access-Control-Allow-Methods", "*");
        hresp.setHeader("Access-Control-Max-Age", "3600");
        hresp.addHeader("Access-Control-Allow-Headers", "*");
        hresp.setHeader("Access-Control-Allow-Credentials", "*");
        String jwt = hreq.getHeader("User-Token");
        // 判断jwt是否有效
        if (StringUtils.isNotBlank(jwt)) {
            // 校验这个token是否有效
            String retJson = JwtHelper.validateLogin(jwt);
            if (StringUtils.isNotBlank(retJson)) {
                JSONObject jsonObject = JSONObject.parseObject(retJson);
                // 校验该串是否存在于redis中
                long expire = JedisUtils.ttl(jwt);
                if (expire > 0) {
                    JedisUtils.expire(jwt, SecretConstant.EXPIRESSECOND);
                    chain.doFilter(req, resp);
                } else {
                    hresp.getWriter().write(JSON.toJSONString(ResultData.fail(Constant.TOKEN_ERROR, "token已失效，需要重新登录")));
                    return;
                }
            } else {
                hresp.getWriter().write(JSON.toJSONString(ResultData.fail(Constant.TOKEN_ERROR, "token验证无效")));
                return;
            }
        } else {
            hresp.getWriter().write(JSON.toJSONString(ResultData.fail(Constant.TOKEN_ERROR, "token验证无效！")));
            return;
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
