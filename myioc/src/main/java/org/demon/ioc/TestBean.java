package org.demon.ioc;

/**
 * @author demon
 * @version 1.0.0
 */
public class TestBean {

    public TestBean(){}

    public TestBean(TestBean2 testBean2) {
    }

    public void test() {
        System.out.println("test方法  " + this);
    }
}
