package com.example.config;

import com.example.common.JwtUtil;
import com.example.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器：从请求头提取 token，解析用户信息存入 UserContext
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.equals("/api/auth/login") || uri.equals("/api/v1/auth/login")) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        String token = header.substring(7);
        if (!jwtUtil.isValid(token)) {
            response.setStatus(401);
            return false;
        }

        UserContext ctx = new UserContext();
        ctx.setUserId(jwtUtil.getUserId(token));
        ctx.setUsername(jwtUtil.getUsername(token));
        ctx.setRole(jwtUtil.parseToken(token).get("role", String.class));
        UserContext.set(ctx);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
