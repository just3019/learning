package org.demon.service;

import org.demon.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * @author demon
 * @version 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCacheServiceTest {

    @Autowired
    private SpringCacheService springCacheService;

    @Test
    public void findUserByUsernameTest() throws InterruptedException {
        springCacheService.findUserByUsername("hello");
    }

    @Test
    public void deleteUserByUsernameTest() {
        springCacheService.deleteUserByUsername("hello");
    }

    @Test
    public void updateUserTest() {
        User user = new User("hello", UUID.randomUUID().toString());
        springCacheService.updateUser(user);
    }
}

