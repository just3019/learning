package org.demon.ioc;

/**
 * @author demon
 * @version 1.0.0
 */
public interface BeanDefinitionRegistry {
    /**
     * 注册Bean定义到容器中
     */
    void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception;
    /**
     * 根据beanName获取bean定义
     */
    BeanDefinition getBeanDefinition(String beanName);
    /**
     * 判断bean定义是否存在
     */
    Boolean containsBeanDefinition(String beanName);
}
