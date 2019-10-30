package com.example.administrator.gaojianzongnianmiji;

/**
 * Created by Administrator on 2019/5/23.
 */

public interface BaseUrl {
    String URL = "http://scgjznmj.app.xiaozhuschool.com";

    //apk应答碾米消息
    String ORDEROPERATIONACK = URL + "/apk/order/orderOperationAck";

    String STARTCALLBACK = URL + "/apk/order/startCallBackNew";

    String ENDCALLBACK = URL + "/apk/order/endCallBack";

    String HEARTBEATCALLBACK = URL + "/apk/order/heartbeatCallBack";
}
