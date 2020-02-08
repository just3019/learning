package org.demon.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author demon
 * @version 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ClusterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() throws InterruptedException {
        // 每个一秒钟，操作一下redis，看看最终效果
        int i = 0;
        while (true) {
            i++;
            redisTemplate.opsForValue().set("test-value" + i, String.valueOf(i));
            System.out.println("修改test-value" + i + "值为: " + i);
            Thread.sleep(100L);
        }
    }

}
