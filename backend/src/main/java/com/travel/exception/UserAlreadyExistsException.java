package com.travel.exception;

/**
 * 用户已存在异常
 * 当尝试创建已存在的用户时抛出此异常
 * 
 * @author Travel System
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException() {
        super();
    }
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}