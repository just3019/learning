package org.demon.ioc;

/**
 * @author demon
 * @version 1.0.0
 */
public interface BeanDefinition {
    //单例标识
    String SINGLETON = "singleton";
    //原型标识
    String PROTOTYPE = "prototype";

    /**
     * 获取bean是单例还是prototype
     */
    String getScope();

    /**
     * 获取类名，可以通过Class直接new一个对象
     */
    Class<?> getBeanClass();

    /**
     * 获取工厂名
     */
    String getFactoryBeanName();

    /**
     * 获取工厂方法名
     */
    String getFactoryMethodName();
}
