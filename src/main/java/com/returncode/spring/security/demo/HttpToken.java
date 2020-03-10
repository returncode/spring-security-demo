package com.returncode.spring.security.demo;

/**
 * Token Interface
 */
public interface HttpToken {

    Boolean hasToken(String token);

    void setToken(String token, Object context);

    Object getToken(String token);

    Boolean removeToken(String token);

}
