package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by liuwenzhuo on 2019/8/9.
 */

public class CommitBean {

    /**
     * code : 1
     * msg : 支付参数
     * data : {"url":"https://qr.95516.com/48020000/58341908022732143131399077"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * url : https://qr.95516.com/48020000/58341908022732143131399077
         */

        public String url;
    }
}
