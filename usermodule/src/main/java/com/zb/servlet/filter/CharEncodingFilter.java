package com.zb.servlet.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CharEncodingFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
        HttpServletResponse hresp = (HttpServletResponse)resp;
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
