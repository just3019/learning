package org.demon.ioc;

import org.junit.jupiter.api.Test;

/**
 * @author demon
 * @version 1.0.0
 */
public class IocTest {

    static DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();

    @Test
    public void testBean() throws Exception {
        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(TestBean.class);
        defaultBeanFactory.registryBeanDefinition("testBean", generalBeanDefinition);
        TestBean testBean = (TestBean) defaultBeanFactory.getBean("testBean");
        testBean.test();
    }

    @Test
    public void testStatic () throws Exception {
        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(TestBeanFactory.class);
        generalBeanDefinition.setFactoryMethodName("getStaticTestBean");
        defaultBeanFactory.registryBeanDefinition("staticBean", generalBeanDefinition);
        TestBean testBean = (TestBean) defaultBeanFactory.getBean("staticBean");
        testBean.test();
    }

    @Test
    public void testFactory() throws Exception{
        //提供工厂bean定义
        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(TestBeanFactory.class);
        String factoryBeanName = "factoryBeanName";
        defaultBeanFactory.registryBeanDefinition(factoryBeanName, generalBeanDefinition);
        //提供创建对象bean定义
        generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setFactoryBeanName(factoryBeanName);
        generalBeanDefinition.setFactoryMethodName("getFactoryBean");
        defaultBeanFactory.registryBeanDefinition("factoryBean", generalBeanDefinition);
        TestBean testBean = (TestBean) defaultBeanFactory.getBean("factoryBean");
        testBean.test();
    }
}
