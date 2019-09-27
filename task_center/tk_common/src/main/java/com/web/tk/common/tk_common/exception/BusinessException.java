package com.web.tk.common.tk_common.exception;

/**
 * 业务异常处理类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-21
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class BusinessException extends Exception {

    private static final long serialVersionUID = -4632539867780934306L;

    private String errCode;
    private int httpCode;
    public BusinessException (int httpCode, String errCode, String errMsg) {
        super(errMsg);
        setErrCode(errCode);
        setHttpCode(httpCode);
    }
    public String getErrCode() {
        return errCode;
    }
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    public int getHttpCode() {
        return httpCode;
    }
    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}