package org.demon.spring;

import org.demon.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author demon
 * @version 1.0.0
 */
public class IocTest {

    @Test
    public void test(){
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-test.xml");
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("classpath:spring-test.xml");
        User user = (User) context.getBean("user");
        System.out.println(user.toString());
    }
}
