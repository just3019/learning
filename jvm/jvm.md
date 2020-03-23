# jvm

## 数据区

### 线程共享
所有线程都能访问共享内存，随虚拟机或者GC而创建销毁
共享部分会产生线程安全问题。
#### 方法区
作用：存储加载的类信息、常量、静态变量、JIT编译后的代码等数据。（存储类的原信息）  
GC：方法区存在GC，单回收效率低。（当类的实例对象还没有被回收的时候，不能回收类对象）  
回收主要针对常量池的回收，和类型的卸载。当方法区无法满足内存需求时，会报OOM。java8变为元数据区
#### 堆
作用：存放对象实例。只是存储了实例字段的值。对于大多数应用来说，堆是JVM管理的内存中的最大的一块内存区域，也是最容易OOM的区域。可以通过（-Xmx，-Xms）控制jvm堆大小。
GC:
引用计数器算法；当计数器减到0则可以回收
可达性分析算法：GC ROOT可以是虚拟机栈；方法中的静态属性；方法区中常量引用对象

### 线程独享
每个线程都会有它独立的空间，随着线程的生命周期而创建销毁 
#### 虚拟机栈
描述方法调用（栈帧）顺序。局部变量存在栈中，成员变量存在堆中，静态变量存在方法区。

### happen-before


## jit
just-in-time compile 即时编译器
优化代码执行顺序。会导致指令重排。
### 指令解释
-Server：server模式启动，速度较慢，但启动之后的性能更高。
-Client: client模式启动，速度较快，但性能不如server模式。
-Djava.compiler=NONE：关闭jit优化。


## 类加载机制

### 类生命周期
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/class1.png)

### 类加载器
Bootstrap loader 核心类库加载器:加载JRE_HOME/jre/lib目录，或者用户配置的目录  
Extension Class loader 拓展类库加载器:加载JRE_HOME/jre/lib/ext（jdk扩展包）目录  
application Class loader 应用程序加载器:加载java.class.path指定的目录

#### 系统热加载
通过创建新的类加载器去加载新编译的类。

### 双亲委派模型
为了避免重复加载，由下到上逐级委托，由上到下逐级查找。
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/class2.png)

## 垃圾回收机制

### 如何确定内存需要被回收
标记：哪些内存正在使用，哪些不在使用。
方式：引用计数；可达性分析；

#### 可达性分析
可以作为GC ROOT的对象：虚拟机栈中正在引用的对象；本地方法栈中正在引用的对象；静态属性引用的对象；方法区常量引用的对象    
看下图，jvm会维护一个GCRoot集合，然后遍历集合，找到GCRoot可以到达的对象。这样就标记上了。
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/class3.png)

#### 引用类型
强引用：必不可少的引用。例如 StringBuilder sb = new StringBuilder("test"); 变量sb指向StringBuilder堆空间，通过sb可以操作该对象。  
软引用：通过SoftReference实现。(一般使用在缓存。)根据JVM当前堆情况判断何时回收。SoftReference<Object> sf = new SoftReference<>(obj);  
弱引用：通过WeakReference实现。一定会被回收，只能存活到下一次垃圾回收发生之前。  
虚引用：通过PhantomReference实现。不能通过他访问对象

###垃圾回收算法
标记-清除法：分为标记和清除两个阶段。首先标记出需要回收的对象，在标记完成后统一回收所有被标记的对象。  
复制算法：将内存分为大小相同的两块，每次使用其中一块。当内存使用完后将还存活的对象复制到另一块中，然后把使用的空间一次清理掉。  
标记-整理法：类似于标记-清理。但是后续是先把存活的对象向一端移动，然后直接清理掉边界以外的内存。  
分代收集算法：将虚拟机分成几块，然后根据每一块的需求使用不同的算法。比如eden区每次都会大量对象死去，可以选择复制算法。老年代对象存活几率高，所以可以使用标记清除或者标记整理
jvm可以设置存活多少次进入老年代和大对象多大进入老年代

### 垃圾收集器 //TODO
串行收集器（Serial）：单个线程来执行所有收集工作，适合单处理器机器。在执行gc的时候，会停止所有其他jvm上的工作。  
并行收集器（parallel）：新生代和老年代可以并行进行。
并发收集器（CMS concurrent Mark Sweep）：尝试和用户线程一起执行。
G1:

### full gc
老年代的连续空间大于新生代所有对象的总大小，则进行minor GC。 否则进行full GC。
> System.gc()方法可能会触发full gc
> 老年代空间不足的时候

### 报OOM

### jvm调优
组件：
堆大小调整
垃圾收集器调整
JIT编辑器

目标：
响应性：应用程序或系统对请求的数据进行响应的速度，对于专注于响应性的程序，长时间暂停不可接受（GC会导致stop-the-word）
吞吐量：特定时间内，最大化应用程序的工作量。对于专注于吞吐量的程序，高暂停可以接受。

GC调优思路：
1.场景分析
2.确定目标
3.收集日志
4.分析日志
5.调整参数

#### JVM参数设置
标准参数：-help； -version之类的
X参数：
XX参数：Boolean参数和KV参数

查询jvm初始化值：java -XX:+PrintFlagsInitial
查询本机的jvm初始化值：java -XX:+PrintFlagsFinal
查看默认执行本机jvm的初始值：java -XX:+PrintCommandLineFlags

-XX:+PrintGCDetails 查看GC日志
-XX:SurvivorRatio 设置eden:from:to 默认为8：1：1
-XX:NewRatio 设置新生代占多少比例 默认为1/3
-XX:MaxTenuringThreshold 设置用于自适应GC分级的最大拉伸阈值。默认15  

-verbose:gc -Xloggc:gc.log -XX:+PrintGCDetails  
打印JVM GC日志
-XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=?
当报OOM时候，记录一个内存快照  
如果做OOM内存快照，那内存、CPU不要完全用完、预留1/3的内存
-Xms4g -Xmx4g  
当内存达到一定大小后，jvm进行gc后，会恢复到Xms参数。生产环境最好这两个参数设置相同，避免来回扩容和缩容。  
-XX:+AlwaysPreTouch  
jvm启动会有预热过程，内存会慢慢扩大。如果想启动立马申请最大内存。
#### 推荐GC收集器选择
新生代使用：ParNewGC ；老年代：CMS  
#### 过多次小文件处理执行Full gc System.gc()
提前性能测试；避免System.gc()

### tomcat调优
connectionTimeout 20s             减少
maxThreads        200             增加
acceptCount       100             增加  超过Tomcat的接受数后，堆积操作系统的数量。
maxConnections    nio 1w;apr 8192 不变  同时有多少线程可以执行

调整maxConnections：connections小于maxThread的时候，需要调大。最好是比预期的最高并发数大20%。
调整acceptCount：不需要调整。

### 线上故障处理流程
告警--->初步判断--->(保留现场（日志分析）/重启)

### CPU消耗很大
top命令监控
请求慢：jstack去查看
死锁：jstack去查看












