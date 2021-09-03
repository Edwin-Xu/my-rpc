# My Netty
## Reactor 模式
根据 Reactor 的数量和处理资源线程的数量不同，有三种典型的实现：  
- 单 Reactor 单线程
- 单 Reactor 多线程
- 主从 Reactor 多线程  

采用单Reactor多线程模式
## 图示


## Design Problems
### Server监听事件类型
client和server很不一样，client先写后读，server先读后写，不断重复？
这是一个长连接，how？如果同时监听读写，感觉不太好，开销大
Client 请求，server读，然后处理业务逻辑，然后写。所以只需要监听READ事件即可


### 程序怎么读写数据
使用Handler，将业务留给用户。 即用于将业务实现在handler中即可，可以有多个handler。
本网络连接工具本身不关注业务逻辑。

### 长链接？
先使用短连接吧，每次请求建立连接。




     