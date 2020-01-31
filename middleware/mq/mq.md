# 消息中间件

## 什么是消息中间件

## 协议

## 持久化

## 消息分发

## 高可用
1.主从共享模式
2.主从同步模式
3.多主同步模式
4.多主集群转发(代理)模式
5.组合
## 高可靠
消息传输可靠
消息存储可靠


#ActiveMQ
## 是什么？

## JMS规范
Java Message Service  
JMS对象模型
| 属性 | 描述 |
|:--------:| :-------------:|
|ConnectionFactory    |连接工厂|
|Connection           |链接|
|Session              |会话|
|Destination          |目的|
|MessageProducer      |生产者|
|MessageConsumer      |消费者|
|Message              |消息|
|Broker               |消息中间件实例|

## 特性

## 安装

## 使用


#kafka
一个分布式流处理平台

Producer Api：允许一个应用程序发布一串流式的数据到一个或者多个topic
Consumer Api：允许一个应用程序订阅一个或者多个topic，并对发布给他们的流式数据进行处理
Streams Api：允许一个应用作为一个流处理器，消费一个或者多个topic产生 输入流，然后生产一个输出流到一个或者多个topic
Connector Api：允许构建并运行可重用的生产者或者消费者，将topics连接到已存在的应用程序或者数据系统。

## ISR（in sync replica）
当前正在同步的副本数据。（活着的副本）

## stream
参考：http://kafka.apache.org/24/documentation/streams/quickstart
必须创建两个topic ，一个接收的，一个输出的。

#zookeeper
















