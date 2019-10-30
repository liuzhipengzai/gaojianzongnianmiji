package com.example.administrator.gaojianzongnianmiji.utils;

import android.content.Context;
import com.licheedev.myutils.LogPlus;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Administrator on 2019/3/19/019.
 */

public class MqttManager {
    private final String serverUri = "tcp://120.77.72.190:1883";
    private final String userName = "dlc";
    private final String passWord = "123456";
    private volatile MqttAndroidClient mqttAndroidClient;
    private static volatile MqttManager mqttManager = null;
    private String[] oldMqttTopic = new String[] {
        "scgjznmj/@/health"
    };
    private String[] mqttTopic;
    private int[] qos = new int[] { 0 };
    private MqttCallback callback;

    public static MqttManager getInstance() {
        if (mqttManager == null) {
            synchronized (MqttManager.class) {
                if (mqttManager == null) {
                    mqttManager = new MqttManager();
                }
            }
        }
        return mqttManager;
    }

    public void setCallback(MqttCallback callback) {
        this.callback = callback;
    }

    public boolean isConnect() {
        if (mqttAndroidClient != null) {
            return mqttAndroidClient.isConnected();
        }
        return false;
    }

    public void initConnect(Context context, String macNo) {
        if (isConnect()) {
            return;
        }
        mqttTopic = new String[oldMqttTopic.length];
        for (int i = 0; i < oldMqttTopic.length; i++) {
            mqttTopic[i] = oldMqttTopic[i].replace("@", macNo);
        }
        Context mContext = context.getApplicationContext();
        if (mqttAndroidClient == null) {
            synchronized (MqttAndroidClient.class) {
                if (mqttAndroidClient == null) {
                    mqttAndroidClient =
                        new MqttAndroidClient(mContext, serverUri, MqttClient.generateClientId());
                }
            }
        }
        mqttAndroidClient.registerResources(mContext);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            /**
             * 连接完成回调
             * @param reconnect true 断开重连,false 首次连接
             * @param serverURI 服务器URI
             */
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    subscribeToTopic(mContext, reconnect);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                if (callback != null) {
                    callback.connectLost(cause.toString());
                }
                cause.printStackTrace();
            }

            /**
             * 消息接收，如果在订阅的时候没有设置IMqttMessageListener，那么收到消息则会在这里回调。
             * 如果设置了IMqttMessageListener，则消息回调在IMqttMessageListener中
             * @param topic
             * @param message
             */
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if (callback != null) {
                    callback.receiveMessage(message.toString());
                }
            }

            /**
             * 交付完成回调。在publish消息的时候会收到此回调.
             * qos:
             * 0 发送完则回调
             * 1 或 2 会在对方收到时候回调
             * @param token
             */
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                LogPlus.e(token.toString());
            }
        });
        //mqtt连接参数设置
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        //设置自动重连
        mqttConnectOptions.setAutomaticReconnect(true);
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        mqttConnectOptions.setCleanSession(false);
        //设置连接的用户名
        mqttConnectOptions.setUserName(userName);
        //设置连接的密码
        mqttConnectOptions.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒
        mqttConnectOptions.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        mqttConnectOptions.setKeepAliveInterval(20);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, mContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    /*连接成功之后设置连接断开的缓冲配置*/
                    DisconnectedBufferOptions disconnectedBufferOptions =
                        new DisconnectedBufferOptions();
                    //开启
                    disconnectedBufferOptions.setBufferEnabled(false);
                    //离线后最多缓存1条
                    disconnectedBufferOptions.setBufferSize(1);
                    //不一直持续留存
                    disconnectedBufferOptions.setPersistBuffer(true);
                    //删除旧消息
                    disconnectedBufferOptions.setDeleteOldestMessages(true);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    //订阅主题
                    subscribeToTopic(mContext, false);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                    if (!(exception.getCause() instanceof MqttException) && callback != null) {
                        callback.connectFail(exception.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.connectFail(e.toString());
            }
        }
    }

    private void subscribeToTopic(Context context, boolean reconnect) {
        try {
            mqttAndroidClient.subscribe(mqttTopic, qos, context.getApplicationContext(),
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (callback != null) {
                            callback.subscribedSuccess(reconnect);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        LogPlus.e("订阅失败");
                        if (callback != null) {
                            callback.connectFail(exception.toString());
                        }
                    }
                });
        } catch (MqttException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.connectFail(e.toString());
            }
        }
    }

    public void publishMessage(String topic, String bean) {
        if (isConnect()) {
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(bean.getBytes());
                mqttAndroidClient.publish(topic, message);
                LogPlus.e("Mqtt 发送消息：" + bean);
                if (!mqttAndroidClient.isConnected()) {
                    LogPlus.e(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
                }
            } catch (MqttException e) {
                LogPlus.e("Error Publishing: " + e.toString());
            }
        }
    }

    public void onDestroy() {
        if (mqttAndroidClient == null) {
            return;
        }
        try {
            mqttAndroidClient.close();
            mqttAndroidClient.disconnect();
            mqttManager = null;
            mqttAndroidClient = null;
            callback = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface MqttCallback {
        /**
         * true 断开重连,false 首次连接
         *
         * @param reconnect
         */
        void subscribedSuccess(boolean reconnect);

        void receiveMessage(String message);

        void connectFail(String message);

        void connectLost(String message);
    }
}
