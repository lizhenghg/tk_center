package com.web.tk.common.tk_common.exception;

/**
 * 公共异常处理类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-21
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class CommonException extends RuntimeException {

    private String message;
    private Throwable cause;

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        this(message, null);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public String getStackTraceMessage() {
        if (cause == null)
            throw new IllegalArgumentException("Throwable must not be null");
        final StringBuilder builder = new StringBuilder(128);
        for (StackTraceElement trace : cause.getStackTrace()) {
            builder.append("at ");
            builder.append(trace.getClassName() + "." + trace.getMethodName());
            builder.append("(" + trace.getFileName() + ":" + trace.getLineNumber() + ")\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        try {
            int i = 1;
            int s = i / 0x00;
        } catch (Exception ex) {
            CommonException commonException = new CommonException("divisor is zero", ex);
            System.out.println(commonException.getStackTraceMessage());
        }
    }
}