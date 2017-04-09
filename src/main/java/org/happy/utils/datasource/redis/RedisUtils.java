package org.happy.utils.datasource.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis访问工具类
 *
 * @author happy
 * @version 17/4/09 上午10:00
 */
public final class RedisUtils {
    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    //访问密码
    private static String AUTH = "password";

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了MAX_TOTAL个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_TOTAL = 1024;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;


    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;

    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

//    private static JedisSentinelPool jedisPool = null;    //哨兵方式连接池

    /**
     * 初始化Redis连接池
     */
    public static void init() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setMaxWaitMillis(MAX_WAIT);

            // local redis
            jedisPool = new JedisPool(config, "127.0.0.1", 6379, TIMEOUT, AUTH);

//            // 哨兵方式连接
//            Set<String> sentinels = new HashSet<String>();
//            sentinels.add("sentinel1 ip:port");
//            sentinels.add("sentinel2 ip:port");
//            redisMasterName = "xxxx";
//
//            jedisPool = new JedisSentinelPool(redisMasterName, sentinels, config, TIMEOUT, AUTH, 0);

        } catch (Exception e) {
            throw new RuntimeException("init redis jedis pool failed");
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getResource() {
        try {
            if (jedisPool == null) {
                init();
            }
            return jedisPool.getResource();
        } catch (Exception e) {
            logger.error("get redis resource failed", e);
            throw new RuntimeException("get redis resource failed");
        }
    }


    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
