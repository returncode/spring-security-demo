package com.returncode.spring.security.demo;

import javax.servlet.http.HttpServletRequest;

/**
 * Token Interface
 */
public interface HttpToken {

    final static String TOKEN_HEADER_NAME = "AUTH-TOKEN";

    String readToken(HttpServletRequest request);

    Boolean hasToken(String token);

    void setToken(String token, Object context);

    Object getToken(String token);

    Boolean removeToken(String token);

}
