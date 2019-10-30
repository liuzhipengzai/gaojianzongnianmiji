package com.example.administrator.gaojianzongnianmiji;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import cn.dlc.commonlibrary.BuildConfig;
import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.okgo.exception.ApiException;
import cn.dlc.commonlibrary.okgo.logger.JsonRequestLogger;
import cn.dlc.commonlibrary.utils.PrefUtil;
import cn.dlc.commonlibrary.utils.ResUtil;
import cn.dlc.commonlibrary.utils.ScreenUtil;
import cn.dlc.commonlibrary.utils.SystemUtil;
import cn.dlc.commonlibrary.utils.ToastUtil;
import com.danikula.videocache.HttpProxyCacheServer;
import com.dlc.bannerplayer.VideoCacheProxy;
import com.dlc.silentupdatelibrary.SilentUpdateListener;
import com.dlc.silentupdatelibrary.SilentUpdateUtil;
import com.example.administrator.gaojianzongnianmiji.base.MainActivity;
import com.example.administrator.gaojianzongnianmiji.bean.AlarmBean;
import com.example.administrator.gaojianzongnianmiji.bean.ConfigureBean;
import com.example.administrator.gaojianzongnianmiji.bean.VersionBean;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.utils.MyErrorTranslator;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.licheedev.myutils.LogPlus;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2019/5/23.
 */

public class App extends MultiDexApplication  implements VideoCacheProxy.AppWrapper  {


    private HttpProxyCacheServer mProxy;
    private HttpProxyCacheServer newProxy() {
        // 这里的配置可以修改，参考上面的“这里”
        return new HttpProxyCacheServer.Builder(this)
            // 视频缓存文件所在的文件夹，可以进行修改
            .cacheDirectory(VideoCacheProxy.getVideoCacheDir(this))
            .build();
    }

    @Override
    public HttpProxyCacheServer getVideoCacheProxy() {
        return mProxy == null ? (mProxy = newProxy()) : mProxy;
    }
    private static App sInstance;
    public static String heartData;
    public static boolean isFirstStart = true;
    private SerialPortUtil portUtil;
    public static boolean isUpdating = false; //是否升级中
    private Handler mHandler = new Handler();
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //Constant.ANDROID_ID="2018081000030001";
        //Constant.ANDROID_ID="dlc1001";
        Constant.ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        LogPlus.e("设备号==="+Constant.ANDROID_ID);
        portUtil = SerialPortUtil.getInstance();
        MultiDex.install(this);
        //初始化mqtt
        initMQTT();
        if (SystemUtil.isMainProcess(this)) {
            ScreenUtil.init(this); // 获取屏幕尺寸
            ResUtil.init(this); // 资源
            PrefUtil.init(this); // SharedPreference
            // 网络
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
            OkGoWrapper.initOkGo(this, builder.build());
            OkGoWrapper.instance()
                    // 错误信息再格式化
                    .setErrorTranslator(new MyErrorTranslator())
                    // 拦截网络错误，一般是登录过期啥的
                    .setErrorInterceptor(tr -> {
                        if (tr instanceof ApiException) {
                            ApiException ex = (ApiException) tr;
                            if (ex.getCode() == 101) {
                                ToastUtil.showOne(getApplicationContext(), "登录信息已过期或已在其它设备登录,请重新登录!");
                                // 登录信息过期，请重新登录
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                return true;
                            }
                        }
                        return false;
                    })
                    // 打印网络访问日志的
                    .setRequestLogger(new JsonRequestLogger(BuildConfig.DEBUG, 30));
        }

        SilentUpdateUtil.getInstance().setSilentUpdateListener(new SilentUpdateListener() {
            @Override
            public void onUpdateSuccess() {
                LogPlus.e("App onUpdateSuccess");
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
            }
            @Override
            public void onUpdateFail() {
                LogPlus.e("App onUpdateFail");
            }
        });


        initBugly();

    }



    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 初始化bugly
     */
    private void initBugly() {
        Context context = getApplicationContext();
// 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
// 初始化Bugly
        CrashReport.initCrashReport(context, "5925fdef61", false, strategy);
    }


    public static App instance() {
        return sInstance;
    }


    private Thread.UncaughtExceptionHandler restartHandler = (thread, ex) -> {
        restartApp();//发生崩溃异常时,重启应用
    };

    public void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    private void initMQTT() {
        MQTTBusiness.getInstance(this).init(this, Constant.ANDROID_ID);
        MQTTBusiness.getInstance(this).setOpenSpCallBack(state -> {
            LogPlus.e("串口状态" + state);
        });
        MQTTBusiness.getInstance(this).setConfigureCallBack(new MQTTBusiness.ConfigureCallBack() {
            @Override
            public void configure(ConfigureBean bean) {
                //portUtil.addC8C(bean.speed);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        portUtil.addC89(1, bean.overtime);
                    }
                },1000);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        portUtil.addC88(bean.colse_delay_time);
                    }
                },2000);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        portUtil.addC87(bean.stop_delay_time);
                    }
                },3000);
                //保存精度
                PrefUtil.getDefault().saveString(Constant.GoodsID1,bean.precision1);
                PrefUtil.getDefault().saveString(Constant.GoodsID2,bean.precision2);
                PrefUtil.getDefault().saveString(Constant.GoodsID3,bean.precision3);
            }
        });
        MQTTBusiness.getInstance(this).setConnectCallBack(new MQTTBusiness.ConnectCallBack() {
            @Override
            public void subscribedSuccess(boolean reconnect) {
                LogPlus.e("是否重连" + reconnect);
            }

            @Override
            public void connectFail(String message) {
                LogPlus.e(message);
            }

            @Override
            public void connectLost(String message) {
                LogPlus.e(message);
            }
        });

        MQTTBusiness.getInstance(this).setVersionCallBack(new MQTTBusiness.VersionCallBack() {
            @Override
            public void version(VersionBean bean) {
                String code = SystemUtil.getVersionName(getApplicationContext());
                LogPlus.e("code==="+code+"====version==="+bean.versions);
                if (code.equals(bean.versions)){
                    return;
                }else {
                    if (!isUpdating) {
                        isUpdating = true;
                        LogPlus.e("isUpdating");
                        SilentUpdateUtil.getInstance()
                            .downloadAndInstallThroughCache(getApplicationContext(), bean.url);
                        LogPlus.e("code==="+code);
                    } else {
                        isUpdating = false;
                    }
                }

            }
        });
        MQTTBusiness.getInstance(this).setDeviceAlarmCallBack(new MQTTBusiness.DeviceAlarmCallBack() {
            @Override
            public void deviceAlarm() {
                RiceMillHttp.get().getAlarm(new Bean01Callback<AlarmBean>() {
                    @Override
                    public void onSuccess(AlarmBean bean) {
                        AlarmBean.DataBean data = bean.data;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC17(data.overheat);
                            }
                        },1000);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC19(data.warm_overtime);
                            }
                        },2000);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC1B(data.down_overtime);
                            }
                        },3000);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC1D(data.lamo_overtime);
                            }
                        },4000);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC1F(data.fengmo_overtime);
                            }
                        },5000);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC21(data.give_rice_overtime);
                            }
                        },6000);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                portUtil.addC23(data.give_mo_overtime);
                            }
                        },7000);
                    }

                    @Override
                    public void onFailure(String message, Throwable tr) {
                        ToastUtil.show(getApplicationContext(),message);
                    }
                });

            }
        });

        MQTTBusiness.getInstance(this).setRelieveAlarmCallBack(new MQTTBusiness.RelieveAlarmCallBack() {
            @Override
            public void relieveAlarm() {
                portUtil.addC15();
            }
        });
    }
}

