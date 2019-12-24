# 手写aop理解springaop原理

## 引入场景
公司要求某个新开发的服务，需要检测这个服务所有方法的耗时情况。  
当看到这个需求，很容易想到使用System.currentTimeMillis()包裹需要检测的方法。但如果在所有方法上都添加，既耗时，又浸入代码。所以肯定不能用这种方式。这是就可以用aop方式，用切面的方式动态切入方法，获取每个方法的耗时时间。  
这里就不给大家提供springaop的编写例子了，我们自己手写一个aop来体会springaop的原理。  
为后续手写aop框架提供一个简单的业务方法。
```java
@Data
public class User {
    private String userName;
    private String password;
}
```
```java
public interface UserService {
    User getUser1();
    User getUser2();
}
```
```java
public class UserServiceImpl implements UserService {
    
    private Random random = new Random();

    public User getUser1() {
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setUserName("user1");
        user.setPassword("111111");
        return user;
    }

    public User getUser2() {
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setUserName("user2");
        user.setPassword("222222");
        return user;
    }
}
```
```java
public class AopTest {

    @Test
    public void test() throws Exception {

        UserService userService = new UserServiceImpl();
        System.out.println(userService.getUser1());
        System.out.println(userService.getUser2());
    }
}
```
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/aop1.png)

## aop介绍
aop我们围绕着  
>1.什么是切面？  
>2.什么是切入点？  
>3.什么是连接点？  
>4.什么是通知？  
>5.切入的方式？  
>6.什么是织入？

![](https://demonself.oss-cn-hangzhou.aliyuncs.com/aop.png)
#### 图解：当我们在执行程序过程中，我们需要再流程中执行的某个点加入一些增强逻辑。那么这个点就是切入点和增强逻辑组成了我们的aop

1.切面就是切入点+增强逻辑。  
2.切入点就是程序执行过程中需要增强逻辑的点。  
3.连接点就是整个执行流程中的各个方法点。  
4.通知就是advice，可以当做增强逻辑。  
5.spring提供了aspect方式  
6.织入就是动态代理，通过动动态代理将增强逻辑织入类中。  

## 手写一个aop
我们可以想一下，在使用springaop的时候，我们只需要定义一个**连接点（pointcut）**和**增强逻辑（advice）**就可以实现aop业务。那我们手写的话也只需要对外提供这两个模块就可以了。
1.我们先定义增强功能。  
提供统一的API 接口 Advice
```java
public interface Advice {

    /**
     * 提供增强接口，参数需要谁？干什么？
     *
     * @param target 需要知道目标对象
     * @param method 需要知道目标方法
     * @param args   方法参数
     */
    Object invoke(Object target, Method method, Object[] args) throws Exception;
}
```
2.需要给定义切入点
```java
@Data
@AllArgsConstructor
public class PointCut {
    //切入点的颗粒度需要到某个类下面的某个方法
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methorName;

}
```
以上两个类就是组成aop的两个组件。我们再写一个AOP组装类Aspect
```java
@Data
@AllArgsConstructor
public class Aspect {
    /**
     * 增强逻辑
     */
    private Advice advice;
    /**
     * 切入点
     */
    private PointCut pointCut;
}
```
然后我们就需要考虑了，有了aop增强的工具，我们怎么把这些组件织入到我们的业务类中呢？这就是spring ioc起作用了。所以我们这边再定义一个简易的ioc（**IocContainer**）。这里我们的ioc最主要就是做两个事情，一个是加载bean到ioc容器，还有一个就是从容器中获取bean的方法getBean()。我们做增强织入主要是针对获取bean的时候进行动态代理，生成一个代理类提供给客户端使用。本示例提供jdk动态代理（**AopInvocationHandler**）。
```java
public class IocContainer {

    private Map<String, Class<?>> beanDefinitionMap = new HashMap<>();
    /**
     * 织入切面aop
     */
    @Getter
    @Setter
    private Aspect aspect;
    /**
     * 往容器中添加bean
     */
    public void addBeanDefinition(String beanName, Class<?> clazz) {
        beanDefinitionMap.put(beanName, clazz);
    }
    //获取bean
    public Object getBean(String beanName) throws Exception {
        Object object = createInstance(beanName);
        object = proxyEnhance(object);
        return object;
    }
    /**
     * 代理增强方法
     */
    private Object proxyEnhance(Object bean) {
        if (bean.getClass().getName().matches(aspect.getPointCut().getClassName())) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(),
                    new AopInvocationHandler(bean, aspect));
        }
        return bean;
    }
    /**
     * 创建实例
     */
    private Object createInstance(String beanName) throws Exception {
        return beanDefinitionMap.get(beanName).newInstance();
    }
}
```
```java
@Data
@AllArgsConstructor
public class AopInvocationHandler implements InvocationHandler {
    //目标对象
    private Object target;
    //aop对象
    private Aspect aspect;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //需要增强
        if (method.getName().matches(aspect.getPointCut().getMethorName())) {
            return aspect.getAdvice().invoke(target, method, args);
        }
        return method.invoke(target, args);
    }
}
```
到此为止，手写的aop架子就已经出来了。我们可以把方法增强测试一下。  
我们定义一个增强方法TimeCsAdvice类来记录每个方法的执行时间
```java
@Slf4j
public class TimeCsAdvice implements Advice {
    @Override
    public Object invoke(Object target, Method method, Object[] args) throws Exception {
        long start = System.currentTimeMillis();
        Object res = method.invoke(target, args);
        long use = System.currentTimeMillis() - start;
        log.info("类：{}，方法：{}，耗时：{}", target.getClass().getName(), method.getName(), use);
        return res;
    }
}
```

还是原来的测试类。
```java
public class AopTest {
    @Test
    public void test() throws Exception {
        // 定义aop的各个节点。
        Advice advice = new TimeCsAdvice();
        //增强org.demon.service.impl这个包下面的所有类的所有方法。
        PointCut pointCut = new PointCut("org\\.demon\\.service\\.impl..*", ".*");
        Aspect aspect = new Aspect(advice, pointCut);
        //需要将业务代码织入增强逻辑。
        IocContainer ioc = new IocContainer();
        ioc.setAspect(aspect);
        ioc.addBeanDefinition("userService", UserServiceImpl.class);
        UserService userService = (UserService) ioc.getBean("userService");
        System.out.println(userService.getUser1());
        System.out.println(userService.getUser2());
    }
}
```
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/aop2.png)

## 使用场景
当你的服务需要很多方法增加相同的逻辑的时候可以使用aop。  
* 权限控制
* 事务处理
* 性能统计

## 总结
至此我们已经成功手动编写了一个aop框架。<br/>
**划重点**  
提供增强接口Advice；  
提供切入点类PointCut；  
提供Aop类Aspect；  
提供Ioc容器做增强织入；  


