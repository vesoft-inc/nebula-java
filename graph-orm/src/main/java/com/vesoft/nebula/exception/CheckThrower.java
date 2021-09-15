package com.vesoft.nebula.exception;

import com.vesoft.nebula.common.ResponseService;

/**
 * Description  CheckThrower is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/15 - 15:07
 * @version 1.0.0
 */
public class CheckThrower {

    public static void ifTrueThrow(boolean flag, String exceptionDesc) {
        if (flag) {
            throw new RuntimeException(exceptionDesc);
        }
    }

    public static void ifFalseThrow(boolean flag, String exceptionDesc) {
        if (!flag) {
            throw new RuntimeException(exceptionDesc);
        }
    }

    public static void ifTrueThrow(boolean flag, ResponseService responseService) {
        if (flag) {
            throw new NebulaException(responseService);
        }
    }

    public static void ifFalseThrow(boolean flag, ResponseService responseService) {
        if (!flag) {
            throw new NebulaException(responseService);
        }
    }


}
