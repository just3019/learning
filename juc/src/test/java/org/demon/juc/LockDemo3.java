package org.demon.juc;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @author demon
 * @version 1.0.0
 */
public class LockDemo3 implements Lock {

    volatile AtomicReference<Thread> owner = null;

    volatile LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<>();


    @Override
    public void lock() {
        while (!tryLock()) {
            // 尝试失败进入等待队列
            waiters.offer(Thread.currentThread());
            LockSupport.park();
            waiters.remove(Thread.currentThread());

        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return owner.compareAndSet(null, Thread.currentThread());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        if (owner.compareAndSet(Thread.currentThread(), null)) {
            Iterator<Thread> iterator = waiters.iterator();
            while (iterator.hasNext()) {
                Thread waiter = iterator.next();
                LockSupport.unpark(waiter);
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
