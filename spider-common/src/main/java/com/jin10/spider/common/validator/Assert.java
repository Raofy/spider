/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.common.validator;

import com.jin10.spider.common.exception.BaseException;
import org.apache.commons.lang.StringUtils;

/**
 * 数据校验
 *
 *
 */
public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new BaseException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new BaseException(message);
        }
    }
}
