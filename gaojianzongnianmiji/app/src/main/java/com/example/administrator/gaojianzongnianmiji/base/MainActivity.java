package com.example.administrator.gaojianzongnianmiji.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import cn.dlc.commonlibrary.okgo.rx.OkObserver;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.App;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.fragment.MainFragment;
import com.example.administrator.gaojianzongnianmiji.utils.RetryWithDelay;
import com.example.administrator.gaojianzongnianmiji.view.MacNoDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.logo_img)
    public ImageView mLogoImg;
    @BindView(R.id.time_tv)
    public TextView mTimeTv;
    @BindView(R.id.back_img)
    public ImageView mBackImg;
    @BindView(R.id.fl_content)
    FrameLayout mFlContent;
    @BindView(R.id.tv_company_name)
    public TextView mTvCompanyName;
    MacNoDialog mMacNoDialog;
    Handler mHandler = new Handler();
    @BindView(R.id.back_time)
    public TextView mBackTime;
    @BindView(R.id.ll_back)
    public LinearLayout mLlBack;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        //检查异常订单上报后台
        checkOrderState();
        replaceFragment(new MainFragment());
    }

    private void initData() {
        if (App.isFirstStart) {
            showNormalDialog();
        }
    }

    private void checkOrderState() {
        int out_num = PrefUtil.getDefault().getInt(Constant.Packet_Num,-1);
        if (out_num>=0){
            String order_number  = PrefUtil.getDefault().getString(Constant.Hitch_Order_Num,"");
            String weight = PrefUtil.getDefault().getString(Constant.Hitch_Weight,"5000");
            RiceMillHttp.get()
                .endCallBack("2", "1", "在碾米中断电，重启后上报异常订单", Constant.ANDROID_ID, order_number, weight,out_num)
                .retryWhen(new RetryWithDelay(20, 10, true))
                .doFinally(() -> {
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OkObserver<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        //updateDbOrder(order_number, 1, 0, 0, "碾米完成上报成功");
                        PrefUtil.getDefault().saveInt(Constant.Packet_Num,-1);
                    }

                    @Override
                    public void onFailure(String message, Throwable tr) {
                        //if (tr instanceof ApiException) {
                        //    updateDbOrder(order_number, 1, 0, 0, message);
                        //} else {
                        //    updateDbOrder(order_number, 2, 0, 0, message);
                        //}
                    }
                });
        }

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    public void showNormalDialog() {
        App.isFirstStart = false;
        mMacNoDialog = new MacNoDialog(this);
        mMacNoDialog.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.this.isFinishing()) {
                    return;
                }
                if (mMacNoDialog != null) {
                    mMacNoDialog.dismiss();
                }
            }
        }, 20000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
