package com.jin10.spider.modules.template.entity;

/**
 * @author Airey
 * @date 2020/1/8 14:29
 * ----------------------------------------------
 * 停止模板原因
 * ----------------------------------------------
 */
public interface StopTemplateReason {


    String TOO_MANY_ERROR_TIMES = "错误次数超过5次,模板自动停止！";

    String PERSON_OPTION = "{} 操作,手动停止模板";

    String EXPIRED_TIME = "模板过期自动停止！";

}
