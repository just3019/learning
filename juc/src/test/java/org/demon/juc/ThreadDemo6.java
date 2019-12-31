package org.demon.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * park unpark方式
 *
 * @author demon
 * @version 1.0.0
 */
public class ThreadDemo6 {

    Object baozidian = null;

    @Test
    public void test() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                System.out.println("1.wait");
                LockSupport.park();
            }
            System.out.println("2.buy");
        });
        thread.start();
        Thread.sleep(1000);
        LockSupport.unpark(thread);
        System.out.println("3.notify");
    }

    @Test
    public void test2() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                System.out.println("1.wait");
                synchronized (this) {
                    LockSupport.park();
                }
            }
            System.out.println("2.buy");
        });
        thread.start();
        Thread.sleep(1000);
        synchronized (this) {
            LockSupport.unpark(thread);
        }
        System.out.println("3.notify");
    }
}
