package com.renren.test.exception;

public class ConfigException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConfigException(String msg, Exception cause) {
        super(msg, cause);
    }
}
