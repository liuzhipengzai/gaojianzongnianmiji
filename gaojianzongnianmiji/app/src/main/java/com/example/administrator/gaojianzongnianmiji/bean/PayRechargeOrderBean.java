package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by liuwenzhuo on 2019/8/6.
 */

public class PayRechargeOrderBean {

    /**
     * code : 1
     * msg : 支付参数
     * data : {"url":"https://qr.95516.com/48020000/58341908018653920137964974"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * url : https://qr.95516.com/48020000/58341908018653920137964974
         */

        public String url;
    }
}
