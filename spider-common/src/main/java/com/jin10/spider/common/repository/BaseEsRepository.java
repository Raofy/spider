package com.jin10.spider.common.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author hongda.fang
 * @date 2019-12-09 16:10
 * ----------------------------------------------
 * <p>
 * es 基础dao
 */
@NoRepositoryBean
public interface BaseEsRepository<T, ID extends Serializable> extends ElasticsearchRepository<T, ID>, EsRepositoryEnhance<T, ID> {


}
