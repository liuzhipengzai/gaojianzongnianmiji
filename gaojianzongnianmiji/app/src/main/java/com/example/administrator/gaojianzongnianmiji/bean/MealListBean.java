package com.example.administrator.gaojianzongnianmiji.bean;

import java.util.List;

/**
 * Created by lixukang   on  2019/7/18.
 */

public class MealListBean {

    /**
     * code : 1
     * msg : 充值套餐列表
     * data : [{"id":14,"name":"111","price":222,"give_money":"0.00"},{"id":13,"name":"套餐四","price":500,"give_money":"0.00"},{"id":12,"name":"套餐三","price":300,"give_money":"0.00"},{"id":11,"name":"套餐二","price":200,"give_money":"0.00"},{"id":10,"name":"套餐一","price":100,"give_money":"10.00"},{"id":7,"name":"测试套餐","price":500,"give_money":"0.00"},{"id":6,"name":"大自然农场","price":100,"give_money":"0.00"},{"id":5,"name":"250","price":250,"give_money":"0.00"},{"id":3,"name":"半月套餐","price":200,"give_money":"0.00"}]
     */

    public int code;
    public String msg;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 14
         * name : 111
         * price : 222
         * give_money : 0.00
         */

        public int id;
        public String name;
        public String price;
        public String give_money;
    }
}
