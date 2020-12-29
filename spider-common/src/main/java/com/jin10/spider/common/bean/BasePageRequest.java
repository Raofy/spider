package com.jin10.spider.common.bean;

public class BasePageRequest {


    public Long pageNum = 1L;
    /**
     * 每页显示记录数
     */
    public Long pageSize = 10L;
    /**
     * 排序字段
     */
    public String orderField;

    /**
     * 排序方式
     */
    public  String order ;


//    private Map<String, Object> search;
//
//    private List<String> sort;


//    public Map<String, Object> getSearch() {
//        return search;
//    }
//
//    public void setSearch(Map<String, Object> search) {
//        this.search = search;
//    }
//
//    public List<String> getSort() {
//        return sort;
//    }
//
//    public void setSort(List<String> sort) {
//        this.sort = sort;
//    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
