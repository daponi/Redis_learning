package com.atguigu;

import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SecKill_redisByScript {
	
	private static final  org.slf4j.Logger logger =LoggerFactory.getLogger(SecKill_redisByScript.class) ;

	public static void main(String[] args) {
		JedisPool jedispool =  com.atguigu.JedisPoolUtil.getJedisPoolInstance();
 
		Jedis jedis=jedispool.getResource();
		System.out.println(jedis.ping());
		
		Set<HostAndPort> set=new HashSet<HostAndPort>();

	//	doSecKill("201","sk:0101");
	}

	/**
	 * 只有在Redis 2.6以上的版本才可以使用。
	 * 使用LUA脚本解决库存遗留问题:
	 *
	 *--定义变量,KEYS[1]、KEYS[2]分别代表接受参数1、参数2
	 * local userid=KEYS[1];
	 * local prodid=KEYS[2];
	 * local qtkey="sk:"..prodid..":qt";
	 * local usersKey="sk:"..prodid.":usr';
	 * --通知redis调用sismember方法，并传入两个参数
	 * local userExists=redis.call("sismember",usersKey,userid);
	 * if tonumber(userExists)==1 then
	 * --当前用户已经秒杀过
	 *   return 2;
	 * end
	 * local num= redis.call("get" ,qtkey);
	 * if tonumber(num)<=0 then
	 * --秒杀结束
	 *   return 0;
	 * else
	 *   redis.call("decr",qtkey);
	 *   redis.call("sadd",usersKey,userid);
	 * end
	 * --秒杀成功
	 * return 1;
	 *
	 */
	
	static String secKillScript ="local userid=KEYS[1];\r\n" + 
			"local prodid=KEYS[2];\r\n" + 
			"local qtkey='sk:'..prodid..\":qt\";\r\n" + 
			"local usersKey='sk:'..prodid..\":usr\";\r\n" + 
			"local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" + 
			"if tonumber(userExists)==1 then \r\n" + 
			"   return 2;\r\n" + 
			"end\r\n" + 
			"local num= redis.call(\"get\" ,qtkey);\r\n" + 
			"if tonumber(num)<=0 then \r\n" + 
			"   return 0;\r\n" + 
			"else \r\n" + 
			"   redis.call(\"decr\",qtkey);\r\n" + 
			"   redis.call(\"sadd\",usersKey,userid);\r\n" + 
			"end\r\n" + 
			"return 1" ;
			 
	static String secKillScript2 = 
			"local userExists=redis.call(\"sismember\",\"{sk}:0101:usr\",userid);\r\n" +
			" return 1";

	public static boolean doSecKill(String uid,String prodid) throws IOException {

		JedisPool jedispool =  com.atguigu.JedisPoolUtil.getJedisPoolInstance();
		Jedis jedis=jedispool.getResource();

		 //String sha1=  .secKillScript;
		String sha1=  jedis.scriptLoad(secKillScript);//加载LUA脚本
		Object result= jedis.evalsha(sha1, 2, uid,prodid);//执行脚本

		  String reString=String.valueOf(result);
		if ("0".equals( reString )  ) {
			System.err.println("已抢空！！");
		}else if("1".equals( reString )  )  {
			System.out.println("抢购成功！！！！");
		}else if("2".equals( reString )  )  {
			System.err.println("该用户已抢过！！");
		}else{
			System.err.println("抢购异常！！");
		}
		jedis.close();
		return true;
	}
}
