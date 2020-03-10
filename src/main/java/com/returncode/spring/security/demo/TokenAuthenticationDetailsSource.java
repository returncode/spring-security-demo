package com.returncode.spring.security.demo;

import com.returncode.spring.security.demo.entity.TokenAuthenticationDetails;
import com.returncode.spring.security.demo.utils.UuidUtil;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TokenAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, TokenAuthenticationDetails> {

    @Override
    public TokenAuthenticationDetails buildDetails(HttpServletRequest httpServletRequest) {
        String remoteAddress = httpServletRequest.getRemoteAddr();
        String tokenId = UuidUtil.getUuid();
        return new TokenAuthenticationDetails(remoteAddress, tokenId);
    }


}
