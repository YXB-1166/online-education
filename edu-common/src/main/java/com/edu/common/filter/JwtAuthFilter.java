package com.edu.common.filter;

import com.edu.common.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter implements Filter {

    private final String[] excludePaths;

    public JwtAuthFilter(String... excludePaths) {
        this.excludePaths = excludePaths;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        for (String e : excludePaths) {
            if (path.contains(e)) {
                chain.doFilter(request, response);
                return;
            }
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或token已过期\",\"data\":null}");
            return;
        }

        try {
            request.setAttribute("userId", JwtUtil.getUserId(auth.substring(7)));
            request.setAttribute("role", JwtUtil.getRole(auth.substring(7)));
            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"token无效\",\"data\":null}");
        }
    }
}
