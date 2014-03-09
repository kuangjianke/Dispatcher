package com.renren.test.exception;

public class TestException extends Exception {

    private static final long serialVersionUID = 1L;

    public TestException(String msg, Exception cause) {
        super(msg, cause);
    }
}
