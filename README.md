# RPC 和相关组件的设计和开发
## my-rpc
写RPC之前先把相关的组件写好：
- 序列化/反序列化工具 Seri
- 网络连接 my-netty
## my-netty



## Seri

### 优点
- 使用接口、抽象类声明对象并不影响，Seri只关注具体的实现对象。

### 缺点
- 不支持版本号，有安全风险
- 所有类都可以序列化，一些系统类没有排除，没有JDIK序列化那样通过Serialiable指定那些可以序列化，可能会造成一些问题。
- 内部类的支持不好.
- 码流可能比较大
- 除了单纯的基本类型值外，不支持基本类型出现在数组、List、Map等地方。 比如 ArrayList<byte[][]>
  bytes = new ArrayList<>();就是不支持的，使用包装类型则支持
- 依赖于java，天生不能跨语言。
- 目前功能有限，结构设计还不太合理，还需要继续改进优化。

### 使用教程
1. 引入依赖 seri
2. 使用对象Seri序列化
```java
Seri seri = new Seri();
// 可以使用write()方法，将自动判断类型进行写入
seri.write(obj);
// 当然也可以使用具体的类型方法, 如
seri.writeObject(obj);
seri.writeChar('c');
```
3. 使用Deseri对象进行反序列化, 两种方式生成该对象：
  - seri.toDeseri()
  - new Deseri(byte[] bytes)  
```java
// 同样是自动判断类型进行读取。
deseri.read();
// 读取指定类型的数据
deseri.readObject();
deseri.readDouble()
```
注意：  
当读取到错误类型的数据时，将会抛出TypeNotFoundException异常


### TODO
- 数组这部分写得不好, 有时间了重构一下




## Knowledge
```java
return Float.intBitsToFloat(intOfFloat);
```



