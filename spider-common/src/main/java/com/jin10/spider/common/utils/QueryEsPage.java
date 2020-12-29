/**
 * https://www.jin10.com
 */

package com.jin10.spider.common.utils;

import com.jin10.spider.common.bean.BasePageRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * <p>
 * 查询 参数
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
public class QueryEsPage {


    public static PageRequest getEsPage(BasePageRequest pageRequest) {


        // 页码 ES的分页是从第0页开始的
        if (pageRequest.getPageNum() == null || pageRequest.getPageNum() < 0) {
            pageRequest.setPageNum(0L);
        } else {
            pageRequest.setPageNum(pageRequest.getPageNum()-1);
        }
        // 页数
        if (pageRequest.getPageSize() == null || pageRequest.getPageSize() < 1) {
            pageRequest.setPageSize(10L);
        } else {

        }

        int pageNum = Math.toIntExact(pageRequest.getPageNum());
        int pageSize = Math.toIntExact(pageRequest.getPageSize());
        if (StringUtils.isEmpty(pageRequest.getOrderField())) {
            PageRequest of = PageRequest.of(Math.toIntExact(pageNum), pageSize);
            return of;
        } else {
            if (Constants.ASC.equalsIgnoreCase(pageRequest.getOrder())) {
                return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, pageRequest.getOrderField()));
            } else {
                return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, pageRequest.getOrderField()));
            }
        }
    }
}