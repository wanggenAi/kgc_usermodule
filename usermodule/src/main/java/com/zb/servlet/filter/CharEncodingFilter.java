package com.zb.servlet.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CharEncodingFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
        HttpServletResponse hresp = (HttpServletResponse)resp;
        HttpServletRequest hreq = (HttpServletRequest) req;
        // 设置允许多个域名请求
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
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
