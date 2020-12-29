package com.jin10.spider.modules.task.service;

import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.template.entity.Template;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-11-08 14:37
 * ----------------------------------------------
 *  任务管理
 */
public interface UrlTaskManger {

     /**
      * 初始化 模版
      * 加载到 内存
      * @return
      */
     List<Template> initTemplate();


     /**
      * 创建 需要推送的 task
      * @return
      */
     List<UrlTask> creatTask();


     /**
      * 根据模板id创建任务
      * @param template
      * @return
      */
     UrlTask creatTask(Template template);


     UrlTaskDto convertAddTemp(UrlTask urlTask);

     /**
      * 转换测试模版
      * @param template
      * @return
      */
     UrlTaskDto convertTestTemp(Template template);

     /**
      * 获取 模版来自当前 模版map
      * @param urlTask
      * @return
      */
     Template findTempByCurMap(UrlTask urlTask);





}
