package com.vesoft.nebula.exception;

public class GraphOperateException extends RuntimeException {

    public GraphOperateException(String message, Object... args) {
        super(String.format(message, args));
    }

}
