# ioc -- 控制反转
将原先由客户端自己创建对象，反转为使用IOC容器创建对象。
>1.可以是代码更加整洁，不需要new对象。  
2.面向接口编程，使用类和具体类解耦，易扩展。  
3.方便进行AOP增强。

## ioc 设计
整体思想是：创建和管理Bean，可以想象成一个bean工厂。

## 1.首先需要一个BeanFactory
```java
  public interface BeanFactory {
      /**
       * 因为是Bean工厂，需要对外提供获取Bean方法。
       * 因为Bean是类型不定，所以使用Object作为返回值类型
       */
      Object getBean(String name) throws Exception;
  }
```
这个接口提供外部获取Bean方法。

## 2.下一步需要往工厂里注册Bean定义
如果要生成Bean的话，需要有这个Bean的生成定义。为了不每次都生成这个Bean定义，我们需要一个Bean定义的注册接口，将所有Bean的定义注册到容器中。
这个时候，我们需要一个Bean定义注册接口(BeanDefinitionRegistry)。
```java
public interface BeanDefinitionRegistry {
    /**
     * 注册Bean定义到容器中
     */
    void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception;
    /**
     * 根据beanName获取bean定义
     */
    BeanDefinition getBeanDefinition(String beanName);
    /**
     * 判断bean定义是否存在
     */
    Boolean containsBeanDefinition(String beanName);
}
```
现在我们需要考虑一下现在有哪几种方式去获得类。
```
1.new一个对象。（需要知道类名）
2.工厂方法。（需要知道工厂类名和工厂方法名）
```
## 3.我们再做Bean注册接口是还需要提供一个Bean的定义类（BeanDefinition），提供注册的时候创建这个类。
```java
public interface BeanDefinition {
    //单例标识
    String SINGLETON = "singleton";
    //原型标识
    String PROTOTYPE = "prototype";

    /**
     * 获取bean是单例还是prototype
     */
    String getScope();

    /**
     * 获取类名，可以通过Class直接new一个对象
     */
    Class<?> getBeanClass();

    /**
     * 获取工厂名
     */
    String getFactoryBeanName();

    /**
     * 获取工厂方法名
     */
    String getFactoryMethodName();
}
```
## 4.有了bean定义接口，我们提供一个通用的bean定义。
```java
@Data
public class GeneralBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String scope = SINGLETON;
    private String factoryBeanName;
    private String factoryMethodName;

    public void SetScope(String scope) {
        if (!StringUtils.isEmpty(scope)) {
            this.scope = scope;
        }
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }
}
```
## 5.至此，我们初始化Bean的准备工作都已经完成，我们可以进行工厂类的实现了。  
第一步：我们需要先把Bean定义注册到工厂容器中。（这里使用Map存储）  
第二步：根据需求，获取Bean定义创建Bean对象。  
第三步：如果是单例，我们将对象缓存起来。（这里使用map）
注：本例我们都默认方法为无参函数，减少代码篇幅方便大家理解意思。
```java
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Cloneable {
    //bean定义的存储
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    //单例bean存储
    private Map<String, Object> beanMap = new ConcurrentHashMap<>(256);

    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception {
        if (containsBeanDefinition(beanName)) {
            throw new Exception("【" + beanName + "】的bean定义已经存在");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public Boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String name) throws Exception {
        return doGetBean(name);
    }

    /**
     * 获取bean对象
     */
    protected Object doGetBean(String beanName) throws Exception {
        Object instance = beanMap.get(beanName);
        if (instance != null) {
            return instance;
        }
        BeanDefinition beanDefinition = getBeanDefinition(beanName);

        Class<?> type = beanDefinition.getBeanClass();

        /*1.new一个对象。（需要知道类名）
        2.工厂方法。（需要知道工厂类名和工厂方法名）*/
        if (type != null) {
            if (beanDefinition.getFactoryMethodName() != null) {
                instance = createInstanceByStaticFactoryMethod(beanDefinition);
            } else {
                instance = createInstanceByConstructor(beanDefinition);
            }
        } else {
            instance = createInstanceByFactoryMethod(beanDefinition);
        }
        if (beanDefinition.getScope().equals(BeanDefinition.SINGLETON)) {
            beanMap.put(beanName, instance);
        }
        return instance;
    }

    /**
     * 根据工厂方法创建对象
     */
    private Object createInstanceByFactoryMethod(BeanDefinition beanDefinition) throws Exception {
        Object factoryBean = doGetBean(beanDefinition.getFactoryBeanName());
        Method method = factoryBean.getClass().getMethod(beanDefinition.getFactoryMethodName(), (Class<?>) null);
        return method.invoke(factoryBean, (Object) null);
    }

    /**
     * 根据静态工厂方法创建对象
     */
    private Object createInstanceByStaticFactoryMethod(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type = beanDefinition.getBeanClass();
        Method method = type.getMethod(beanDefinition.getFactoryMethodName(), (Class<?>) null);
        return method.invoke(type, (Object) null);
    }

    /**
     * new方式创建
     */
    private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws IllegalAccessException, InstantiationException {
        return beanDefinition.getBeanClass().newInstance();
    }
}
```
## 6.创建测试类
```java
public class TestBean {
    public void test() {
        System.out.println("test方法  " + this);
    }
}

public class TestBeanFactory {
    //静态方法
    public static TestBean getStaticTestBean() {
        return new TestBean();
    }
    //工厂方法
    public TestBean getFactoryBean() {
        return new TestBean();
    }
}
```

```java
public class IocTest {

    static DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();

    @Test
    public void testBean() throws Exception {
        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(TestBean.class);
        defaultBeanFactory.registryBeanDefinition("testBean", generalBeanDefinition);
        TestBean testBean = (TestBean) defaultBeanFactory.getBean("testBean");
        testBean.test();
    }

    @Test
    public void testStatic () throws Exception {
        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(TestBeanFactory.class);
        generalBeanDefinition.setFactoryMethodName("getStaticTestBean");
        defaultBeanFactory.registryBeanDefinition("staticBean", generalBeanDefinition);
        TestBean testBean = (TestBean) defaultBeanFactory.getBean("staticBean");
        testBean.test();
    }

    @Test
    public void testFactory() throws Exception{
        //提供工厂bean定义
        GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setBeanClass(TestBeanFactory.class);
        String factoryBeanName = "factoryBeanName";
        defaultBeanFactory.registryBeanDefinition(factoryBeanName, generalBeanDefinition);
        //提供创建对象bean定义
        generalBeanDefinition = new GeneralBeanDefinition();
        generalBeanDefinition.setFactoryBeanName(factoryBeanName);
        generalBeanDefinition.setFactoryMethodName("getFactoryBean");
        defaultBeanFactory.registryBeanDefinition("factoryBean", generalBeanDefinition);
        TestBean testBean = (TestBean) defaultBeanFactory.getBean("factoryBean");
        testBean.test();
    }
}
```
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/ioc.png)

## 7.总结
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/ioc-simple.png)
该图展示了简易ioc的类关系图。

# DI --- 依赖注入