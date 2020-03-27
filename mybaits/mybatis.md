#sql预编译防止sql注入
我们使用prepareStatement方式预编译sql，到数据库后进行编译，则确定了关键字，执行逻辑之类的东西，后续编译注入的部分职能当做字符串被处理。


#一次mybatis查询流程
先初始化Configuration， 再创建SqlSessionFactory，再创建SqlSession，最后执行sql（先看cache，如果没有cache从数据库中查询）
Configuration
SqlSessionFactory
SqlSession
Executor
MappedStatement:封装了sql
StatementHandler:操作数据库的接口
ResultSetHandler:返回值的解析接口

#mybatis 开启缓存
一级缓存：作用范围是SqlSession，存储在localCache中
```xml
<setting name="localCacheScope" value="SESSION"/>
```
二级缓存：
```xml
<setting name="cacheEnabled" value="true"/>
```
