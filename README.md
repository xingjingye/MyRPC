该项目为自己练习造的RPC轮子，采用netty进行通信，Nacos作为注册中心，自定义了通信协议、编码器、解码器，实现了两种序列化算法，Kryo序列化和Json序列化，实现了两种负载均衡算法，随机算法和轮转算法，使用钩子函数注销Nacos中的服务。

![image-20230527224535361](C:\Users\XingJingYe\AppData\Roaming\Typora\typora-user-images\image-20230527224535361.png)