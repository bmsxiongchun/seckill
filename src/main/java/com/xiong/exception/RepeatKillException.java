package com.xiong.exception;


/**
 * 重复秒杀异常，是一个运行期异常，不需要我们动手try/catch
 * mysql只支持运行期遗产的回滚操作
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
