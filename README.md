## 目录
* ####[说明](#1)
* ####[功能](#2)
* ####[如何使用](#3)
* ####[参考帮助](#4)

## <a name="1">说明</a>
**重要的放前面：V1.0版本是一个非常基础的版本，除了完整的MQTT协议实现外，其他功能什么都没做。**

MQTT 协议是 IBM 开发的即时通讯协议，相对于 IM 的实际上的准标准协议 XMPP 来说，MQTT 更小，更快，更轻量。MQTT 适合于任何计算能力有限，工作在低带宽、不可靠的网络中的设备，包括手机，传感器等等。

开发此客户端的目的，是因为实际项目中需要用到推送、即时通讯的内容，而第三方平台有时候不稳定，遇到问题难于调试。所以决定自己开发一套即时通讯系统。选用 MQTT 的原因正如上所说，它比XMPP更适合手机端使用。具体比较请参看：[Android推送方案分析（MQTT/XMPP/GCM）](http://m.oschina.net/blog/82059)。

项目断断续续写了快一年（2015年2月~12月），大部分在业余时间完成。代码中的每个函数都有明确的中文注释信息，对于 MQTT 实现的部分，更是细节到每个功能对应的文档的页数都进行了标明。此服务器既可以针对具体项目二次开发使用，也可以用于 MQTT 协议的学习。

## <a name="2">功能</a>
已实现：
* 网络传输功能（使用 Java7 才开始支持的 AIO 实现）~~
* 会话管理功能
* 任务调度框架（使用Quartz框架为基础封装）
* 协议层与网络层的分离（即换套协议，网络层一点不用改，也能用）
* MQTT完整实现（推送，单聊，群聊）


未实现：
* ~~安全层（消息加密、解密、防重放，防中间人等等）~~
* ~~好友功能（添加好友，删除好友，好友列表等等）~~
* ~~群组管理功能（添加群组，退出群组等等）~~
* ~~语音~~
* ~~视频~~
* ~~总之就是可以拓展的应用层都没做~~

## <a name="3">如何使用</a>
####zer0MqttServer 的使用很简单：
* 下载源码（源码中包括所有依赖包）
* 检查 Java 版本是否为1.7或以上，不是则按照 Java 1.7及以上
* 导入IDE
* 引用依赖包
* 运行包 com.syxy.server 下的 StartServer 文件，即可启动服务器。

<img width="300px" src="http://images2015.cnblogs.com/blog/646489/201512/646489-20151208221520277-1992198031.png">

####测试方法也同样简单：

运行包 test 下的 MQTTClientTest 文件，即可开启测试客户端。

测试客户端包括的功能有：连接服务器、订阅主题、发送固定信息，客户端通过 pahoMqtt 第三方jar包编写，你可以自行修改代码进行更详细的测试

####下面简述一下项目的目录结构：

<img width="300px" src="http://images2015.cnblogs.com/blog/646489/201512/646489-20151209152138449-1840437421.png">

com.syxy.util 包中是一些公共类，包括缓冲池BufferPool、任务调度框架QuartzManager、字符串处理类StringTool、日期时间类TimeUtils等等

com.syxy.Aiohandler 是 AIO 的具体实现，包含了 IO 建立、数据接收、数据回写3个类。

com.syxy.server 是网络应用层，StartServer 用于启动服务器，并初始化协议相关的类。TcpServer 初始化了服务器的初始资源，包括缓冲区大小，协议处理器，端口配置，AIO 等等。ClientSession 是会话管理类，每个客户端的连接对应一个此类对象，包含心跳处理，会话断开，数据接收，处理，回写等等内容。

com.syxy.protocol 定义了协议处理接口

com.syxy.protocol.mqttImp 则是具体的 MQTT 协议的处理。包括协议的编码，解码，业务处理等等。其中的 message 包中处理了14种 MQTT 对应的消息类型（编码、解码）。process 包中进行了协议的具体处理。最重要的是```ProtocolProcess.java```文件，其中完整实现了MQTT协议文件中的具体流程。

resource 文件中包含了一些配置文件，其中 mqtt.properties 文件可以针对缓冲区大小、临时存储文件名、服务器端口等信息

## <a name="4">参考帮助</a>

1. [moquette开源项目](https://github.com/andsel/moquette)

2. [MQTT协议V3.1.1版本](http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.pdf)

3. [MQTT协议V3.1版本](http://www.ibm.com/developerworks/webservices/library/ws-mqtt/ws-mqtt-pdf.pdf)

4. [Java AIO 基础](http://lxy2330.iteye.com/blog/1122849)

5. [聂永的博客](http://www.blogjava.net/yongboy/)

## <a name="5">源码</a>
最后附上源码地址： [https://github.com/zer0Black/zer0MQTTServer](https://github.com/zer0Black/zer0MQTTServer)

在github中选择分支 release-V1.0 即可。主干会持续开发，不能保证可以跑通。
