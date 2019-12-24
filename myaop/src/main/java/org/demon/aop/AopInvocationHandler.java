package org.demon.aop;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author demon
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class AopInvocationHandler implements InvocationHandler {
    //目标对象
    private Object target;
    //aop对象
    private Aspect aspect;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //需要增强
        if (method.getName().matches(aspect.getPointCut().getMethorName())) {
            return aspect.getAdvice().invoke(target, method, args);
        }
        return method.invoke(target, args);
    }
}
