package com.jin10.spider.modules.statistics.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.utils.DomainNameUtils;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.statistics.service.impl.TaskLogServiceImpl;
import com.jin10.spider.modules.task.service.impl.SpiderCountXxlJobServiceImpl;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.service.ITemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;


/**
 * @author Airey
 * @date 2020/2/13 13:55
 * ----------------------------------------------
 * 添加测试源
 * ----------------------------------------------
 */
@RestController
@RequestMapping("addSource")
@Slf4j
public class AddSourceController {


    @Autowired
    private ITemplateService templateService;

    @Autowired
    private TaskLogServiceImpl taskLogService;

    @Autowired
    private IDingTalkWarnService warnService;

    @Autowired
    private SpiderCountXxlJobServiceImpl spiderCountXxlJobService;


    @PostMapping("test")
    public BaseResponse test(@RequestBody Template template) {


        Template template1 = new Template();
        for (int i = 307; i <= 1000; i++) {
            Template addTemp = new Template();
            template.setTitle("loop-" + i);
            template.setCategory("公告");
            template.setChannel("无限重定向测试源" + i);
            template.setPageSite("http://47.110.225.57:7001/redirect/loop/" + i);
            BeanUtils.copyProperties(template, addTemp);
            template1 = templateService.saveTemp(addTemp);

        }

        return BaseResponse.ok(template1);
    }


    @GetMapping("domain")
    public BaseResponse domain() {
        List<Template> templateList = templateService.list();
        templateList.forEach(item -> {
            String pageSite = item.getPageSite();
            if (StringUtils.isNotBlank(pageSite)) {
                try {
                    Template template = new Template();
                    template.setId(item.getId());
                    template.setWeforeign(item.getWeforeign());
                    template.setSpecial(item.getSpecial());
                    template.setDomainName(DomainNameUtils.getDomainName(pageSite));
                    templateService.updateById(template);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }


        });


        return BaseResponse.ok();
    }


    @GetMapping("es")
    public BaseResponse es() {


        String msg = "{\"taskId\": \"8b97219c10a54b168840141d5912bab2\", \"startTime\": 1584610917024, \"endTime\": 1584610917514, \"source\": \"代理测试\", \"status\": 2, \"ip\": \"47.110.248.202\", \"tempId\": 8052, \"spiderId\": \"0\", \"snapshotInfo\": {\"url\": \"http://47.110.147.250:8889/test_proxy\", \"headers\": {\"User-Agent\": \"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Iron/0.2.152.0 Safari/12475112.525\", \"Expect\": \"\", \"Pragma\": \"\"}, \"isDetailPage\": false, \"responseCode\": 200, \"snapshotHash\": \"c20d775e0b7a8ae6b0bdee7678358266\"}, \"remark\": null, \"proxy\": \"10.218.61.91:6688\", \"proxyType\": \"http\", \"proxyExtra\": \"{}\"}\n";

        TaskLog taskLogRequest = JSONUtil.toBean(msg, TaskLog.class);

        System.out.println(taskLogRequest);

        TaskLog save1 = new TaskLog();
        try {
            save1 = taskLogService.save(taskLogRequest);
        } catch (Exception e) {
            log.error("存储es 错误 ！！", e);
        }

        return BaseResponse.ok(save1);

    }


    @GetMapping("ding")
    public BaseResponse ding() {

        String msg = "测试钉钉消息！";
        warnService.secretKeyMsg(msg);

        return BaseResponse.ok();
    }


    @GetMapping("maintainer")
    public BaseResponse maintainer() {

        Date curTime = new Date();

        spiderCountXxlJobService.warnMaintainer(DateUtil.offsetHour(curTime, -1), curTime);

        return BaseResponse.ok();
    }


    @PostMapping("taskLog")
    public BaseResponse taskLog() {


        return BaseResponse.ok();
    }


    public static void main(String[] args) {

        String msg="{\"taskId\": \"123\", \"endTime\": 1589872387477, \"spiderId\": 998, \"startTime\": 1589872382830, \"proxyType\": \"http\", \"status\": 2, \"proxy\": null, \"proxyExtra\": null, \"snapshotInfo\": [{\"innerStartTime\": 1589872385402, \"innerEndTime\": 1589872387464, \"detail\": {\"url\": \"http://192.168.171.128:8889/timeout/2\", \"headers\": {\"Pragma\": \"\", \"User-Agent\": \"Mozilla/5.0 (Windows; U; Windows NT 5.1; it) AppleWebKit/522.13.1 (KHTML, like Gecko) Version/3.0.2 Safari/522.13.1\", \"Expect\": \"\"}, \"responseCode\": 200, \"snapshotHash\": \"6552f4bbcdfbc41036cb02e3444352d7\", \"wheDetailPage\": false}}, {\"innerStartTime\": 1589872387464, \"innerEndTime\": 1589872387469, \"detail\": {\"url\": \"http://192.168.171.128:8889/\", \"headers\": {\"Pragma\": \"\", \"User-Agent\": \"Mozilla/5.0 (X11; U; Linux i686; pt-BR; rv:1.9.0.4) Gecko/2008111317 Ubuntu/8.04 (hardy) Firefox/3.0.4\", \"Expect\": \"\"}, \"responseCode\": 200, \"snapshotHash\": \"621dd30641fde7167634509062011e51\", \"wheDetailPage\": false}}], \"ip\": \"47.91.252.211\", \"source\": \"test\", \"remark\": null, \"taskHash\": \"2fe6c77ec2de638cb93810c512dcd15a\", \"tempId\": 1}";

        TaskLog taskLog = JSONUtil.toBean(msg, TaskLog.class);

        System.out.println("taskLog : " + taskLog);

    }

}
