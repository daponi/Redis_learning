package com.atguigu.www;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * 要求：
 * 1、输入手机号，点击发送后随机生成6位数字码，2分钟有效
 * 2、输入验证码，点击验证，返回成功或失败
 * 3、每个手机号每天只能输入3次
 */
public class PhoneCode {

    public static void main(String[] args) {
        //模拟验证码发送
        verifyCode("13678765435");

        //模拟验证码校验
        getRedisCode("13678765435","4444");

    }

    //1.生成6位随机数
    public static String getCode(){
        Random random = new Random();
        //区间范围的计算方式是: （(最大值 - 最小值 + 1) + 最小值）：999999-100000=899999+1=900000
        return random.nextInt(900000)+100000+"";
    }


    //2 每个手机每天只能发送三次，验证码放到redis中，设置过期时间120
    public static void verifyCode(String phoneNum){
        //连接Redis
        Jedis jedis = new Jedis("192.168.108.131", 6379);
        jedis.auth("1230123");

        //手机发送次数key
        String countKey="VerifyCode" + phoneNum +":count";
        //验证码key
        String codeKey = "VerifyCode" + phoneNum + ":code";

        //每个手机每天只能发送3次
        String count = jedis.get(countKey);
        if (count == null) {
            //没有发送次数，就设置次数为1
            jedis.setex(countKey,60*60*24l,"1");
        }else if (Integer.parseInt(count)<=2){
            //发送次数+1
            jedis.incr(countKey);
        }else {
            //发送超过3次，不能再发送
            System.out.println("今天发送次数已超过三次，请明天再尝试！");
            jedis.close();
            return;
        }

        //将验证码放到redis里
        jedis.setex(codeKey,120l,getCode());
        jedis.close();

    }

    //3 验证码校验
    public static void getRedisCode(String phoneNum,String code) {
        //从redis获取验证码
        Jedis jedis = new Jedis("192.168.108.131",6379);
        jedis.auth("1230123");

        //验证码key
        String codeKey = "VerifyCode"+phoneNum+":code";
        String redisCode = jedis.get(codeKey);
        //判断
        if(redisCode.equals(code)) {
            System.out.println("验证成功！");
        }else {
            System.out.println("验证失败！");
        }
        jedis.close();
    }

}
