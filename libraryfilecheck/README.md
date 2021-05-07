# FileChecker

![Codacy Badge](https://api.codacy.com/project/badge/Grade/880ed281aff445f890766ccccbe81d7d)![License](https://img.shields.io/badge/License-Apache--2.0-green.svg)![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)

**联通文件校验服务器客户端Socket通讯基础框架，基于Java Socket，功能简单。包含登录，登出，上报（文件开始/结束，业务数据开始、结束）等功能。为了保证阅读体验，请使用markdown编辑器打开。**

- **项目依赖：** `implementation project(path: ':socketclient')`

## 功能
- **支持登录远程服务器；**
- **支持登出远程服务器；**
- **支持设置Socket连接超时和读取超时；**
- **支持Socket连接超时提示；**
- **支持Socket连接断开提示；**
- **支持不断开连接登录登出；**
- **支持上报日志文件创建；**
- **支持上报日志文件切换；**
- **支持上报业务测试开始；**
- **支持上报业务测试结束；**
- **支持监听服务器发送给客户端的任意符合格式的数据；**
- **支持超时等待类消息；**
- **支持分包发送及配置分包大小；**
- **支持消息重发；**
- **支持配置发送带session心跳包；**
- **支持协议扩展；**
- **支持多连接通道，同时连接多个不同服务器并通讯**
- **支持连接动态创建和单一管理**
- **支持连接统一管理**

## 简介
打造该库的目的是为了简化SKP与后台服务器交互的流程。该库是 基于原生Socket的基础框架，包含文件上传协议文档规定的基本逻辑，也包含具体的数据处理逻辑，如数据的分包与组包等，因此改库不具备普适性。使用该库最大的好处是应用交互层只需要通过调取API就能够实现和文件服务器的交互而不需要过多关心里面的业务逻辑的封装；

由于业务需求，实现了多连接管理，但出于时间考虑，暂未实现缓存复用及相关对象优化，后续会以通道的方式实现多连接，

接口文档参考`文件联合校验平台接口规范.docx`和`联通文件校验需求文档20210415.rtf`

## 版本说明
 ![LatestVersion](https://img.shields.io/badge/LatestVersion-0.1.0-orange.svg)

最新版本更新记录
- V 0.1.0（2021-04-25）
    - 增加登出指令的处理；
    - 增加README.txt文件；
    - 修正测试Demo交互逻辑。
- V 0.1.1（2021-05-07）
    - 修复多连接有几率导致其中一个连接发送超时；
    - 增加多通道连接管理支持；

## 代码托管
 [![JCenter](https://img.shields.io/badge/Gerrit-2.0.6-orange.svg)](http://10.168.1.110:8088/#/dashboard/self)

## 常见问题
 ![FAQ](https://img.shields.io/badge/FAQ-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98-red.svg)

## 使用介绍

### 权限配置
访问网络需增加如下权限：
```
<uses-permission android:name="android.permission.INTERNET"/>
```
### 引入 SDK
在工程 module 的 build.gradle 文件中的 `dependencies` 中添加如下依赖：
```
implementation project(path: ':libraryfilecheck')
```
构建完后就可以直接使用该库的功能了。

### 初始化

- 初始化

在使用该库前需要进行初始化，初始化代码如下所示：
```
// 获取实例并传入Context执行setup
baseManager = ManagerCore.getInstance();
baseManager.setup(this);
```
初始化可以是在 `Application` 中也可以是在 `MainActivity`中，只需要是在连接服务器前就行。

- 反初始化

如果不需要使用了，则需要进行反初始化：

```
baseManager.onDestroy();
```

反初始化建议和初始化放在同一个类中调用，比如`onCreate`和`onDestroy`；

### 连接服务器
库中使用场景提供了丰富的连接配置，但是为了简化接入，使用者只需要传入host和port即可，其他配置均使用默认配置：这里需要注意的是，用户需要在连接前注册连接监听，否则无法正常处理连接结果；

支持修改连接配置，诸如连接超时，读取超时，心跳间隔，读写字节序，心跳频率。如果想要修改连接配置，需要修改库中`ManagerCore`的`connect`方法；

- 快速连接
```
String host = "10.168.1.70";
int port = 60000;
// 注册连接监听
baseManager.addConnectStateListener(this);
// 开始连接服务器
baseManager.connect(host, port);
```

- 修改连接配置
```
// 修改connect方法
public void connect(String host, int port) {
        SocketConnectOption option = new SocketConnectOption.Builder()
                .setConnectTimeout(1000)	// 连接超时（毫秒） 
                .setPulseFrequency(1)		// 心跳频率（毫秒） 
                .setReadByteOrder(ByteOrder.BIG_ENDIAN)		// 读取端序
                .setReadTimeout(1000)		// 读取超时（毫秒）
                .setReconnectAllowed(false)	// 容许重连
                .setReconnectInterval(1000)	// 重连间隔
                .setReconnectMaxAttemptTimes(1)		// 重连重试次数
                .setWriteByteOrder(ByteOrder.BIG_ENDIAN)	//写入端序
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

- 断开连接
```
baseManager.disconnect();
```

注意，以上提到的`connect`方法和`disconnect`方法都不能在主线程中调用，否则会卡`ANR`。

### 登录登出
连接成功后，可以调用`login`和`logout`方法实现登陆或者登出，登录和登出均为同步方法，需要在子线程中执行；在连接未断开情况下可以反复执行登录和登出；出于接入体验考虑，遇到连接断开不会直接抛出异常而是返回false，需要用户在连接监听中处理连接断开逻辑；

登陆后会将服务器返回的Session信息保存起来，可通过`getLoginResult`获取；

- 登录
```
// 登陆需要接入几个参数，result表示登陆结果
boolean result = baseManager.login(
                    "869107044188467",	// 设备Imei 
                    "Password",			// 登陆密码
                    "1.0",				// 软件版本
                    "2.0");				// 配置版本
```

- 登出
```
// 登出直接调用，result表示登出结果
boolean result = baseManager.logout();
```

- 获取Session
```
Session loginResult = ManagerCore.getInstance().getLoginResult();
// Session中最有用的是session，标识此次连接会话
```

### 连接详情
#### Session(连接信息)
- 获取登陆状态(logged):`session.isLogged()`；
- 获取会话标识(session):`session.getSession()`；
- 获取登陆码(code):`session.getCode()`；
- 获取是否需要更新软件(updateSoftware):`session.isUpdateSoftware()`；
- 获取是否需要更新配置(updateConfig):`session.isUpdateConfig()`；

#### 添加/移除监听
- 添加连接监听(IConnectStateListener):`managerCore.addConnectStateListener()`；
- 移除连接监听(IConnectStateListener):`managerCore.removeConnectListener()`；
- 移除消息监听(IMessageListener):`managerCore.addMessageListener(MessageIdFilter filter, IMessageListener listener)`；
- 移除消息监听(MessageFilter):`managerCore.removeMsgListener()`；

#### 连接状态

- 检测是否连接(boolean):`managerCore.isConnected()`；

### 上报数据
目前业务主要是上报三种形式的消息，分别是文件创建，任务开始，任务结束，文件结束，均是以command形式，为了方便扩展，还提供了发送自定义Command接口：

#### 发送文件创建消息

```
List<String> fileList = new ArrayList<>();
fileList.add("20210423.skz");
fileList.add("20210423.ipx");
fileList.add("20210423.cu");
DataUploadCommand command = new DataUploadCommand
        .Builder(DataUploadCommand.TAG_FILE_CREATE)
        .time(1616132316.5712156)
        .location(112.9362, 28.2259)
        .fileList(fileList)
        .build();
boolean result = baseManager.sendMessage(command, true);
```

#### 发送文件结束消息

```
List<String> fileList = new ArrayList<>();
fileList.add("20210423.skz");
fileList.add("20210423.ipx");
fileList.add("20210423.cu");
List<DataUploadCommand.FileInfo> infoList = new ArrayList
infoList.add(new DataUploadCommand.FileInfo("20210423.skz
        10054, "fsdajkfhjklsdhf"));
infoList.add(new DataUploadCommand.FileInfo("20210423.ipx
        10054, "fsdajkfhjklsdhf"));
infoList.add(new DataUploadCommand.FileInfo("20210423.cu"
        10054, "fsdajkfhjklsdhf"));
DataUploadCommand command = new DataUploadCommand
        .Builder(DataUploadCommand.TAG_FILE_SWITCH)
        .time(1616132316.5712156)
        .location(112.9362, 28.2259, 112.9362, 28.2259)
        .fileList(fileList)
        .fileInfo(infoList)
        .build();
boolean result = baseManager.sendMessage(command, true);
```
#### 上报业务开始

```
List<String> fileList = new ArrayList<>();
fileList.add("20210423.skz");
fileList.add("20210423.ipx");
fileList.add("20210423.cu");
DataUploadCommand command = new DataUploadCommand
        .Builder(DataUploadCommand.TAG_MO_START)
        .time(1616132316.5712156)
        .location(112.9362, 28.2259)
        .fileList(fileList)
        .build();
boolean result = baseManager.sendMessage(command, true);
showToast("startReportMoStart ,result=" + result);
```

#### 上报业务结束

```
List<String> fileList = new ArrayList<>();
fileList.add("20210423.skz");
fileList.add("20210423.ipx");
fileList.add("20210423.cu");
DataUploadCommand command = new DataUploadCommand
        .Builder(DataUploadCommand.TAG_MO_END)
        .time(1616132316.5712156)
        .location(112.9362, 28.2259)
        .fileList(fileList)
        .build();
boolean result = baseManager.sendMessage(command, true);
showToast("startReportMoEnd ,result=" + result);
```

根据业务场景，通常情况下`fileList`中需要添加skz，ipx，cu文件。然后构建一条`DataUploadCommand`命令，在`Builder`中需要指令上报业务类型、时间、位置。而在`fileInfoList`中还需要把上述文件的md5信息带上；关于上报业务类型的枚举，可以参考`DataUploadCommand`类，也可以在该类中自行扩展；

### 发送扩展命令
早期版本内置了上报命令，后续可能遇到需要增加上报命令需求，可以在库中通过新增类继承`AbsCommand`实现，只要组装的消息合格均可正常发送，以心跳包为例：

```
public class HeartCommand extends AbsCommand {
	// 不同消息的 COMMAND 可能不同
    private static final String COMMAND = "Heartbeat";

    HeartCommand(Builder builder) {
    	// 需要填充父类的cmd和data字段，均为String类型
        super(builder.commandStr, builder.dataStr);
    }

    @Override
    public MessageFilter getMessageFilter() {
    	// 发送后是否需要等待服务器回应，不需要则为null
        return null;
    }

    @Override
    public MessageFilter getResultHandler() {
        // 发送后是否需要对服务器的回应做处理，不需要为null
        return null;
    }


    public static class Builder {
        Object filed;

        String commandStr;
        String dataStr;

        public Builder filed(Object filed) {
            this.filed = filed;
            return this;
        }

        public HeartCommand build() {
        	// 按照协议文档要求组装command和data字段
            commandStr = "Command=" + COMMAND + "\r\n" +
                    "filed=" + filed + "\r\n";
            dataStr = "";
            return new HeartCommand(this);
        }

    }

}
```



## 总结
从以上的描述中可以知道，设备相关的所有操作都统一交给 `ManagerCore` 进行处理，并且该类是单例模式，全局只有一个，管理很方便。使用该库提供的功能前必须要调用 `ManagerCore.getInstance().setup(context);` 进行初始化。连接后需要登录，登陆后会开启心跳。同时用户可以根据业务需要发送对应的消息上报；在连接断开后，会收到断开的回调。`ManagerCore`  中封装了几个常用的 API，如：连接和断开连接、登录和登出、初始化和销毁、数据上报等，该库提供的功能尽量简单易用，这也正是该项目的宗旨。

## 关于我
[![Website](https://img.shields.io/badge/Website-zhonglunshun-blue.svg)](https://blog.csdn.net/zhonglunshun)

[![CSDN](https://img.shields.io/badge/CSDN-zhonglunshun-blue.svg)](https://blog.csdn.net/zhonglunshun)

## 最后
改库不完善，肯定有bug，使用需谨慎，如果需要修改本库，请按照良好的编码习惯修改或者通知作者修改；



