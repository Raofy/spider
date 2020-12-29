package com.jin10.pushsocket.scheduled;

import com.jin10.pushsocket.constants.GlobalCounter;
import com.jin10.pushsocket.global.ChannelSupervise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @author Airey
 * @date 2020/6/9 11:26
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Component
@Slf4j
public class StatisScheduleTask {



    @Scheduled(cron = "0 0/1 * * * ? ")
    public void statisMsgRate(){

        long totalCount = GlobalCounter.socket_global_msg_total_count.get();
        long busCount = GlobalCounter.socket_global_msg_bus_count.get();
        long fullHttpCount = GlobalCounter.socket_global_msg_fullhttp_count.get();
        log.info("消息速率为 : total :  { " + totalCount + " } 个 / min , bus : { " + busCount + " } 个 / min , fullHttp : { " + fullHttpCount + " } 个 / min , 客户端数量为 { " + ChannelSupervise.GlobalGroup.size() + " }个");
        GlobalCounter.socket_global_msg_total_count.getAndSet(0L);
        GlobalCounter.socket_global_msg_bus_count.getAndSet(0L);
        GlobalCounter.socket_global_msg_fullhttp_count.getAndSet(0L);

    }



}
