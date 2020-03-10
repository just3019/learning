package org.demon.juc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author demon
 * @version 1.0.0
 */
public class LockDemo2 {

    private Map<String, Object> map = new HashMap<>();

    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    @Test
    public void test() {
        System.out.println(get("1"));
    }


    public Object get(String id) {
        Object value;
        rwl.readLock().lock();
        try {
            if ((value = map.get(id)) == null) {
                rwl.readLock().unlock();
                rwl.writeLock().lock();
                try {
                    value = id;
                    rwl.readLock().lock();//锁降级
                } finally {
                    rwl.writeLock().unlock();
                }
            }
            return value;
        } finally {
            rwl.readLock().unlock();
        }
    }

}
