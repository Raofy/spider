package com.jin10.spider.modules.template.mapper;

import com.jin10.spider.modules.template.entity.Template;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 爬虫模版 Mapper 接口
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */

@Mapper
public interface TemplateMapper extends BaseMapper<Template> {

    @Update("UPDATE template SET `status`=#{status} WHERE id=#{id}")
    int updateStatusById(Long id, int status);


    /**
     * 停止过期的模版
     *
     * @return
     */
    @Update("UPDATE template SET status = 2 WHERE `status` = 1 and expire_time < now()")
    int updateStatusExpireToStop();


    @Update("UPDATE template SET delete_user = #{user} , delete_time =now() where id = #{id} ")
    int updateDeleteNameById(@Param("id") Long id, @Param("user") String user);


}
