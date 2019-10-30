package com.example.administrator.gaojianzongnianmiji.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.BindView;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import com.dlc.bannerplayer.PlayerBanner;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.bean.Banner;
import com.example.administrator.gaojianzongnianmiji.bean.LatelyOrderBean;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class MainFragment extends BaseFragment {
    @BindView(R.id.fl_content)
    FrameLayout mFlContent;
    @BindView(R.id.ViewFlipper)
    ViewFlipper vp;
    @BindView(R.id.banner)
    PlayerBanner mBanner;
    private ScheduledThreadPoolExecutor scheduled;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //在主线程里面处理消息并更新UI界面
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();//获取系统时间
                    CharSequence sysTimeStr =
                        DateFormat.format("yyyy-MM-dd   HH:mm:ss", sysTime);//时间显示格式
                    if (mActivity.mTimeTv!=null){
                        mActivity.mTimeTv.setText(sysTimeStr); //更新时间
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.mBackImg.setVisibility(View.INVISIBLE);
        mActivity.mLlBack.setVisibility(View.GONE);
        new TimeThread().start();
        initFragment(new MainShopFragment());
        initViewFlipper();
        initBanner();
        MQTTBusiness.getInstance(mActivity).setRenewCallBack(new MQTTBusiness.RenewCallBack() {
            @Override
            public void renew() {
                initBanner();
            }
        });

    }

    private void initBanner() {

        RiceMillHttp.get()
            .getBanner()
            .subscribeOn(Schedulers.io())
            .retry(new BiPredicate<Integer, Throwable>() {
                @Override
                public boolean test(Integer integer, Throwable throwable) throws Exception {
                    SystemClock.sleep(5000);
                    return true;
                }
            })
            .timeout(10 * 60, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Banner>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Banner banner) {
                    try {
                        Banner.DataBean data = banner.data;
                        ArrayList<String> strings = new ArrayList<>();
                        for (int i = 0; i <data.img.size() ; i++) {
                            strings.add(data.img.get(i).image);
                        }
                        for (int i = 0; i < data.voide.size(); i++) {
                            strings.add(data.voide.get(i).image);

                        }
                        mBanner.setBannerDataWithUrls(strings);
                        // 准备
                        mBanner.prepare();
                    }catch (Exception e){
                    }
                }

                @Override
                public void onError(Throwable e) {
                    showOneToast(e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });


        //RiceMillHttp.get().getBanner(new Bean01Callback<Banner>() {
        //    @Override
        //    public void onSuccess(Banner banner) {
        //        Banner.DataBean data = banner.data;
        //        ArrayList<String> strings = new ArrayList<>();
        //        for (int i = 0; i <data.img.size() ; i++) {
        //            strings.add(data.img.get(i).image);
        //        }
        //        for (int i = 0; i < data.voide.size(); i++) {
        //            strings.add(data.voide.get(i).image);
        //
        //        }
        //        mBanner.setBannerDataWithUrls(strings);
        //        // 准备
        //        mBanner.prepare();
        //    }
        //
        //    @Override
        //    public void onFailure(String message, Throwable tr) {
        //        showOneToast(message);
        //    }
        //});
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止播放
        mBanner.stopPlay();
    }
    @Override
    public void onResume() {
        super.onResume();
        // 重新播放
        mBanner.resumePlay();
    }
    @Override
    public void onDestroy() {
        // 释放
        if (mBanner!=null) {
            mBanner.releaseBanner();
        }
        super.onDestroy();
    }

    @SuppressLint("ResourceType")
    private void initViewFlipper() {

        //是否自动开始滚动
        vp.setAutoStart(true);
        //滚动时间
        vp.setFlipInterval(2000);
        //开始滚动
        vp.startFlipping();
        //出入动画
        vp.setOutAnimation(getActivity(), R.animator.push_up_out);
        vp.setInAnimation(getActivity(), R.animator.push_down_in);
        scheduled = new ScheduledThreadPoolExecutor(2);
        scheduled.scheduleAtFixedRate(new SwitchTask(), 0, 60 * 1000, TimeUnit.MILLISECONDS);
    }
    private class SwitchTask extends TimerTask {
        @Override
        public void run() {
            RiceMillHttp.get().getLatelyOrder(new Bean01Callback<LatelyOrderBean>() {
                @Override
                public void onSuccess(LatelyOrderBean latelyOrderBean) {
                    List<String> data = latelyOrderBean.data;
                    for (int i = 0; i < data.size(); i++) {
                        TextView view = new TextView(getActivity());
                        ViewFlipper.LayoutParams params =
                            new ViewFlipper.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER_VERTICAL;
                        view.setTextColor(getResources().getColor(R.color.color_333));
                        view.setLayoutParams(params);
                        view.setText(data.get(i));
                        //view.setOnClickListener(new View.OnClickListener() {
                        //    @Override
                        //    public void onClick(View v) {
                        //        //Toast.makeText(getActivity(), "我被点了", Toast.LENGTH_SHORT).show();
                        //    }
                        //});
                        vp.addView(view);
                    }
                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    showOneToast(message);
                }
            });
        }
    }
    public void initFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
    }
}
