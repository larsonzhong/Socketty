package com.skyruler.filechecklibrary.connection;

import android.content.Context;
import android.util.Log;

import com.skyruler.filechecklibrary.command.AbsCommand;
import com.skyruler.filechecklibrary.command.HeartCommand;
import com.skyruler.filechecklibrary.command.LoginCommand;
import com.skyruler.filechecklibrary.command.LogoutCommand;
import com.skyruler.filechecklibrary.command.result.Session;
import com.skyruler.filechecklibrary.message.WrappedMessage;
import com.skyruler.socketclient.ISocketClient;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.MessageSnBuilder;

import java.util.concurrent.CopyOnWriteArrayList;

public class SocketChanel {
    private static final String TAG = "ManagerCore";
    private final String host;
    private ISocketClient socketClient;
    private FileCheckConnectOption connectOption;
    private Session loginResult;

    private CopyOnWriteArrayList<IConnectStateListener> connListeners;


    SocketChanel(Context context, String hostName) {
        this.host = hostName;
        this.socketClient = new SocketClient();
        this.socketClient.setup(context, new IStateListener() {

            @Override
            public void onConnected(Object device) {
                for (IConnectStateListener listener : connListeners) {
                    boolean reconnect = device != null && ((Boolean) device);
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
    }

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

    public void addConnectStateListener(IConnectStateListener listener) {
        if (connListeners == null) {
            connListeners = new CopyOnWriteArrayList<>();
        }
        if (listener != null && !connListeners.contains(listener)) {
            connListeners.add(listener);
        }
    }

    void removeConnectListener(IConnectStateListener listener) {
        if (connListeners != null) {
            connListeners.remove(listener);
        }
    }

    void addMessageListener(MessageIdFilter filter, IMessageListener listener) {
        this.socketClient.addMessageListener(listener, filter);
    }

    void removeMsgListener(MessageFilter filter) {
        this.socketClient.removeMessageListener(filter);
    }

    public boolean isConnected() {
        return socketClient.isConnected();
    }

    public void disconnect() {
        socketClient.disConnect();
    }

    public void onDestroy() {
        socketClient.onDestroy();
        socketClient = null;
    }

    /**
     * Log in to the remote server and process the log-in result information returned by the server
     * 登录需要保存SessionID
     *
     * @return 消息是否发送
     */
    public boolean login(String imei, String password, String softVersion, String configVersion) {
        LoginCommand.LoginCallback loginCallback = new LoginCommand.LoginCallback() {

            @Override
            public void onLoginResponse(Session session) {
                loginResult = session;
                MessageSnBuilder.getInstance(session.getSession()).resetSn();
                updateHeartbeat(session.getSession());
                for (IConnectStateListener listener : connListeners) {
                    listener.onLogged(host, loginResult);
                }
            }

            @Override
            public void onLoginTimeout() {
                for (IConnectStateListener listener : connListeners) {
                    listener.onLoginTimeout(host);
                }
            }
        };

        LoginCommand command = new LoginCommand.Builder(loginCallback)
                .imei(imei)
                .pass(password)
                .sver(softVersion)
                .cver(configVersion)
                .build();
        boolean send = this.sendMessage(command, true);
        Log.i(TAG, "larson:login,send = " + send);
        return send;
    }

    public Session getLoginResult() {
        return loginResult;
    }

    public boolean logout() {
        if (loginResult == null) {
            Log.e(TAG, "larson:没有登陆信息");
            return false;
        }

        LogoutCommand command = new LogoutCommand.Builder()
                .session(loginResult.getSession())
                .build();
        boolean send = this.sendMessage(command, true);

        // 重新session和心跳
        updateHeartbeat(null);
        loginResult = null;
        // 目前不管服务器返回什么结果，只要是用户调用了登出就表示登出成功
        for (IConnectStateListener listener : connListeners) {
            listener.onLogout(host);
        }
        Log.i(TAG, "larson:logout,send = " + send);
        return send;
    }

    /**
     * 由于上报心跳需要先登录拿到session，所以拿到session需要更新发送的心跳包
     *
     * @param session session
     */
    private void updateHeartbeat(String session) {
        // 如果是登出则需要停止心跳上报
        if (session == null) {
            connectOption.updateHeartBeat(null);
            return;
        }

        // 登陆成功，开始心跳上报
        HeartCommand heartCommand = new HeartCommand.Builder()
                .session(session)
                .build();

        IMessage heartBeat = new WrappedMessage
                .Builder()
                .command(heartCommand.getCommand())
                .ackMode(AckMode.NON)
                .data(heartCommand.getData())
                .build().getMessages().get(0);
        connectOption.updateHeartBeat(heartBeat);
    }

    public boolean sendMessage(AbsCommand cmd, boolean hasReply) {
        try {
            WrappedMessage message = new WrappedMessage
                    .Builder()
                    .command(cmd.getCommand())
                    .data(cmd.getData())
                    .ackMode(hasReply ? AckMode.MESSAGE : AckMode.NON)
                    .msgFilter(cmd.getMessageFilter())
                    .resultHandler(cmd.getResultHandler())
                    .build();
            boolean isSend = socketClient.sendMessage(message);
            Log.d(TAG, "sendMessage state=" + isSend);
            return isSend;
        } catch (ConnectionException | UnFormatMessageException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        }
    }
}
