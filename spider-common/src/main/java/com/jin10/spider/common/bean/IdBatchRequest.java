package com.jin10.spider.common.bean;

import lombok.Data;

import java.util.List;

/**
 * @author Airey
 * @date 2020/3/30 17:35
 * ----------------------------------------------
 * 批量模板id
 * ----------------------------------------------
 */
@Data
public class IdBatchRequest {


    /**
     * 模板id列表
     */
    private List<Long> idList;

}
