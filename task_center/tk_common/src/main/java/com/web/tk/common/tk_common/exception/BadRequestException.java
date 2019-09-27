package com.web.tk.common.tk_common.exception;

import com.web.tk.common.tk_common.codec.HTTP_CODE;

/**
 * 400业务异常处理类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-21
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(String errCode, String errMsg) {
        super(HTTP_CODE.BAD_REQUEST, errCode, errMsg);
    }
}