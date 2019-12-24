package org.demon.service.impl;

import org.demon.entity.User;
import org.demon.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author demon
 * @version 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    private Random random = new Random();

    public User getUser1() {
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setUserName("user1");
        user.setPassword("111111");
        return user;
    }

    public User getUser2() {
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setUserName("user2");
        user.setPassword("222222");
        return user;
    }
}
