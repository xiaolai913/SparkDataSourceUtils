package org.happy.utils.datasource.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Redis读写测试
 *
 * @author happy
 * @version 17/4/09 上午10:00
 */
public class RedisUtilsTest {

    @Test
    public void testWriteRedis() {

        Jedis jedis = RedisUtils.getResource();
        assert jedis != null;

        int dbIndex = 5;                            //Redis index num
        jedis.select(dbIndex);
        Pipeline jedisPl = jedis.pipelined();       //采用pipeline方式批量写入

        int successCount = 0;
        int jedisPlSyncNum = 10;                    //批量处理个数
        for (int i=0; i<100; i++) {
            try {
                String hostKey = "key" + i;
                jedisPl.set(hostKey, "value" + i);      //if success return 'OK'

                successCount++;
                if(successCount % jedisPlSyncNum == 0) {
                    jedisPl.sync();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        jedisPl.sync();
    }


    @Test
    public void testReadRedis() {

        Jedis jedis = RedisUtils.getResource();
        assert jedis != null;

        int dbIndex = 5;                            //Redis index num
        jedis.select(dbIndex);
        for(int i=0; i<10; i++) {
            String value = jedis.get("key" + i);
            System.out.println(value);
        }

    }
}