package com.returncode.spring.security.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.returncode.spring.security.demo.entity.ErrorEnum;
import com.returncode.spring.security.demo.entity.ErrorResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录验证失败
 */
@Component("loginFailureHandler")
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Log log = LogFactory.getLog(LoginFailureHandler.class);
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.debug(request.getServletPath() + " - " + exception.getMessage());
        response.setStatus(ErrorEnum.ACCOUNT_PWD_ERROR.getState());
        response.setContentType("application/json;charset=UTF-8");
        ErrorResult result = new ErrorResult(request, ErrorEnum.ACCOUNT_PWD_ERROR, exception.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}
