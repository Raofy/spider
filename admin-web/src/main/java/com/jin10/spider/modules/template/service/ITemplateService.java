package com.jin10.spider.modules.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.modules.statistics.dto.TaskQueueInfoDto;
import com.jin10.spider.modules.template.dto.TaskQueueInfo;
import com.jin10.spider.modules.template.dto.TestTempDto;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.request.RenewalRequest;
import com.jin10.spider.modules.template.request.TemplatePageRequest;

import java.util.List;

/**
 * <p>
 * 爬虫模版 服务类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
public interface ITemplateService extends IService<Template> {

    PageUtils queryPage(TemplatePageRequest params);

    List<Template> findByStatusRunning();

    /**
     * 保存。更新模版
     *
     * @param request
     * @return
     */
    Template saveTemp(Template request);

    /**
     * 更新备注
     *
     * @param id
     * @param remark
     * @return
     */
    boolean updateRemark(Long id, String remark);

    /**
     * 停止模板
     *
     * @param id
     * @return
     */
    boolean stop(Long id);

    /**
     * 启动模板
     *
     * @param id
     * @return
     */
    boolean start(Long id);


    /**
     * 批量启动模板
     *
     * @param idList
     * @return
     */
    boolean startBatch(List<Long> idList);

    /**
     * 模板续期
     *
     * @param renewalRequest
     * @return
     */
    boolean renewal(RenewalRequest renewalRequest);

    /**
     * 删除模板
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    boolean deletes(List<Long> ids);

    void initTemplate();

    /**
     * 通过ID查找模版
     *
     * @param id        模版ID
     * @param addDetail 是否带rule详情
     * @return
     */
    Template findById(Long id, boolean addDetail);

    /**
     * 测试接口
     *
     * @param showAll
     * @param tempId
     * @return
     */
    List<TaskQueueInfo> taskQueueInfos(boolean showAll, Long tempId);

    /**
     * 用于校验
     *
     * @param category
     * @param title
     * @param channel
     * @return
     */
    List<Template> findByCategoryAndTitleAndChannel(String category, String title, String channel);

    /**
     * web 获取任务队列信息
     *
     * @return
     */
    List<TaskQueueInfoDto> getTaskQueueInfos();

    /**
     * 测试模版
     *
     * @param temp
     * @return
     */
    TestTempDto testTemp(Template temp);

    /**
     * 停止过期的模版
     *
     * @return
     */
    int updateStatusExpireToStop();


    int updateDeleteNameById(Long id, String user);


}
