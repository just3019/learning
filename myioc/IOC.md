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
通过上面篇幅讲的ioc过程中，我们只能进行无参构造函数创建对象。但真实工作中不是这么简单的。我们类的对象会有不同类型的构造参数和属性，这个时候使用上面的simple-ioc框架就创建不了对象了。  

## 进一步细化bean定义
我们需要做几件事：
1.设置这个类的构造参数（我们先只考虑基础数据类型和Bean引用）
2.获取这个类的构造参数
3.获取这个类对应的构造函数

### 第一步 
首先，我们需要再bean定义接口提供设置构造参数的方法(BeanDefinition)
```
List<?> getConstructorArgumentValues();
```
通用的bean定义类需要添加构造参数的属性用来存储。(GeneralBeanDefinition)
```
private List<?> constructorArgumentValues;
```
我们使用List<?>来存储构造参数，当数据类型是Bean的时候，我们需要先将Bean参数获取。为了能识别哪些类是bean对象，我们提供一个BeanReference类，如果是bean引用的参数，继承BeanReference。
```java
@AllArgsConstructor
public class BeanReference {
    @Getter
    private String beanName;
}
```
## 第二步 有了Bean标识，我们可以再DefaultBeanFactory中添加获取构造参数的方法
```
/**
 * 获取bean定义中的构造参数
 */
private Object[] getConConstructorArgumentValues(BeanDefinition beanDefinition) throws Exception {
    return getRealValues(beanDefinition.getConstructorArgumentValues());
}

/**
 * 获取构造参数
 */
private Object[] getRealValues(List<?> constructorArgumentValues) throws Exception {
    if (CollectionUtils.isEmpty(constructorArgumentValues)) {
        return null;
    }
    int size = constructorArgumentValues.size();
    Object[] values = new Object[constructorArgumentValues.size()];
    for (int i = 0; i < size; i++) {
        if (constructorArgumentValues.get(i) instanceof BeanReference) {
            values[i] = doGetBean(((BeanReference) constructorArgumentValues.get(i)).getBeanName());
        } else {
            values[i] = constructorArgumentValues.get(i);
        }
    }
    return values;
}
```
### 第三步
参数有了我们需要找对应的构造参数，我们使用Class类中的方法寻找。
我们再DefaultBeanFactory类中定义查询我们需要的构造方法
```
/**
 * 查询构造方法
 */
private Constructor<?> determineConstructor(BeanDefinition beanDefinition, Object[] args) throws Exception {
    Constructor<?> constructor = beanDefinition.getConstructor();
    if (constructor != null) {
        return constructor;
    }
    //如果无参，返回无参构造函数
    if (args == null) {
        return beanDefinition.getBeanClass().getConstructor(null);
    }
    Class<?>[] paramTypes = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
        paramTypes[i] = args[i].getClass();
    }
    constructor = beanDefinition.getBeanClass().getConstructor(paramTypes);
    //如果不是单例，则可以直接存Bean定义中
    if (constructor != null && beanDefinition.getScope().equals(BeanDefinition.PROTOTYPE)) {
        beanDefinition.setConstructor(constructor);
    }
    return constructor;
}
```
## 有了构造参数和构造函数，我们就可以修改创建类的方法了
DefaultBeanFactory
```
/**
 * new方式创建,新增有参构造方法实现
 */
private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws Exception {
    Object[] args = getConstructorArgumentValues(beanDefinition);
    if (args == null) {
        return beanDefinition.getBeanClass().newInstance();
    } else {
        return determineConstructor(beanDefinition, args).newInstance(args);
    }
}
```

## 开始写测试用例。
修改原先的TestBean类，再添加一个TestBean2类
```java
public class TestBean {

    public TestBean(){}

    public TestBean(TestBean2 testBean2) {
    }

    public void test() {
        System.out.println("test方法  " + this);
    }
}

public class TestBean2 {

    public TestBean2(){}

    public void test() {
        System.out.println("testBean2方法  " + this);
    }
}
```
添加我们的测试方法
```
@Test
public void testDi() throws Exception {
    GeneralBeanDefinition generalBeanDefinition = new GeneralBeanDefinition();
    generalBeanDefinition.setBeanClass(TestBean2.class);
    defaultBeanFactory.registryBeanDefinition("testBean2", generalBeanDefinition);

    generalBeanDefinition = new GeneralBeanDefinition();
    generalBeanDefinition.setBeanClass(TestBean.class);
    List<Object> args = new ArrayList<>();
    args.add(new BeanReference("testBean2"));
    generalBeanDefinition.setConstructorArgumentValues(args);
    defaultBeanFactory.registryBeanDefinition("testBean", generalBeanDefinition);


    TestBean testBean = (TestBean) defaultBeanFactory.getBean("testBean");
    testBean.test();
}
```
打印结果
```
test方法  org.demon.ioc.TestBean@36902638
```
至此简版的依赖注入也已经完成

## 总结
依赖注入我们重点关注构造参数和构造方法。
我们还可以扩展通过工厂方法，静态方法等方式创建对象。

提供一个spring ioc学习路线：
IOC-->DI-->AOP-->TX

ps: 示例工程地址： https://github.com/just3019/learning.git