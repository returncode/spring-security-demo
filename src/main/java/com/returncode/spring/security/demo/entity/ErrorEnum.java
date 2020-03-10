package com.returncode.spring.security.demo.entity;

/**
 * 错误异常枚举
 */
public enum ErrorEnum {

    NULL_RESULT(555, "Null Result Error", "数据结果为空"),
    ACCOUNT_PWD_ERROR(556, "Account Password Error", "用户名或密码错误");

    private int state;
    private String error;
    private String message;

    ErrorEnum(int state, String error, String message) {
        this.state = state;
        this.error = error;
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
