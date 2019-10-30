package com.example.administrator.gaojianzongnianmiji.bean;

/**
 * Created by liuwenzhuo on 2019/8/6.
 */

public class DeviceInfoBean {

    /**
     * code : 1
     * msg : 设备信息
     * data : {"title":"2018081000030001","macno":"2018081000030001","business_start":"00:00","business_end":"00:00","qrcode":"http://scgjznmj.app.xiaozhuschool.com/h5/dist/index.html#/login?macno=2018081000030001","device_set":{"id":9,"speed":11,"overtime":22,"colse_delay_time":33,"stop_delay_time":44,"device_id":84}}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * title : 2018081000030001
         * macno : 2018081000030001
         * business_start : 00:00
         * business_end : 00:00
         * qrcode : http://scgjznmj.app.xiaozhuschool.com/h5/dist/index.html#/login?macno=2018081000030001
         * device_set : {"id":9,"speed":11,"overtime":22,"colse_delay_time":33,"stop_delay_time":44,"device_id":84}
         */

        public String title;
        public String macno;
        public String business_start;
        public String business_end;
        public String qrcode;
        public DeviceSetBean device_set;
        public String warehouse;//稻谷
        public String bag;//袋子

        public static class DeviceSetBean {
            /**
             * id : 9
             * speed : 11
             * overtime : 22
             * colse_delay_time : 33
             * stop_delay_time : 44
             * device_id : 84
             */

            public int id;
            public int speed;
            public int overtime;
            public int colse_delay_time;
            public int stop_delay_time;
            public int device_id;
        }
    }
}
