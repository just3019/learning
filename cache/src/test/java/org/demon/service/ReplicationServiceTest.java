package org.demon.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author demon
 * @version 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ReplicationServiceTest {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Before
//    public void before() {
//        System.setProperty("mode", "replication");
//    }

    @Test
    public void test() {
        //redisTemplate.opsForValue().set("b", "b");
        for (int i = 0; i < 10; i++) {
            redisTemplate.opsForValue().get("a");

        }
    }
}
