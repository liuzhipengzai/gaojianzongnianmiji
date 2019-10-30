package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by liuzhipeng on 2019/8/20.
 */

public class UpdaePrecisionBean {

    /**
     * code : 1
     * msg : 处理成功
     * data : {"api_name":"updaePrecision","macno":"132123","precision1":"1","precision2":"1","precision3":"1"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * api_name : updaePrecision
         * macno : 132123
         * precision1 : 1
         * precision2 : 1
         * precision3 : 1
         */

        public String api_name;
        public String macno;
        public String precision1;
        public String precision2;
        public String precision3;
    }
}
