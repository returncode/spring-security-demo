package com.returncode.spring.security.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.returncode.spring.security.demo.entity.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录成功
 */
@Component("logoutSuccessHandler")
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        if (authentication != null) {
            response.getWriter().write(objectMapper.writeValueAsString(authentication.getPrincipal()));
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ErrorResult result = new ErrorResult(request, HttpStatus.UNAUTHORIZED, "未找到登录用户");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
        response.getWriter().flush();
    }
}
