package com.jin10.spider.common.bean;

import lombok.Data;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author hongda.fang
 * @date 2019-11-12 16:08
 * ----------------------------------------------
 */
@Data
public class UrlTaskDto {
    /**
     * 爬虫地址
     */
    private String url;


    /**
     * 任务唯一ID
     */
    private String taskUuid;


    /**
     * 是否国外网站
     */
    private boolean weForeign;


    /**
     * 创建时间
     */
    private Date createTime;


    private TemplateDto temp;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UrlTaskDto)) {
            return false;
        }
        UrlTaskDto that = (UrlTaskDto) o;
        return getUrl().equals(that.getUrl()) &&
                getTemp().equals(that.getTemp());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getTemp());
    }

    public static void main(String[] args) {

        TemplateDto t1 = new TemplateDto();
        t1.setTempId(809L);


        UrlTaskDto u1 = new UrlTaskDto();
        u1.setTemp(t1);

        u1.setUrl("123");


        UrlTaskDto u2 = new UrlTaskDto();
        u2.setUrl("123");
        u2.setTemp(t1);

        LinkedBlockingQueue<UrlTaskDto> urlTaskDtos = new LinkedBlockingQueue<>();
        urlTaskDtos.offer(u1);

        if (u1.equals(u2)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }


        System.out.println(urlTaskDtos.contains(u2));

    }
}
