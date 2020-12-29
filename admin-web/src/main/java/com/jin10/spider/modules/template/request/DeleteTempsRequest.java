package com.jin10.spider.modules.template.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-18 11:48
 * ----------------------------------------------
 * 删除模版list
 */
@Data
public class DeleteTempsRequest {

    @NotNull
    private List<Long> ids;
}
