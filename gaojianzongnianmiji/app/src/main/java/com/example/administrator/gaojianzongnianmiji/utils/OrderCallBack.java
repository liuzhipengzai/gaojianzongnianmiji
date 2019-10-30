package com.example.administrator.gaojianzongnianmiji.utils;

import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationBean;

public interface OrderCallBack {
    /**
     * @param bean
     * @param msg
     * @param state 1收到推送 2开启成功 3完成碾米 4其他错误 5碾米过程
     */
    int RECEIVED = 0x01;
    int OPEN_SUCCESS = 0x02;
    int FINISH_RICE = 0x03;
    int ERROR = 0x04;
    int RICEING = 0x05;

    void outRiceResult(OrderOperationBean bean, String msg, int state);

}
