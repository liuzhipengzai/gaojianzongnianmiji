package com.example.administrator.gaojianzongnianmiji.bean;

import java.io.Serializable;

/**
 * Created by lixukang   on  2019/7/18.
 */

public class LoginBean {

    /**
     * code : 1
     * msg : 登陆成功
     * data : {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjMxLCJleHAiOjE1NjM4NjIwNTl9.EdbYiuG8PSM59hk7NxCf0KtJTajfi4fIohSMKCZn0O4","mobile":"131****1832","money":"0.00"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean implements Serializable {
        /**
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjMxLCJleHAiOjE1NjM4NjIwNTl9.EdbYiuG8PSM59hk7NxCf0KtJTajfi4fIohSMKCZn0O4
         * mobile : 131****1832
         * money : 0.00
         */

        public String token;
        public String mobile;
        public String money;
        public String idcard;
        public String integral;
    }
}
