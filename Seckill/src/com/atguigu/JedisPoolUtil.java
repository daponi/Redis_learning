package com.atguigu;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
	//volatile作用- 保证变量的内存可见性 - 禁止指令重排序
	private static volatile JedisPool jedisPool = null;

	private JedisPoolUtil() {
	}

	//连接池，解决请求超时问题
	public static JedisPool getJedisPoolInstance() {
		//这是单例模式懒加载的方式 并且双重校验加锁 还有禁止指令重排
		if (null == jedisPool) {
			//锁
			synchronized (JedisPoolUtil.class) {
				if (null == jedisPool) {
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(200);//最大连接数
					poolConfig.setMaxIdle(32); //最大空闲数
					poolConfig.setMaxWaitMillis(100*1000);//最大等待数
					poolConfig.setBlockWhenExhausted(true);//池耗尽时是否阻塞
					poolConfig.setTestOnBorrow(true);  // 连接前先经行测试ping  PONG
				 
					jedisPool = new JedisPool(poolConfig, "192.168.108.131",6379, 60000,"1230123" );
				}
			}
		}
		return jedisPool;
	}


	public static void release(JedisPool jedisPool, Jedis jedis) {
		if (null != jedis) {
			jedisPool.returnResource(jedis);//现在用jedis.close()代替被淘汰的jedisPool.returnResource(jedis);

		}
	}

}
