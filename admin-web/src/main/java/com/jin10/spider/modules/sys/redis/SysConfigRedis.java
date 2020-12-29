/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.modules.sys.redis;


import com.jin10.spider.common.utils.RedisKeys;
import com.jin10.spider.common.utils.RedisUtilsBAK;
import com.jin10.spider.modules.sys.entity.SysConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统配置Redis
 *
 *
 */
@Component
public class SysConfigRedis {
    @Autowired
    private RedisUtilsBAK redisUtils;

    public void saveOrUpdate(SysConfigEntity config) {
        if(config == null){
            return ;
        }
        String key = RedisKeys.getSysConfigKey(config.getParamKey());
        redisUtils.set(key, config);
    }

    public void delete(String configKey) {
        String key = RedisKeys.getSysConfigKey(configKey);
        redisUtils.delete(key);
    }

    public SysConfigEntity get(String configKey){
        String key = RedisKeys.getSysConfigKey(configKey);
        return redisUtils.get(key, SysConfigEntity.class);
    }
}
