package com.jin10.spider.spiderserver.form;

import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;

/**
 * @author Airey
 * @date 2019/12/12 11:35
 * ----------------------------------------------
 * 终端上线下线消息条件封装
 * ----------------------------------------------
 */
@Data
public class TerminalRecordForm extends BasePageRequest {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 机器码
     */
    private String machineCode;


    /**
     * 标题
     */
    private String title;


}
