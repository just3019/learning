package org.demon.ioc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author demon
 * @version 1.0.0
 */
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Cloneable {
    //bean定义的存储
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    //单例bean存储
    private Map<String, Object> beanMap = new ConcurrentHashMap<>(256);

    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception {
        if (containsBeanDefinition(beanName)) {
            throw new Exception("【" + beanName + "】的bean定义已经存在");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public Boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String name) throws Exception {
        return doGetBean(name);
    }

    /**
     * 获取bean对象
     */
    protected Object doGetBean(String beanName) throws Exception {
        Object instance = beanMap.get(beanName);
        if (instance != null) {
            return instance;
        }
        BeanDefinition beanDefinition = getBeanDefinition(beanName);

        Class<?> type = beanDefinition.getBeanClass();

        /*1.new一个对象。（需要知道类名）
        2.工厂方法。（需要知道工厂类名和工厂方法名）*/
        if (type != null) {
            if (beanDefinition.getFactoryMethodName() != null) {
                instance = createInstanceByStaticFactoryMethod(beanDefinition);
            } else {
                instance = createInstanceByConstructor(beanDefinition);
            }
        } else {
            instance = createInstanceByFactoryMethod(beanDefinition);
        }
        if (beanDefinition.getScope().equals(BeanDefinition.SINGLETON)) {
            beanMap.put(beanName, instance);
        }
        return instance;
    }

    /**
     * 根据工厂方法创建对象
     */
    private Object createInstanceByFactoryMethod(BeanDefinition beanDefinition) throws Exception {
        Object factoryBean = doGetBean(beanDefinition.getFactoryBeanName());
        Method method = factoryBean.getClass().getMethod(beanDefinition.getFactoryMethodName(), null);
        return method.invoke(factoryBean, null);
    }

    /**
     * 根据静态工厂方法创建对象
     */
    private Object createInstanceByStaticFactoryMethod(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type = beanDefinition.getBeanClass();
        Method method = type.getMethod(beanDefinition.getFactoryMethodName(), null);
        return method.invoke(type, null);
    }

    /**
     * new方式创建
     */
    private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws IllegalAccessException, InstantiationException {
        return beanDefinition.getBeanClass().newInstance();
    }
}
