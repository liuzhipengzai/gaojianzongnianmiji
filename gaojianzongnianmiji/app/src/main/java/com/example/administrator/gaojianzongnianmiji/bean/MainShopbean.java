package com.example.administrator.gaojianzongnianmiji.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class MainShopbean  {

    /**
     * code : 1
     * msg : 大米类型
     * data : [{"title":"有道稻寒地富硒稻花香大米","img":"","price":"20.00","level":"7","name":"胚芽","goodsId":2}]
     */

    public int code;
    public String msg;
    public List<DataBean> data;

    public static class DataBean  implements Serializable{
        /**
         * title : 有道稻寒地富硒稻花香大米
         * img : 
         * price : 20.00
         * level : 7
         * name : 胚芽
         * goodsId : 2
         */

        public String title;
        public String img;
        public String price;
        public String level;
        public String name;
        public String goodsId;
    }
}
