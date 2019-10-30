package com.example.administrator.gaojianzongnianmiji.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import cn.dlc.commonlibrary.ui.base.BaseCommonActivity;
import cn.dlc.commonlibrary.ui.widget.TitleBar;

public abstract class BaseActivity extends BaseCommonActivity {

    //需要处理全屏逻辑之类的操作,在子类重写beforeSetContentView方法做处理
    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
    }

    //需要在每个Activity处理逻辑,直接在onCreate方法处理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //初始化TitleBar
    protected void initTitleBar(TitleBar mTitleBar) {
        if (mTitleBar != null) {
            mTitleBar.leftExit(this);
        }
    }

    // 打电话
    protected static void makePhone(String phoneNumber, Context context) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(uri);
        context.startActivity(intent);
    }

    //通过外部浏览器打开网页
    protected static void openWeb(String url, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public interface RefleshInf {
        void reflesh();

        void loadmore();
    }
}
