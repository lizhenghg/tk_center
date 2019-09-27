package com.web.tk.common.tk_common.config;

import com.web.tk.common.tk_common.exception.CommonException;

import java.io.UnsupportedEncodingException;
/**
 * 简单的config适配器类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-18
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ConfigAdapter extends AbstractConfig {


    public ConfigAdapter(String filePath) {
        super(filePath);
    }

    @Override
    public long getLongSetting(String key) {
        try {
            return Long.getLong(setting.get(key));
        } catch (Exception ex) {
            throw new CommonException("value is not long", ex);
        }
    }

    @Override
    public byte[] getByteSetting(String key, String charset) {
        try {
            return setting.get(key).getBytes(charset);
        } catch (UnsupportedEncodingException ex) {
            throw new CommonException("UnsupportedEncodingException", ex);
        } catch (Exception ex) {
            throw new CommonException("Exception", ex);
        }
    }
}