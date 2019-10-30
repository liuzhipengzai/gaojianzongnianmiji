package com.example.administrator.gaojianzongnianmiji.utils;

import cn.dlc.commonlibrary.okgo.exception.ApiException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;

public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;
    private boolean httpErrorInfinite;

    public RetryWithDelay(int maxRetries, int retryDelayMillis, boolean httpErrorInfinite) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.httpErrorInfinite = httpErrorInfinite;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (throwable instanceof ApiException) {
                return Observable.error(throwable);
            } else {
                if (!httpErrorInfinite) {
                    retryCount++;
                }
                if (retryCount <= maxRetries) {
                    return Observable.timer(retryDelayMillis, TimeUnit.SECONDS);
                } else {
                    return Observable.error(throwable);
                }
            }
        });
    }
}