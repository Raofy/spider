/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.common.validator;


import com.jin10.spider.common.exception.BaseException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * hibernate-validator校验工具类
 *
 * 参考文档：http://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/
 *
 *
 */
public class ValidatorUtils {
    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     * @param object        待校验对象
     * @param groups        待校验的组
     * @throws BaseException  校验不通过，则报RRException异常
     */
    public static void validateEntity(Object object, Class<?>... groups)
            throws BaseException {
        if (object == null){
            return;
        }
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
//        if (!constraintViolations.isEmpty()) {
//        	ConstraintViolation<Object> constraint = (ConstraintViolation<Object>)constraintViolations.iterator().next();
//            throw new BaseException(constraint.getMessage());
//        }

        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for(ConstraintViolation<Object> constraint:  constraintViolations){
                constraint.getMessageTemplate();
                msg.append(constraint.getPropertyPath().toString());
                msg.append(":");
                msg.append(constraint.getMessage());
            }
            throw new BaseException(msg.toString());
        }
    }
}
