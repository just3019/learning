package org.demon.juc;

import org.junit.jupiter.api.Test;

/**
 * suspend 和resume方式
 *
 * @author demon
 * @version 1.0.0
 */
public class ThreadDemo4 {

    Object baozidian = null;

    @Test
    public void test() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                System.out.println("1.no baozi,wait");
                Thread.currentThread().suspend();
            }
            System.out.println("2.buy baozi");
        });
        thread.start();
        Thread.sleep(1000);
        baozidian = new Object();
        thread.resume();
        System.out.println("3.notify");
    }

    @Test
    public void test2() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                System.out.println("1.no baozi,wait");
                synchronized (this) {
                    Thread.currentThread().suspend();
                }
            }
            System.out.println("2.buy baozi");
        });
        thread.start();
        Thread.sleep(1000);
        baozidian = new Object();
        synchronized (this) {
            thread.resume();
        }
        System.out.println("3.notify");
    }

    @Test
    public void test3() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                System.out.println("1.no baozi,wait");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().suspend();
            }
            System.out.println("2.buy baozi");
        });
        thread.start();
        Thread.sleep(1000);
        baozidian = new Object();
        thread.resume();
        System.out.println("3.notify");
    }
}
