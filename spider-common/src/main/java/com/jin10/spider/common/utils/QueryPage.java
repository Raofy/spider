/**
 * https://www.jin10.com
 */

package com.jin10.spider.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin10.spider.common.bean.BasePageRequest;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * 查询 参数
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
public class QueryPage<T> {


    public IPage<T> getPage(BasePageRequest pageRequest) {

        Long pageNum = pageRequest.getPageNum();
        Long pageSize = pageRequest.getPageSize();
        //分页对象

        if (pageNum == null){
            pageNum = 0L;
        }
        if (pageSize == null){
            pageSize = 10L;
        }
        Page<T> page = new Page<>(pageNum, pageSize);

        //排序字段
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = pageRequest.getOrderField();
        String order = pageRequest.getOrder();

        //前端字段排序
        if (StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)) {
            if (Constants.ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            } else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }
        return page;
    }


}