#

## 七层协议
低三层：  
    物理层：  
    数据链路层：  
    网络层：进行路由选择和流量控制。（IP协议）  
传输层：提供可靠的端口到端口的数据传输服务（TCP/UDP协议）。
高三层：  
    会话层：  
    表示层：  
    应用层：  

TCP:面向连接、可靠、慢、资源占用多。三次握手，四次挥手。
UDP:无连接、不可靠、快、资源占用少。（音视频聊天，定位上报）

### socket api
创建套接字->断点绑定->发送数据->接受数据->释放套接字。  
listen()、accept()函数只用于服务端；  
connect()函数只用于客户端；

## BIO网络编程
阻塞和非阻塞是获取资源方式；同步和异步是程序如何处理资源的逻辑。


## NIO
三大组件：Buffer，Channel，Selector

### Buffer
position：写模式的时候代表写数据的位置；读模式时代表读的位置。
capacity：容量大小。
limit：写模式等于buffer的容量；读模式limit等于写入数据量。

提供了直接内存（direct堆外）和非直接内存（heap堆）
```
ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(10);
```
如果使用堆外内存，建议分配给大对象（网络传输、文件读写）；通过虚拟机参数MaxDirectMemorySize限制，防止耗尽整个机器内存。

### Channel

### Selector
四个时间分别对应SelectionKey四个常量。
1. 读取：OP_READ = 1;
2. 写入：OP_WRITE = 4;
3. 连接：OP_CONNECT = 8;
4. 准备就绪：OP_ACCEPT 


## Netty

###启动流程
1.初始化两个EventLoopGroup对象（parentGroup和childGroup,parent作为acceptor使用，child作为事件轮询器）
2.初始化ServerBootstrap。

###Reactor模型

###Channel

###ChannelPipeline责任链设计模式
创建channel时自动创建一个pipeline。

###内存管理 ByteBuf
