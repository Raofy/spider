package com.jin10.spider.common.request;

/**
 * @author hongda.fang
 * @date 2019-11-08 17:55
 * ----------------------------------------------
 */
public class DingTalkSendRequest {


    /**
     * text : {"content":"我就是小雷, 是不一样的烟火"}
     * msgtype : text
     */
    private TextEntity text;
    private String msgtype;


    public DingTalkSendRequest(String msgtype, String content) {
        text = new TextEntity();
        text.setContent(content);
        this.msgtype = msgtype;
    }

    public void setText(TextEntity text) {
        this.text = text;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextEntity getText() {
        return text;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public static class TextEntity {
        /**
         * content : 我就是小雷, 是不一样的烟火
         */
        private String content;

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}
