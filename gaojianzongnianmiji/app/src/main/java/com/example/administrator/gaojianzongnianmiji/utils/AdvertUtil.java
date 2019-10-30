package com.example.administrator.gaojianzongnianmiji.utils;
import android.widget.TextView;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuwenzhuo on 2019/6/5.
 */

public class AdvertUtil {

    protected static Disposable mDisposable;
    public static void showCountDownTime(int seconds, TextView tvTime,TimeCallback timeCallback) {
        disposable();
        mDisposable = Flowable.intervalRange(0, seconds + 1, 0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(aLong -> {

                tvTime.setText(seconds - aLong + "s");
            }).doOnComplete(() -> {
                if (timeCallback != null) {
                    timeCallback.timeFinish();
                }
            }).subscribe();

    }

    public static void disposable() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable=null;
        }
    }
    //public static CountDownTimer countDownTimer;
    //private static long advertisingTime = 30 * 1000;//定时跳转广告时间
    //public static void startAD(Activity activity) {
    //    if (countDownTimer == null) {
    //        countDownTimer = new CountDownTimer(advertisingTime, 1000) {
    //            @Override
    //            public void onTick(long millisUntilFinished) {
    //
    //            }
    //
    //            @Override
    //            public void onFinish() {
    //                //定时完成后的操作
    //
    //                Class aClass=getClass();
    //                LogPlus.e("aClass"+aClass);
    //                if (aClass==AdvertActivity.class){
    //                    return;
    //                }else {
    //                    Intent intent = new Intent(activity,AdvertActivity.class);
    //                    activity.startActivity(intent);
    //                    activity.finish();
    //                }
    //
    //            }
    //        };
    //        countDownTimer.start();
    //    } else {
    //        countDownTimer.start();
    //    }
    //}
    //public static void cancal(){
    //    if (countDownTimer!=null){
    //        countDownTimer.cancel();
    //    }
}
