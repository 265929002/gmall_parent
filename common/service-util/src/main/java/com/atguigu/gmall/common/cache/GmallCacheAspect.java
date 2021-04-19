package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

//组成部件
@Component
@Aspect//AOP注解
/**
 *
 @Pointcut：Pointcut是植入Advice的触发条件。每个Pointcut的定义包括2部分，一是表达式，二是方法签名。方法签名必须是 public及void型。
 可以将Pointcut中的方法看作是一个被Advice引用的助记符，因为表达式不直观，因此我们可以通过方法签名的方式为 此表达式命名。
 因此Pointcut中的方法只需要方法签名，而不需要在方法体内编写实际代码。
 @Around：环绕增强，相当于MethodInterceptor
 @AfterReturning：后置增强，相当于AfterReturningAdvice，方法正常退出时执行
 @Before：标识一个前置增强方法，相当于BeforeAdvice的功能，相似功能的还有
 @AfterThrowing：异常抛出增强，相当于ThrowsAdvice
 @After: final增强，不管是抛出异常或者正常退出都会执行
 ————————————————
 版权声明：本文为CSDN博主「狂丰」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 原文链接：https://blog.csdn.net/fz13768884254/article/details/83538709
 */
public class GmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @SneakyThrows//小辣椒的统一处理异常的注解
    //  切GmallCache注解
    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")//环绕通知
    public Object gmallCacheGetData(ProceedingJoinPoint joinPoint){
        Object object = null;
        /*
            1.  获取方法上的注解
            2.  获取到注解的前缀，并组成缓存的key
            3.  根据key 获取缓存中的数据
            4.  判断是否获取到了数据{分布式锁的业务逻辑}
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);//获取注解
        String prefix = gmallCache.prefix();//获取到了前缀
//        获取方法的参数
        Object[] args = joinPoint.getArgs();
//        将Object数组转换为list集合，然后拼接字符串
        String key = prefix + Arrays.asList(args);
        try {
//        从缓存获取方法
            object = gitCache(key, signature);
//        判断
            if (object == null) {//缓存没有数据
//            分布式锁的业务逻辑
//            先加锁
                RLock lock = redissonClient.getLock(key + ":Lock");
//            上锁            最多等待时间        过期时间                  时间单位
                boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
//            判断是否加锁成功
                if (res) {
                    try {
//              查询数据库
                        //执行被@GmallCache
                        object = joinPoint.proceed(joinPoint.getArgs());//获取方法的参数，传参
                        //进行判断，防止被缓存击穿
                        if (object == null) {
                            Object object1 = new Object();
//                将空对象添加到缓存
                            redisTemplate.opsForValue().set(key, JSON.toJSONString(object1), RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                            return object1;
                        }
                        //不为空，将查到的数据添加到缓存中
                        redisTemplate.opsForValue().set(key, JSON.toJSONString(object), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                        return object;
                    } finally {
                        //解锁
                        lock.unlock();
                    }
                } else {
                    //  没有获取到锁对象
                    Thread.sleep(300);
                }

            } else {
                return object;
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

//兜底   直接访问数据库，
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private Object gitCache(String key, MethodSignature signature) {
        //  返回String
        String sObject = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(sObject)){
            //  返回数据！ 获取到返回类型
            //  如果缓存 ： public BigDecimal getSkuPrice(Long skuId) 返回值 BigDecimal
            //  如果缓存：  public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) 返回SpuSaleAttr
            //  如果缓存：  public SkuInfo getSkuInfo(Long skuId) 返回SkuInfo
            Class returnType = signature.getReturnType();

            //  将字符串变为要返回的数据类型！
            return JSON.parseObject(sObject,returnType);
        }
        return null;

    }


}
