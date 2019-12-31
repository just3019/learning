# JUC

## volatile
内存可见性。当多个线程对共享数据进行操作时，内存可见。
注：  
1.不具备互斥性。
2.不能保证变量的原子性。

## Atomic
1.使用volatile保证内存可见性。
2.通过CAS（Compare-And-Swap）算法保证数据原子性。


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
park和unparkfan方式
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


