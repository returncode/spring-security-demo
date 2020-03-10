package com.returncode.spring.security.demo.entity;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class ErrorResult {

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResult(HttpServletRequest request, HttpStatus httpStatus, String message) {
        this.timestamp = new Date();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = request.getRequestURI();
    }

    public ErrorResult(HttpServletRequest request, ErrorEnum errorEnum, String message) {
        this.timestamp = new Date();
        this.status = errorEnum.getState();
        this.error = errorEnum.getError();
        this.message = message;
        this.path = request.getRequestURI();
    }

    public ErrorResult(HttpServletRequest request, int status, String error, String message) {
        this.timestamp = new Date();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path =  request.getRequestURI();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
