package com.vesoft.nebula.exception;

import com.vesoft.nebula.enums.ErrorEnum;
import lombok.Getter;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * Description NebulaExecuteException
 * Date 2020/3/19 - 10:29
 */
public class NebulaExecuteException extends RuntimeException {

    @Getter
    private String code;

    @Getter
    private String msg;


    public NebulaExecuteException(int code, String msg) {
        super(msg);
        this.code = String.valueOf(code);
        this.msg = msg;
    }

    public NebulaExecuteException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public NebulaExecuteException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = String.valueOf(code);
        this.msg = msg;
    }

    public NebulaExecuteException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public NebulaExecuteException(ErrorEnum errorEnum) {
        super(errorEnum.getResponseMessage());
        this.code = errorEnum.getResponseCode();
        this.msg = errorEnum.getResponseMessage();
    }

    public NebulaExecuteException(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getResponseMessage(), cause);
        this.code = errorEnum.getResponseCode();
        this.msg = errorEnum.getResponseMessage();
    }


}
