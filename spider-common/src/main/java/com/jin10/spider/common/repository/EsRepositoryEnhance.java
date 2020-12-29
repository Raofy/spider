package com.jin10.spider.common.repository;

import java.io.Serializable;


/**
 * @author hongda.fang
 * * @date 2019-12-09 15:11
 * ----------------------------------------------
 * 接口 dao
 */
public interface EsRepositoryEnhance<T, ID extends Serializable> {


    Class<T> getEntityClass();




}
