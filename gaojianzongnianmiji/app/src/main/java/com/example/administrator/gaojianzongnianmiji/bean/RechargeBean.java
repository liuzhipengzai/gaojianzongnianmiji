package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by lixukang   on  2019/7/18.
 */

public class RechargeBean {

    /**
     * code : 1
     * msg : 充值支付
     * data : {"order_number":"155859996538549265","money":"100.00"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * order_number : 155859996538549265
         * money : 100.00
         */

        public String order_number;
        public String money;
    }
}
