
package com.jin10.spider.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 */
@Data
public class PageUtils implements Serializable {

    private static final long serialVersionUID = 6134902616418130857L;
    /**
     * 总记录数
     */
    public int totalCount;
    /**
     * 每页记录数
     */
    public int pageSize;
    /**
     * 总页数
     */
    public int totalPage;
    /**
     * 当前页数
     */
    public int currPage;
    /**
     * 列表数据
     */
    public List<?> list;

    /**
     * 分页
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * com.baomidou.mybatisplus.core.metadata.IPage 分页
     */
    public PageUtils(IPage<?> page) {
        this.list = page.getRecords();
        this.totalCount = (int) page.getTotal();
        this.pageSize = (int) page.getSize();
        this.currPage = (int) page.getCurrent();
        this.totalPage = (int) page.getPages();
    }

    /**
     * org.springframework.data.domain.Page 分页
     *
     * @param page
     */
    public PageUtils(Page<?> page) {
        this.list = page.getContent();
        this.totalCount = (int) page.getTotalElements();
        this.pageSize = page.getSize();
        this.currPage = page.getNumber() + 1;
        this.totalPage = page.getTotalPages();
    }




}
