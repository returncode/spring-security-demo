package com.returncode.spring.security.demo;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpTokenInMemoryProvider implements HttpToken {

    private final Map<String, Object> tokenCache = new HashMap();

    @Override
    public Boolean hasToken(String token) {
        return tokenCache.containsKey(token);
    }

    @Override
    public void setToken(String token, Object context) {
        tokenCache.put(token, context);
    }

    @Override
    public Object getToken(String token) {
        return tokenCache.get(token);
    }

    @Override
    public Boolean removeToken(String token) {
        Object value = tokenCache.get(token);
        return tokenCache.remove(token, value);
    }
}
