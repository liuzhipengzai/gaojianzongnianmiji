package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by lixukang   on  2019/7/19.
 */

public class IntegralBean {

    /**
     * code : 1
     * msg : 积分规格
     * data : {"id":1,"xf_money":"1.00","xf_score":"1.00","dh_score":"1.00","dh_money":"1.00"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * id : 1
         * xf_money : 1.00
         * xf_score : 1.00
         * dh_score : 1.00
         * dh_money : 1.00
         */

        public int id;
        public String xf_money;
        public String xf_score;
        public String dh_score;
        public String dh_money;
    }
}
