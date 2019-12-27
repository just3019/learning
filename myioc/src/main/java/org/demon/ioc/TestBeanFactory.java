package org.demon.ioc;

/**
 * @author demon
 * @version 1.0.0
 */
public class TestBeanFactory {
    //静态方法
    public static TestBean getStaticTestBean() {
        return new TestBean();
    }
    //工厂方法
    public TestBean getFactoryBean() {
        return new TestBean();
    }
}
