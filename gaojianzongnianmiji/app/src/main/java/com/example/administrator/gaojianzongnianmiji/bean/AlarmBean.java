package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by liuzhipeng on 2019/9/11.
 */

public class AlarmBean {

    /**
     * code : 1
     * msg : 处理成功
     * data : {"device_id":102,"overheat":50,"warm_overtime":30,"rice_overtime":0,"down_overtime":11,"lamo_overtime":0,"fengmo_overtime":0,"give_rice_overtime":88,"give_mo_overtime":20,"macno":"6a0c2362ab35fd7b"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * device_id : 102
         * overheat : 50
         * warm_overtime : 30
         * rice_overtime : 0
         * down_overtime : 11
         * lamo_overtime : 0
         * fengmo_overtime : 0
         * give_rice_overtime : 88
         * give_mo_overtime : 20
         * macno : 6a0c2362ab35fd7b
         */

        public int device_id;
        public int overheat;
        public int warm_overtime;
        public int down_overtime;
        public int lamo_overtime;
        public int fengmo_overtime;
        public int give_rice_overtime;
        public int give_mo_overtime;
        public String macno;
    }
}
