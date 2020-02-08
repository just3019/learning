# 缓存

## redis

### 主从复制
#### 命令行
```
# 加入集群
slaveof [ip] [port]
# 退出集群
slaveof no one
```

#### j2cache 减少网络io太大问题（多级缓存）

#### 监控工具

##### redis-stat
redis stat是用Ruby编写的一个简单的redis监控工具。
[github地址](https://github.com/junegunn/redis-stat)
######安装
```
#redis stat是用Ruby编写的一个简单的redis监控工具。
gem install redis-stat
```
######运行
```
-v, --verbose                    显示更多信息
--style=STYLE                输出样式：unicode | ascii
--no-color                   禁止显示ANSI颜色代码
--csv=OUTPUT_CSV_FILE_PATH   将结果保存为CSV格式
--es=ELASTICSEARCH_URL       将结果发送到ElasticSearch: [http://]HOST[:PORT][/INDEX]
--server[=PORT]              启动redis-stat Web服务器（默认端口：63790）
--daemon                     后台启动redis-stat。必须与--server选项一起使用。
--version                    显示版本
redis-stat --verbose --daemon --server=8080 127.0.0.1:6381 127.0.0.1:6382 127.0.0.1:6383 127.0.0.1:6384 127.0.0.1:6385 127.0.0.1:6386 5
```
