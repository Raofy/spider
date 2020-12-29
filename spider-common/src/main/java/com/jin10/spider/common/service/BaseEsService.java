package com.jin10.spider.common.service;


import cn.hutool.core.bean.BeanUtil;
import com.jin10.spider.common.repository.BaseEsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


/**
 * @author hongda.fang
 * * @date 2019-12-09 15:11
 * ----------------------------------------------
 * es 基础 Service
 */
public abstract class BaseEsService<T, ID extends Serializable> {

    public T save(T source, ID targetId) {

        T entity = find(targetId);

        if (entity == null) {
            entity = source;
        }
        BeanUtil.copyProperties(source, entity);
        return getDao().save(entity);

    }

    public T save(T entity) {
        return getDao().save(entity);

    }

    public List<T> save(Iterable<T> entites) {
        BaseEsRepository rep = getDao();
        Iterable<T> iterable = rep.saveAll(entites);

        if (iterable != null){
            List<T> copy = new ArrayList<>();
            Iterator<T> iterator = iterable.iterator();
            while (iterator.hasNext()){
                T next = iterator.next();
                copy.add(next);
            }
            return copy;
        }
        return null;
    }


    public long count() {
        return getDao().count();

    }

    public T find(ID id) {
        Optional<T> byId = getDao().findById(id);
        if (byId != null && byId.isPresent()) {
            return byId.get();
        }
        return null;

    }

    public Iterable<T> findAll() {
        return getDao().findAll();

    }

    public void delete(ID id) {
        getDao().deleteById(id);

    }

    public Page<T> search(Pageable pageable) {
        return getDao().findAll(pageable);

    }

    public Page<T> search(List<Criteria> criteriaList, Pageable pageable) {
//        return getDao().pageByProps(pageable, criteriaList);

        return null;
    }

    public List<T> search(List<Criteria> criteriaList, Sort sort) {
//        return getDao().findByProps(criteriaList, sort);

        return null;
    }


    protected abstract BaseEsRepository<T, ID> getDao();

}
