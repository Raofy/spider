package com.jin10.spider.modules.template.dto;

import lombok.Data;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-16 10:26
 * ----------------------------------------------
 */
@Data
public class TestTempDto {

    private String message;
    private int code;
    private List<ResultEntity> result;

    public class ResultEntity {

        private String url;
        private String title;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


}
