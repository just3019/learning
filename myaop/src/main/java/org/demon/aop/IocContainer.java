package org.demon.aop;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author demon
 * @version 1.0.0
 */
public class IocContainer {

    private Map<String, Class<?>> beanDefinitionMap = new HashMap<>();

    /**
     * 织入切面aop
     */
    @Getter
    @Setter
    private Aspect aspect;

    /**
     * 往容器中添加bean
     */
    public void addBeanDefinition(String beanName, Class<?> clazz) {
        beanDefinitionMap.put(beanName, clazz);
    }

    //获取bean
    public Object getBean(String beanName) throws Exception {
        Object object = createInstance(beanName);
        object = proxyEnhance(object);
        return object;
    }

    /**
     * 代理增强方法
     */
    private Object proxyEnhance(Object bean) {
        if (bean.getClass().getName().matches(aspect.getPointCut().getClassName())) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(),
                    new AopInvocationHandler(bean, aspect));
        }
        return bean;
    }

    /**
     * 创建实例
     */
    private Object createInstance(String beanName) throws Exception {
        return beanDefinitionMap.get(beanName).newInstance();
    }
}
