package com.example.administrator.gaojianzongnianmiji.utils;

import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseBean;

/**
 * Created by liuwenzhuo on 2019/7/10.
 */

public class riZhiUtil {

    public static void setrizhi(String msg){
        RiceMillHttp.get().rizhi(msg, new Bean01Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean baseBean) {

            }

            @Override
            public void onFailure(String message, Throwable tr) {

            }
        });
    }

}
