# SOCKETTY

![Codacy Badge](https://api.codacy.com/project/badge/Grade/880ed281aff445f890766ccccbe81d7d)![License](https://img.shields.io/badge/License-Apache--2.0-green.svg)![API](https://img.shields.io/badge/API-29%2B-brightgreen.svg?style=flat)

**Android 通讯基础操作框架，包含Socket、LocalSocket和Ble蓝牙通讯，操作简单，扩展植入方便。支持Socket和LocalSocket，BLE的单连接和多连接，基于socketclient库可进行自由改造以实现具体业务需求，比如自定义解包封包策略，自动重连等。**

- **项目地址：** [Socketty](https://github.com/larsonzhong/Socketty)

- **项目依赖：** `无`

## 功能

- **支持蓝牙扫描；**
- **支持蓝牙连接；**
- **支持蓝牙多连接通讯；**
- **支持LocalSocket快速配置连接；**
- **支持LocalSocket通讯；**
- **支持Socket快速配置连接；**
- **支持Socket通讯；**
- **支持Socket多连接通讯；**
- **支持自定义拆解包协议；**
- **支持发送异步消息/等待消息/同步消息；**
- **支持自定义消息拦截规则；**
- **支持Socket多个通道连接多个服务器；**
- **支持断线重连及相关配置；**
- **支持心跳包和相关配置；**


## 简介

本项目包含一个基础库SocketClient和两个基于基础库的协议实现`libraryglonavin(蓝牙)`、`libraryfilecheck(Socket)`和对应的业务Demo`appfilecheck(App)`、`appglonavin(蓝牙)`，这两个库本属于项目中的业务实现；

SocketClient库是 BLE和Socket通讯的基础框架，只处理 BLE 和Socket通信逻辑，不包含具体的数据处理，如数据的分包与组包等。通讯的基础逻辑已经封装，用户只需要按照要求实现对应的接口类即可完成调用，不需要关心发送和接收，短包毡包之类的细节，也不需要关心底层的连接逻辑和上报逻辑，操作简单，扩展性强，接入方便。

## 版本说明

 ![LatestVersion](https://img.shields.io/badge/LatestVersion-0.1.1-orange.svg)

最新版本更新记录

- V0.1.1（2021-05-08）
  - 支持同步消息；
  - 支持断线重连；
  - 支持多连接管理。

## 代码托管

[![Github](https://img.shields.io/badge/JCenter-2.0.6-orange.svg)](https://github.com/larsonzhong/Socketty)

## 常见问题

[![FAQ](https://img.shields.io/badge/FAQ-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98-red.svg)](https://github.com/xiaoyaoyou1212/BLE/blob/master/FAQ.md)

## 使用介绍

### 权限配置

蓝牙操作针对 6.0 以下系统需要配置如下权限：

```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

而 6.0 以上系统还需要增加模糊定位权限：

```
<uses-permission-sdk-23 android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

如果使用Socket通讯则需要配置网络访问权限：

```
<uses-permission android:name="android.permission.INTERNET"/>
```

为了简便操作，库中对蓝牙和网络访问操作需要的权限都做了相关设置不需要重复设置，但 6.0 以上系统需要动态申请模糊定位权限。

### 引入 SDK

在工程 module 的 build.gradle 文件中的 dependencies 中添加如下依赖：

```
implementation project(path: ':socketclient')
```

构建完后就可以直接使用该库的功能了。

### 初始化

在使用该库前需要进行初始化，初始化代码如下所示：

```
this.socketClient = new SocketClient();
// 第二个参数是连接监听，可以从调用的地方传入，也可以在调用类中使用监听集合为上面的调用提供多个回调
this.socketClient.setup(context, new IStateListener() {
    @Override
    public void onConnected(Object device) {
        for (IConnectStateListener listener : connListeners) {
            boolean reconnect = device != null && ((Boolean) device
            listener.onConnect(host, reconnect);
        }
    }
    @Override
    public void onConnectFailed(String reason) {
        for (IConnectStateListener listener : connListeners) {
            listener.onConnectFailed(host, reason);
        }
    }
    @Override
    public void onDisconnect(Object device) {
        for (IConnectStateListener listener : connListeners) {
            listener.onDisconnect(host);
        }
    }
});
```

初始化可以是在 Application 中也可以是在 MainActivity 中，只需要是在使用蓝牙或者Socket功能前就行。还有需要注意的是，蓝牙配置必须在蓝牙初始化前进行修改，Socket则在连接之前修改，如果默认配置满足要求也可以不修改配置。

### 设备扫描

设备扫描是蓝牙通讯的专用功能，因此该功能在协议层代码而不是socketClient，在协议层代码调用只需要在获取实例后调用`scanDevice`：

- 扫描所有设备

```
managerCore.scanDevice(enable);
```

其中扫描到的设备通过注册的`IBleStateListener`回调给上层调用，而单个设备信息都统一放到`BluetoothDevice`中，其中包含了设备的所有信息，如设备名称、设备地址、广播包解析信息等。

### 设备连接

设备连接有三种方式，一种是根据设备信息直接进行连接，另外两种是在没扫描的情况下直接通过设备名称或设备 MAC 进行扫描连接。

Socket的连接方式有两种，LocalSocket和RemoteSocket（就是一般意义上的Socket，LocalSocket是和本地通讯的Socket，比如Java和C程序通讯），我们在构建ConnectOption（比如上面的`FileCheckConnectOption`）的时候，需要实现BaseSocketConnectOption，通过修改`getType`设置连接类型。

蓝牙连接方式去掉了2.0连接（太旧没有应用已从源码中去掉），目前只有BLE连接方式 ，三种连接方式定义如下；

1. SOCKET
2. LOCAL_SOCKET
3. BLE

- Socket连接

```
public void connect(String host, int port) {
    SocketConnectOption option = new SocketConnectOption.Builder()
            .setConnectTimeout(5000)            // 连接超时（毫秒）
            .setPulseFrequency(30 * 1000)       // 心跳频率（毫秒）
            .setReadTimeout(3000)               // 读取超时（毫秒）
            .setReconnectAllowed(true)          // 容许重连
            .setReconnectInterval(3000)         // 重连间隔
            .setReconnectMaxAttemptTimes(5)     // 重连重试次数
            .build();
    connectOption = new FileCheckConnectOption
            .Builder()
            .host(host)
            .port(port)
            .skSocketOption(option)
            .build();
    socketClient.connect(connectOption);
}
```

- Ble连接

```
void connect(BluetoothDevice bluetoothDevice) {
    GlonavinConnectOption option = new GlonavinConnectOption(bluetoothDevice);
    socketClient.connect(option);
}
```

- 连接状态

```
public boolean isConnected() {    return socketClient.isConnected();}
```

- 断开连接

```
public void disconnect() {    socketClient.disConnect();}
```

### 封解包策略

由于SocketClient知识做了业务能力支持并未实现具体的协议，我们在连接的时候需要一个实现连接配置，二实现连接配置需要我们对发送的包进行封装以及对接收的包进行解析，相关的封包解包策略则定义在实现的这个连接配置中：

#### 解包策略

```
 @Override public IPacketStrategy getPacketConstructor() {     return new PacketStrategy(); }
```

这里的 `PacketStrategy`表示包解析策略，也就是收到服务器的数据后如何将字节数据转换成程序能够处理的最基础的包数据，在实现这个类的时候需要将收到的字节数组转换成Packet对象。

#### 封包策略

任何包需要实现`IPacket`接口才能发送，也就必须实现它的三个方法：

```
public interface IPacket {	// 包长度    short getLength() ;	// 包主体数据    byte[] getData() ;	// 整个包转换成byte数据    byte[] getBytes() ;}
```

在`getBytes`中定义了如何将属性转换成字节数据的代码逻辑，需要根据实际协议来实现；

### 发送消息

有三种消息发送模式，分别是异步消息、同步消息、等待超时消息，分别代表的消息类型如下：

- 洪水消息(`AckMode.NON`)：调用了发送就不管了的消息，也不管消息是否发送成功；
- 异步消息(`AckMode.PACKET`)：发送之后不需要关心发送结果只需要发送出去就算成功,如果没有发送成功则按照配置的重发次数重发；
- 同步消息(`AckMode.MESSAGE`)：需要关心发送后服务端返回结果，并对返回结果进行判定，如果服务器返回消息中没有想要的消息，则提示发送超时，有责根据服务器中的返回结果进行相应业务处理；在发送消息前需要对消息添加消息拦截器`msgFilter`和`resultFilter`；
- 等待超时消息(`AckMode.WAIT_MESSAGE`)：就是不发消息，直接等服务器发送对应的消息，如果拦截到对应的消息则返回成功；

#### 构建消息包

socketclient在发送的时候支持消息包，消息包`IWrappedMessage`包含了消息`IMessage`的构建策略，包括消息模式，拦截器`msgFilter`，结果处理器`resultHandler`，实际上Message的封包策略也是在这里定义的，示例程序中使用构造这模式`Builder`，使用者可根据自己使用习惯修改，只要实现了`IWrappedMessage`正确构造消息包即可；

1. 构建异步消息

```
WrappedMessage message = new WrappedMessage        .Builder()        .xxx())						// 根据实际协议和需求实现        .ackMode(AckMode.PACKET)		// 消息模式        .msgFilter(cmd.getMessageFilter())		// 消息拦截器        .resultHandler(cmd.getResultHandler())	// 结果处理器        .build();
```

2. 构建同步消息

```
WrappedMessage message = new WrappedMessage        .Builder()        .xxx())						// 根据实际协议和需求实现        .ackMode(AckMode.MESSAGE)		// 消息模式        .msgFilter(cmd.getMessageFilter())		// 消息拦截器        .resultHandler(cmd.getResultHandler())	// 结果处理器        .build();
```

3. 构建洪水消息

```
WrappedMessage message = new WrappedMessage        .Builder()        .xxx())						// 根据实际协议和需求实现        .ackMode(AckMode.NON)		// 消息模式        .msgFilter(cmd.getMessageFilter())		// 消息拦截器        .resultHandler(cmd.getResultHandler())	// 结果处理器        .build();
```

4. 构建等待消息

```
WrappedMessage message = new WrappedMessage        .Builder()        .xxx())						// 根据实际协议和需求实现        .ackMode(AckMode.WAIT_MESSAGE)		// 消息模式        .msgFilter(cmd.getMessageFilter())		// 消息拦截器        .resultHandler(cmd.getResultHandler())	// 结果处理器        .build();
```

#### 发送消息

发送消息统一调用`sendMessage`方法：

```
boolean isSend = socketClient.sendMessage(message);
```

#### 处理服务器返回

服务端的返回结果回调在`resultHandler`中，用户可在构建这个拦截器的时候实现对应的业务；

### 构建示例消息

以登录消息构建为例，通常情况下，建议每一条接口协议的实现使用一个单独的类来封装，这样有利于代码解耦，登陆消息的一般流程是发送出去希望服务器返回登录信息比如session，客户端需要处理这个登录信息：

```
public class LoginCommand extends AbsCommand {    private static final String COMMAND_HEADER = "Login";    private LoginCallback loginCallback;    LoginCommand(Builder builder) {        super(builder.commandStr, builder.dataStr);        this.loginCallback = builder.loginCallback;    }    @Override    public MessageFilter getMessageFilter() {        return new MessageFilter() {            @Override            public boolean accept(IMessage msg) {                String command = readCommandHeader(msg);                return COMMAND_HEADER.equals(command);            }        };    }    @Override    public MessageFilter getResultHandler() {        return new MessageFilter() {            @Override            public boolean accept(IMessage msg) {                if (msg == null) {                    loginCallback.onLoginTimeout();                    return false;                }                String commandStr = ((Message) msg).getCommand();                String[] strings = commandStr.split("\r\n");                String session = strings[1].split("=")[1];                //AC/NAC#代表Request处理结果,Accept和Not accept                boolean logged = strings[2] != null && strings[2].split("=")[1].equals("AC");                String code = logged ? null : strings[3].split("=")[1];                boolean updateSoftware = strings[4] != null && strings[4].split("=")[1].equals("Y");                boolean updateConfig = strings[5] != null && strings[5].split("=")[1].equals("Y");                Session loginResult = new Session(logged, session, code, updateSoftware, updateConfig);                loginCallback.onLoginResponse(loginResult);                Log.i("LoginCommand", "login result :" + session);                return true;            }        };    }    public interface LoginCallback {        /**         * on server return login state         *         * @param loginResult logged Session         */        void onLoginResponse(Session loginResult);        /**         * return null when login timeout         */        void onLoginTimeout();    }    public static class Builder {        String command;        String imei;        String pass;        String sver;        String cver;        String commandStr;        String dataStr;        private LoginCallback loginCallback;        public Builder(LoginCallback loginCallback) {            this.command = COMMAND_HEADER;            this.loginCallback = loginCallback;        }        public Builder imei(String user) {            this.imei = user;            return this;        }        public Builder pass(String pass) {            this.pass = pass;            return this;        }        public Builder sver(String sver) {            this.sver = sver;            return this;        }        public Builder cver(String cver) {            this.cver = cver;            return this;        }        public LoginCommand build() {            commandStr = "Command=" + command + "\r\n" +                    "User=" + imei + "\r\n" +                    "Pass=" + pass + "\r\n" +                    "Sver=" + sver + "\r\n" +                    "Cver=" + cver + "\r\n";            dataStr = "";            return new LoginCommand(this);        }    }}
```

## 总结

从以上的描述中可以知道，设备相关的所有操作都统一交给 `socketChanel` 进行处理，并且该类是单例模式，全局只有一个，管理很方便。使用该库提供的功能前必须要调用 `mutiSocketor.setup(context);` 进行初始化。连接后需要登录，登陆后会开启心跳。同时用户可以根据业务需要发送对应的消息上报；在连接断开后，会收到断开的回调。`mutiSocketor`  中封装了几个常用的 API，如：连接和断开连接、登录和登出、初始化和销毁、数据上报等，该库提供的功能尽量简单易用，这也正是该项目的宗旨。

## 关于我

[![Website](https://img.shields.io/badge/Website-zhonglunshun-blue.svg)](https://blog.csdn.net/zhonglunshun)

[![CSDN](https://img.shields.io/badge/CSDN-zhonglunshun-blue.svg)](https://blog.csdn.net/zhonglunshun)

## 最后

改库不完善，肯定有bug，使用需谨慎，如果需要修改本库，请按照良好的编码习惯修改或者通知作者修改；



