package org.demon.juc;

import org.junit.jupiter.api.Test;

/**
 * wait notify方式
 *
 * @author demon
 * @version 1.0.0
 */
public class ThreadDemo5 {

    Object baozidian = null;

    @Test
    public void test() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                synchronized (this) {
                    System.out.println("1.no baozi,wait");
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("2.buy baozi");
        });
        thread.start();
        Thread.sleep(1000);
        baozidian = new Object();
        synchronized (this) {
            this.notify();
        }
        System.out.println("3.notify");
    }

    @Test
    public void test2() throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (baozidian == null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (this) {
                    try {
                        System.out.println("1.no baozi,wait");
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("2.buy baozi");
        });
        thread.start();
        Thread.sleep(1000);
        baozidian = new Object();
        synchronized (this) {
            this.notify();
        }
        System.out.println("3.notify");
    }
}
