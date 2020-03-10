#java设计模式

##代理模式
###jdk代理
####为什么只能代理接口？
动态生成的代理类继承了Proxy类，就不能再继承其他的类了。所有只能通过实现被代理的接口。
我们可以再代码中修改环境变量 System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
这时我们运行后会产生一个**$Proxy0.class**增强过后的类。当我们看这个类的时候，他已经继承了Proxy类，就不能继承其他类。

###cglib代理