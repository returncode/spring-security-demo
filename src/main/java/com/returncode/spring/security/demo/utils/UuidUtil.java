package com.returncode.spring.security.demo.utils;

import java.util.UUID;

public class UuidUtil {

    public static String getUuid() {
        String uuid = UUID.randomUUID().toString().toUpperCase();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }
}
