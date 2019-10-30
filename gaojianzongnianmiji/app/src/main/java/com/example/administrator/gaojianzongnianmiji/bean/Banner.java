package com.example.administrator.gaojianzongnianmiji.bean;

import java.util.List;

/**
 * Created by lixukang   on  2019/7/18.
 */

public class Banner {

    /**
     * code : 1
     * msg : 广告图
     * data : {"img":[{"banner_id":20,"title":"ceshi","image":"http://sdjsnmj.app.xiaozhuschool.com/public/public/uploads/imgs/20181101/2ce2f08a947731697b556fafa3f93224.jpg"}],"voide":[{"banner_id":3,"title":"ceshi","image":"http://sdjsnmj.app.xiaozhuschool.com/public/uploads/imgs/20181011/b9bc8c3c6f3d2e004ea82ade5ece4a1d.mp4"}]}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        public List<ImgBean> img;
        public List<VoideBean> voide;

        public static class ImgBean {
            /**
             * banner_id : 20
             * title : ceshi
             * image : http://sdjsnmj.app.xiaozhuschool.com/public/public/uploads/imgs/20181101/2ce2f08a947731697b556fafa3f93224.jpg
             */

            public int banner_id;
            public String title;
            public String image;
        }

        public static class VoideBean {
            /**
             * banner_id : 3
             * title : ceshi
             * image : http://sdjsnmj.app.xiaozhuschool.com/public/uploads/imgs/20181011/b9bc8c3c6f3d2e004ea82ade5ece4a1d.mp4
             */

            public int banner_id;
            public String title;
            public String image;
        }
    }
}
