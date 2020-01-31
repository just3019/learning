package org.demon.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author demon
 * @version 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PipelineTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() {
        long time = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForList().leftPush("queue_1", String.valueOf(i));
        }
        log.info("normal：{}", (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();

        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            for (int i = 0; i < 10000; i++) {
                connection.lPush("queue_2".getBytes(), String.valueOf(i).getBytes());
            }
            return null;
        });

        log.info("pipeline：{}", (System.currentTimeMillis() - time));


    }

}
