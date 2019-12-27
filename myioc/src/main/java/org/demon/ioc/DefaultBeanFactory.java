package org.demon.ioc;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
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
    private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws Exception {
        Object[] args = getConstructorArgumentValues(beanDefinition);
        if (args == null) {
            return beanDefinition.getBeanClass().newInstance();
        } else {
            return determineConstructor(beanDefinition, args).newInstance(args);
        }
    }

    /**
     * 获取bean定义中的构造参数
     */
    private Object[] getConstructorArgumentValues(BeanDefinition beanDefinition) throws Exception {
        return getRealValues(beanDefinition.getConstructorArgumentValues());
    }

    /**
     * 获取构造参数
     */
    private Object[] getRealValues(List<?> constructorArgumentValues) throws Exception {
        if (CollectionUtils.isEmpty(constructorArgumentValues)) {
            return null;
        }
        int size = constructorArgumentValues.size();
        Object[] values = new Object[constructorArgumentValues.size()];
        for (int i = 0; i < size; i++) {
            if (constructorArgumentValues.get(i) instanceof BeanReference) {
                values[i] = doGetBean(((BeanReference) constructorArgumentValues.get(i)).getBeanName());
            } else {
                values[i] = constructorArgumentValues.get(i);
            }
        }
        return values;
    }

    /**
     * 查询构造方法
     */
    private Constructor<?> determineConstructor(BeanDefinition beanDefinition, Object[] args) throws Exception {
        Constructor<?> constructor = beanDefinition.getConstructor();
        if (constructor != null) {
            return constructor;
        }
        //如果无参，返回无参构造函数
        if (args == null) {
            return beanDefinition.getBeanClass().getConstructor(null);
        }
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        constructor = beanDefinition.getBeanClass().getConstructor(paramTypes);
        //如果不是单例，则可以直接存Bean定义中
        if (constructor != null && beanDefinition.getScope().equals(BeanDefinition.PROTOTYPE)) {
            beanDefinition.setConstructor(constructor);
        }
        return constructor;
    }
}
