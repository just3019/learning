package org.demon.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author demon
 * @version 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOps;

    @Test
    public void addLink() throws MalformedURLException {
        String userId = "aaa";
        URL url = new URL("http://www.baidu.com");
        //listOps.leftPush(userId, url.toExternalForm());
        listOps.leftPop(userId);
    }

}
