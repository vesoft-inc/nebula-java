package com.vesoft.nebula.database.exception;

import com.vesoft.nebula.database.constant.ErrorEnum;
import lombok.Data;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description ConnectException is used for
 * @Date 2020/3/19 - 10:29
 */
@Data
public class ConnectException extends RuntimeException {

    private int code;

    private String msg;


    public ConnectException(int code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ConnectException(int code,String msg,Throwable cause){
        super(msg,cause);
        this.code = code;
        this.msg = msg;
    }

    public ConnectException(ErrorEnum errorEnum){
        super(errorEnum.getMsg());
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMsg();
    }

    public ConnectException(ErrorEnum errorEnum,Throwable cause){
        super(errorEnum.getMsg(),cause);
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMsg();
    }





}
