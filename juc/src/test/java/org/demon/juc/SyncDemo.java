package org.demon.juc;

import org.junit.jupiter.api.Test;

/**
 * @author demon
 * @version 1.0.0
 */
public class SyncDemo {

    public synchronized void test1(Object arg) {
        System.out.println(Thread.currentThread().getName() + " " + arg);
        if (arg == null) {
            test1(new Object());
        }
        System.out.println(Thread.currentThread().getName() + " " + arg);
    }

    @Test
    public void test(){
        test1(null);
    }

}
