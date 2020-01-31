package org.demon.service;

import lombok.extern.slf4j.Slf4j;
import org.demon.pojo.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author demon
 * @version 1.0.0
 */
@Service
@Slf4j
public class SpringCacheService {

    @Cacheable(value = "cache-user", key = "#username")
    public User findUserByUsername(String username) throws InterruptedException {
        User user = new User(username, username + UUID.randomUUID().toString());
        Thread.sleep(100);
        log.info("从数据库中获取{}", username);
        return user;
    }

    @CacheEvict(value = "cache-user", key = "#username")
    public void deleteUserByUsername(String username) {
        log.info("从数据库中删除{}", username);
    }

    @CachePut(value = "cache-user", key = "#user.username", condition = "#result ne null")
    public User updateUser(User user){
        log.info("数据库更新，检查缓存一致性");
        return user;
    }


}
