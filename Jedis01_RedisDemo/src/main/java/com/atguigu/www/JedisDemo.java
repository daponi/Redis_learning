package com.atguigu.www;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JedisDemo {
    public static void main(String[] args) {
        //连接Redis
        Jedis jedis = new Jedis("192.168.108.131",6379);
        //输入密码
        jedis.auth("1230123");
        //进行ping
        String ping = jedis.ping();
        System.out.println(ping);
        jedis.close();
    }

    //操作key string
    @Test
    public void demo1() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.108.131",6379);
        jedis.auth("1230123");
        //添加
        jedis.set("name","lucy");

        //获取
        String name = jedis.get("name");
        System.out.println(name);

        //设置多个key-value
        jedis.mset("k1","v1","k2","v2");
        List<String> mget = jedis.mget("k1", "k2");
        System.out.println(mget);

        //获取全部key
        Set<String> keys = jedis.keys("*");
        for(String key : keys) {
            System.out.println(key);
        }
        jedis.close();
    }



    //操作list
    @Test
    public void demo2() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.108.131",6379);
        jedis.auth("1230123");
        jedis.lpush("key1","lucy","mary","jack");
        List<String> values = jedis.lrange("key1", 0, -1);
        System.out.println(values);
        jedis.close();
    }

    //操作set
    @Test
    public void demo3() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.108.131",6379);
        jedis.auth("1230123");
        jedis.sadd("names","lucy");
        jedis.sadd("names","mary");

        Set<String> names = jedis.smembers("names");
        System.out.println(names);
        jedis.close();
    }

    //操作zset
    @Test
    public void demo5() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.108.131",6379);
        jedis.auth("1230123");

        jedis.zadd("china",100d,"shanghai");
        jedis.zadd("china",200d,"beijing");
        jedis.zadd("china",300d,"guangzhou");

        Set<String> china = jedis.zrange("china", 0, -1);
        Iterator<String> iterator = china.iterator();
        while (iterator.hasNext()) {
            String next =  iterator.next();
            System.out.println(next);
        }
        System.out.println(china);

        jedis.close();
    }

    //操作hash
    @Test
    public void demo4() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.108.131",6379);
        jedis.auth("1230123");

        jedis.hset("users","age","20");
        String hget = jedis.hget("users", "age");
        System.out.println(hget);
        jedis.close();
    }

}
