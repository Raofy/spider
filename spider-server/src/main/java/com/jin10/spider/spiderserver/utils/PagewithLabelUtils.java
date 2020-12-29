package com.jin10.spider.spiderserver.utils;

import com.jin10.spider.common.utils.PageUtils;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Airey
 * @date 2019/11/28 17:56
 * ----------------------------------------------
 * 多一个label字段的分页处理
 * ----------------------------------------------
 */
@Data
public class PagewithLabelUtils extends PageUtils implements Serializable {

    private Map<String, Object> label;

    public PagewithLabelUtils(List<?> list, int totalCount, int pageSize, int currPage) {
        super(list, totalCount, pageSize, currPage);
    }

    /**
     * org.springframework.data.domain.Page 分页
     *
     * @param page
     */
    public PagewithLabelUtils(Page<?> page, Map<String, Object> label) {
        super(page);
        this.label = label;
    }


}
