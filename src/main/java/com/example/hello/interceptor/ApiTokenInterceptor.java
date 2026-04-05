package com.example.hello.interceptor;

import com.example.hello.annotation.RequireApiToken;
import com.example.hello.config.LicenseApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * API Token 验证拦截器
 * 拦截带有 @RequireApiToken 注解的接口，验证请求头中的 Token
 */
@Component
public class ApiTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private LicenseApiConfig licenseApiConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理带有 @RequireApiToken 注解的方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequireApiToken annotation = handlerMethod.getMethodAnnotation(RequireApiToken.class);
            
            if (annotation != null) {
                // 需要验证 Token
                String token = request.getHeader("X-API-Token");
                
                if (token == null || token.trim().isEmpty()) {
                    sendErrorResponse(response, "缺少API Token，请在请求头中添加 X-API-Token");
                    return false;
                }
                
                if (!token.equals(licenseApiConfig.getToken())) {
                    sendErrorResponse(response, "API Token 无效，无权限访问此接口");
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

