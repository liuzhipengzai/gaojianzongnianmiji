package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by lixukang   on  2019/7/19.
 */

public class PayOrderBean {
    /**
     * code : 1
     * msg : 买米支付
     * data : {"order_number":"58342019081214094800000003144","goods_num":1,"weight":"500.00","pay_price":"20.00","integral_price":"1.00"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * order_number : 58342019081214094800000003144
         * goods_num : 1
         * weight : 500.00
         * pay_price : 20.00
         * integral_price : 1.00
         */

        public String order_number;
        public int goods_num;
        public String weight;
        public String pay_price;
        public String integral_price;
    }

    ///**
    // * code : 1
    // * msg : 买米支付
    // * data : {"order_number":"156332962353632671","weight":"500","goods_num":"1","pay_price":10,"integral_price":0}
    // */
    //
    //public int code;
    //public String msg;
    //public DataBean data;
    //
    //public static class DataBean {
    //    /**
    //     * order_number : 156332962353632671
    //     * weight : 500
    //     * goods_num : 1
    //     * pay_price : 10
    //     * integral_price : 0
    //     */
    //
    //    public String order_number;
    //    public String weight;
    //    public String goods_num;
    //    public String pay_price;
    //    public int integral_price;
    //}


}
