## 目录
* [说明](#1)
* [功能](#2)
* [如何使用](#3)
* [参考帮助](#4)

## 更新日志
2015年12月8日 zer0MQTTServer第一版，实现了MQTT协议

2016年05月25日 zer0MQTTServer第二版，协议通信底层由 Java AIO 切换到 Netty5.0，使用netty的编码解码模块功能重写了全部的协议编码解码

## <a name="1">说明</a>

MQTT 协议是 IBM 开发的即时通讯协议，相对于 IM 的实际上的准标准协议 XMPP 来说，MQTT 更小，更快，更轻量。MQTT 适合于任何计算能力有限，工作在低带宽、不可靠的网络中的设备，包括手机，传感器等等。

开发此客户端的目的，是因为实际项目中需要用到推送、即时通讯的内容，而第三方平台有时候不稳定，遇到问题难于调试。所以决定自己开发一套即时通讯系统。选用 MQTT 的原因正如上所说，它比XMPP更适合手机端使用。具体比较请参看：[Android推送方案分析（MQTT/XMPP/GCM）](http://m.oschina.net/blog/82059)。

项目断断续续写了快一年（2015年2月~12月），大部分在业余时间完成。代码中的每个函数都有明确的中文注释信息，对于 MQTT 实现的部分，更是细节到每个功能对应的文档的页数都进行了标明。此服务器既可以针对具体项目二次开发使用，也可以用于 MQTT 协议的学习。

## <a name="2">功能</a>
已实现：
* 网络传输功能（使用 Netty5.0 实现）~~
* 会话管理功能
* 任务调度框架（使用Quartz框架为基础封装）
* MQTT完整实现（推送，单聊，群聊）


未实现：
* ~~安全层（消息加密、解密、防重放，防中间人等等）~~
* ~~好友功能（添加好友，删除好友，好友列表等等）~~
* ~~群组管理功能（添加群组，退出群组等等）~~
* ~~语音~~
* ~~视频~~
* ~~总之就是可以拓展的应用层都没做~~

## <a name="3">如何使用</a>
#### zer0MqttServer 的使用很简单：
* 下载源码（源码中包括所有依赖包）
* 检查 Java 版本是否为1.7或以上，不是则按照 Java 1.7及以上
* 导入IDE
* 引用依赖包
* 运行包 com.syxy.server 下的 StartServer 文件，即可启动服务器。

#### 测试方法也同样简单：

运行包 test 下的 MQTTClientTest 文件，即可开启测试客户端。

测试客户端包括的功能有：连接服务器、订阅主题、发送固定信息，客户端通过 pahoMqtt 第三方jar包编写，你可以自行修改代码进行更详细的测试

#### 下面简述一下项目的目录结构：

com.syxy.util 包中是一些公共类，包括缓冲池BufferPool、任务调度框架QuartzManager、字符串处理类StringTool、日期时间类TimeUtils等等

com.syxy.server 是网络应用层，StartServer 用于启动服务器，并初始化协议相关的类。TcpServer用于处理配置文件中的系统常量，并启动服务器。

com.syxy.protocol.mqttImp 定义了协议编码、解码、业务逻辑接口。并实现了具体的协议编码，解码，业务处理

com.syxy.protocol.mqttImp.message 中包含了14种消息类型的实体类，并将每个消息类型划分成固定头部、可变头部、荷载三部分。

com.syxy.protocol.mqttImp.process 中进行了协议的具体处理。最重要的是```ProtocolProcess.java```文件，其中完整实现了MQTT协议文件中的具体流程。

resource 文件中包含了一些配置文件，其中 mqtt.properties 文件可以针对缓冲区大小、临时存储文件名、服务器端口等信息

## <a name="4">参考帮助</a>

1. [moquette开源项目](https://github.com/andsel/moquette)

2. [MQTT协议V3.1.1版本](http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.pdf)

3. [MQTT协议V3.1版本](http://www.ibm.com/developerworks/webservices/library/ws-mqtt/ws-mqtt-pdf.pdf)

4. [Java AIO 基础](http://lxy2330.iteye.com/blog/1122849)

5. [聂永的博客](http://www.blogjava.net/yongboy/)

