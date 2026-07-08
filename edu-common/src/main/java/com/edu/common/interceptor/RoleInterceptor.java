package com.edu.common.interceptor;

import com.edu.common.annotation.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }
        RequireRole ann = hm.getMethodAnnotation(RequireRole.class);
        if (ann == null) {
            return true;
        }
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || Arrays.stream(ann.value()).noneMatch(r -> r == role)) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403,\"message\":\"权限不足\",\"data\":null}");
            return false;
        }
        return true;
    }
}
