package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by liuwenzhuo on 2019/8/7.
 */

public class OperationBean {

    /**
     * code : 1
     * msg : 登陆成功
     * data : {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjUsImV4cCI6MTU2NTc2MTMyN30.uvnCSsUpzWNJfjNTDUanrfZmFI4GmzvofWlj1si1OUs"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjUsImV4cCI6MTU2NTc2MTMyN30.uvnCSsUpzWNJfjNTDUanrfZmFI4GmzvofWlj1si1OUs
         */

        public String token;
    }
}
