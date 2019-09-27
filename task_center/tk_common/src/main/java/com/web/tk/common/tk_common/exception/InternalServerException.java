package com.web.tk.common.tk_common.exception;
import com.web.tk.common.tk_common.codec.HTTP_CODE;

/**
 * 通用的系统异常错误响应
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class InternalServerException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InternalServerException(String errCode, String errMsg) {
        super(HTTP_CODE.INTERNAL_SERVER_ERROR, errCode, errMsg);
    }
}
