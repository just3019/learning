# JUC

## volatile
内存可见性。当多个线程对共享数据进行操作时，内存可见(禁止缓存)，防止指令重排。
注：  
1.不具备互斥性。
2.不能保证变量的原子性。

## Atomic
1.使用volatile保证内存可见性。
2.通过Unsafe类中的CAS（Compare-And-Swap）算法保证数据原子性。
CAS问题：
1大量的循环，会消耗大量CPU资源。
2.只能针对单个变量的操作。
3.ABA问题。
###java1.8增加LongAdder类，使用了分组的概念，提升了性能。

## Thread
### 线程状态流转图
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/thread1.png)

### 线程终止  
不建议使用stop()方法，stop方法会在线程未执行完直接停止掉线程。  
推荐使用interrupt()方法，interrupt方法会抛出**InterruptedException**这个异常，我们可以根据异常去处理后续逻辑。

### 线程通讯
1、文件共享
2、网络共享
3、共享变量
4、jdk提供的api

suspend和resume方式
会出现死锁，同步代码块方式；resume比suspend早执行。
```java
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
```
wait和notify方式
wait可以自动解锁，但是对顺序有要求(顺序错误会导致死锁)。
```java
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
```
park和unpark方式
park不会释放锁，也可能导致死锁，如果在同步代码块中执行，则会死锁
```java
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
```
## 线程封闭
将数据封闭到各自线程中，就不需要做同步操作，线程安全。
### ThreadLocal
```
ThreadLocal<T> var = new ThreadLocal<T>();
```
### 栈封闭
局部变量就是各自线程特有的，其他线程无法访问。

## 线程池 
| 类型 | 名称 | 描述 |
|:--------:| :-------------:|:------------:|
| 接口 | Executor | 最上层的接口，定义执行任务的execute |
| 接口 | ExecutorService | 集成Executor接口，拓展了Callable、Future、关闭方法 |
| 接口 | ScheduledExecutorService | 集成ExecutorService接口，增加定时任务相关方法 |
| 实现类 | ThreadPoolExecutor | 基础的线程池实现 |
| 实现类 | ScheduledThreadPoolExecutor | 继承ThreadPoolExecutor，实现ScheduledExecutorService接口中的定时任务方法 |

### 线程池执行原理
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/threadpool1.png)

## Lock

### ReentrantLock
可重入锁。同一个线程可以多次lock()。如果其他线程想要使用的话，必须锁几次，释放几次。

### ReadWriteLock
读写锁。当有读锁存在的时候，写锁不能获取锁。写锁是线程独占，读锁是共享。
锁降级指写锁降级为读锁。
示例：LockDemo2.java

### AQS
提供了对资源占用、释放，线程的等待、唤醒等接口和具体实现。
包括：链表(Node)，标志位(owner)，状态(state)
独享资源使用 tryAcquire、tryRelease方法实现。操作owner属性
共享资源使用 tryAcquireShare、tryReleaseShare方法实现。操作链表。
// acquire、 acquireShared ： 定义了资源争用的逻辑，如果没拿到，则等待。
// tryAcquire、 tryAcquireShared ： 实际执行占用资源的操作，如何判定一个由使用者具体去实现。
// release、 releaseShared ： 定义释放资源的逻辑，释放之后，通知后续节点进行争抢。
// tryRelease、 tryReleaseShared： 实际执行资源释放的操作，具体的AQS使用者去实现。

### CopyOnWriteArrayList
每次写都加锁，并且创建一个新的数组，写完后再替换掉旧的数组。获取数据不加锁。

### CyclicBarrier
使用：每慢多少个事件后执行
场景：当达到一定数量去操作。（批量处理）

### Semaphore
场景：限流

### ForkJoin
适合数据处理，结果汇总。
配合RecursiveTask和RecursiveAction

