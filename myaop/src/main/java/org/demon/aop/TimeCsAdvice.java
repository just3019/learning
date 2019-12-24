package org.demon.aop;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author demon
 * @version 1.0.0
 */
@Slf4j
public class TimeCsAdvice implements Advice {

    @Override
    public Object invoke(Object target, Method method, Object[] args) throws Exception {
        long start = System.currentTimeMillis();
        Object res = method.invoke(target, args);
        long use = System.currentTimeMillis() - start;
        log.info("类：{}，方法：{}，耗时：{}", target.getClass().getName(), method.getName(), use);
        return res;
    }
}
