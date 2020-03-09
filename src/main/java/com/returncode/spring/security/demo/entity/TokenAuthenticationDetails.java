package com.returncode.spring.security.demo.entity;

import java.io.Serializable;

public class TokenAuthenticationDetails implements Serializable {

    private String remoteAddress;
    private String tokenId;

    public TokenAuthenticationDetails(String remoteAddress, String tokenId) {
        this.remoteAddress = remoteAddress;
        this.tokenId = tokenId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
