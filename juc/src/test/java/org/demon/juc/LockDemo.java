package org.demon.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author demon
 * @version 1.0.0
 */
public class LockDemo {
    volatile int i;

    Lock lock = new ReentrantLock();

    public void add() {
        lock.lock();
        try {
            i++;
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void test() throws InterruptedException {
        LockDemo lockDemo = new LockDemo();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    lockDemo.add();
                }
            }).start();
        }
        Thread.sleep(2000);
        System.out.println(lockDemo.i);
    }
}
