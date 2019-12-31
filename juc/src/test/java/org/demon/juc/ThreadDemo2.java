package org.demon.juc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 线程状态演示
 *
 * @author demon
 * @version 1.0.0
 */
@Slf4j
public class ThreadDemo2 {

    @Test
    public void test() throws InterruptedException {
        System.out.println("===== new->runnable->terminate ========");
        Thread thread1 = new Thread(() -> {
            log.info("thread1当前状态：{}", Thread.currentThread().getState());
            log.info("thread1执行完了。");
        });
        log.info("thread1状态：{}", thread1.getState());
        thread1.start();
        Thread.sleep(1000);
        log.info("thread1状态：{}", thread1.getState());

        System.out.println("===== new->runnable->wait->runnable->terminate ========");
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("thread2当前状态：{}", Thread.currentThread().getState());
            log.info("thread2执行完了。");
        });
        log.info("thread2未执行状态：{}", thread2.getState());
        thread2.start();
        Thread.sleep(100);
        log.info("100ms thread2状态：{}", thread2.getState());
        Thread.sleep(1000);
        log.info("1000ms thread2状态：{}", thread2.getState());

        System.out.println("===== new->runnable->block->runnable->terminate ========");
        Thread thread3 = new Thread(() -> {
            synchronized (ThreadDemo2.class) {
                log.info("thread2当前状态：{}", Thread.currentThread().getState());
                log.info("thread2执行完了。");
            }
        });

        synchronized (ThreadDemo2.class) {
            log.info("thread3 未执行状态：{}", thread3.getState());
            thread3.start();
            log.info("thread3 开始后状态：{}", thread3.getState());
            Thread.sleep(100);
            log.info("thread3 100ms 状态：{}", thread3.getState());
        }
        Thread.sleep(1000);
        log.info("1000ms thread3状态：{}", thread3.getState());
    }
}
