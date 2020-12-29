package com.jin10.spider.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Airey
 * @date 2020/5/8 17:01
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Service
@Slf4j
public class CurlUtils {


    /**
     * 执行curl请求
     *
     * @param cmds
     * @return
     */
    public String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        } catch (IOException e) {
            log.error("执行curl命令错误！！！", e);
        }
        return null;
    }


}
