package com.example.administrator.gaojianzongnianmiji.utils;

import android.content.Context;
import android.os.Handler;
import cn.dlc.commonlibrary.okgo.exception.ApiException;
import cn.dlc.commonlibrary.okgo.rx.OkObserver;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.App;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.bean.ConfigureBean;
import com.example.administrator.gaojianzongnianmiji.bean.DeviceStateBean;
import com.example.administrator.gaojianzongnianmiji.bean.UserOperationBean;
import com.example.administrator.gaojianzongnianmiji.bean.VersionBean;
import com.example.administrator.gaojianzongnianmiji.dao.DaoMaster;
import com.example.administrator.gaojianzongnianmiji.dao.DaoSession;
import com.example.administrator.gaojianzongnianmiji.dao.OrderBean;
import com.example.administrator.gaojianzongnianmiji.dao.OrderBeanDao;
import com.example.administrator.gaojianzongnianmiji.utils.bean.HeartbeatBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.HeartbeatCallBackBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationAckBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.RechargeOperationBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.ShopOperationBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.StartCallBackBean;
import com.google.gson.Gson;
import com.licheedev.myutils.LogPlus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.greenrobot.greendao.database.Database;

public class MQTTBusiness {
    private static MQTTBusiness business;
    private MqttManager mqttManager;
    private ConnectCallBack connectCallBack;
    private OrderCallBack orderCallBack;
    private DaoSession daoSession;
    private static OrderBeanDao noteDao;
    private String macNo;
    private SerialPortUtil portUtil;
    private OrderOperationBean runOrderBean;
    private HeartbeatBean heartbeatBean;
    private DeviceStateBean deviceStateBean;
    private Disposable disposable;
    private OpenSpCallBack openSpCallBack;
    private ShopOperationCallBack shopOperationCallBack;
    private LoginCallBack loginCallBack;
    private PayRechargeCallBack payRechargeCallBack;
    private UserOperationCallBack userOperationCallBack;
    private ConfigureCallBack configureCallBack;
    private VersionCallBack versionCallBack;
    private AboutCallBack aboutCallBack;
    private RenewCallBack renewCallBack;
    private DeviceAlarmCallBack deviceAlarmCallBack;
    private RelieveAlarmCallBack relieveAlarmCallBack;
    private HitchCallBack hitchCallBack;
    private NoHitchCallBack noHitchCallBack;
    private int heartContent;
    private Handler handler;
    private boolean answer;
    private int packet_num = 0;
    private int workstate1 ;
    private int workstate;
    private String timestampHealth;

    public static MQTTBusiness getInstance(Context context) {
        if (business == null) {
            synchronized (MQTTBusiness.class) {
                if (business == null) {
                    business = new MQTTBusiness(context);
                }
            }
        }
        return business;
    }

    public MQTTBusiness(Context context) {
        portUtil = SerialPortUtil.getInstance();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        noteDao = daoSession.getOrderBeanDao();
        heartbeatBean = new HeartbeatBean();
        deviceStateBean = new DeviceStateBean();
        handler = new Handler();
    }

    public void init(Context context, String macNo) {
        this.macNo = macNo;
        mqttManager = MqttManager.getInstance();
        mqttManager.setCallback(new MqttManager.MqttCallback() {
            @Override
            public void subscribedSuccess(boolean reconnect) {
                if (connectCallBack != null) {
                    handler.post(() -> connectCallBack.subscribedSuccess(reconnect));
                }
            }

            @Override
            public void receiveMessage(String message) {
                LogPlus.e("receiveMessage===" + message);
                riZhiUtil.setrizhi(
                    Constant.ANDROID_ID + "===gaojianzong===receiveMessage===" + message);
                try {//登录成功，充值成功，运维端设置，版本更新，
                    Gson gson1 = new Gson();
                    OrderOperationBean bean1 = gson1.fromJson(message, OrderOperationBean.class);
                    switch (bean1.topic) {
                        case "orderOperation"://买米支付
                            orderOperation(message);
                            break;
                        case "banner":
                            break;
                        case "health":
                            timestampHealth = bean1.timestamp;
                            //if (App.isFirstStart){
                            //    App.isFirstStart = false;
                            testDate(timestampHealth);
                            //}
                            break;
                        case "updateApk":
                            break;
                        case "shopOperation":
                            handler.post(() -> {
                                if (shopOperationCallBack != null) {
                                    Gson gson = new Gson();
                                    ShopOperationBean bean =
                                        gson.fromJson(message, ShopOperationBean.class);
                                    shopOperationCallBack.paySuccess(bean);
                                }
                            });
                            break;
                        case "payRecharge"://会员充值
                            Gson gson = new Gson();
                            RechargeOperationBean rechargeOperationBean =
                                gson.fromJson(message, RechargeOperationBean.class);
                            if (payRechargeCallBack != null) {
                                if (rechargeOperationBean.status == 1) {
                                    handler.post(() -> payRechargeCallBack.rechargeSuccess());
                                } else {
                                    handler.post(() -> payRechargeCallBack.rechargeFail());
                                }
                            }
                            break;
                        case "userOperation"://会员扫描登录
                            Gson gson2 = new Gson();
                            UserOperationBean userOperationBean =
                                gson2.fromJson(message, UserOperationBean.class);
                            if (userOperationCallBack != null) {
                                handler.post(
                                    () -> userOperationCallBack.userOperation(userOperationBean));
                            }
                            break;
                        case "configure"://运维端设置
                            Gson gson3 = new Gson();
                            ConfigureBean configureBean =
                                gson3.fromJson(message, ConfigureBean.class);
                            if (configureCallBack != null) {
                                handler.post(() -> configureCallBack.configure(configureBean));
                            }
                            //PrefUtil.getDefault().saveString(Constant.Configure_Speed,configureBean.speed);
                            //PrefUtil.getDefault().saveString(Constant.Configure_Overtime,configureBean.overtime);
                            //PrefUtil.getDefault().saveString(Constant.Configure_Colse_Delay_Time,configureBean.colse_delay_time);
                            //PrefUtil.getDefault().saveString(Constant.Configure_Stop_Delay_Time,configureBean.stop_delay_time);
                            break;
                        case "version"://更新版本
                            LogPlus.e("version更新");
                            Gson gson4 = new Gson();
                            VersionBean versionBean = gson4.fromJson(message, VersionBean.class);
                            if (versionCallBack != null) {
                                handler.post(() -> versionCallBack.version(versionBean));
                            }
                            break;
                        case "about"://logo和公司名称
                            LogPlus.e("about更新");
                            if (aboutCallBack != null) {
                                handler.post(() -> aboutCallBack.about());
                            }
                            break;
                        case "renew"://轮播图
                            LogPlus.e("renew更新");
                            if (renewCallBack != null) {
                                handler.post(() -> renewCallBack.renew());
                            }
                            break;
                        case "deviceAlarm":
                            LogPlus.e("deviceAlarm报警设置");
                            if (deviceAlarmCallBack != null) {
                                handler.post(() -> deviceAlarmCallBack.deviceAlarm());
                            }
                            break;
                        case "relieveAlarm":
                            LogPlus.e("relieveAlarm解除提升机报警状态");
                            if (relieveAlarmCallBack != null){
                                handler.post(() -> relieveAlarmCallBack.relieveAlarm());
                            }
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectFail(String message) {
                mqttManager.initConnect(context, Constant.ANDROID_ID);
                if (connectCallBack != null) {
                    handler.post(() -> connectCallBack.connectFail(message));
                }
            }

            @Override
            public void connectLost(String message) {
                if (connectCallBack != null) {
                    handler.post(() -> connectCallBack.connectLost(message));
                }
            }
        });
        mqttManager.initConnect(context, macNo);
        portUtil.setOperationResult(new SerialPortUtil.operationResult() {
            @Override
            public void receiveResult(String data) {
                App.heartData = data;
                StringBuffer sb = new StringBuffer();
                String a = "",b="",c="",d="",e="",f="",g="",h="",i="",j="",k="",
                    l="",m="",n="",o="",p="";
                String command = data.substring(0, 4);
                switch (command) {
                    case "0902":
                        LogPlus.e("0902心跳数据===========" + data);
                        String dateType = data.substring(12, 14);
                        if ("79".equals(dateType)) {
                            //int length = Integer.parseInt(data.substring(14, 18), 16) * 2;
                            if (data.length() == 62) {
                                heartbeatBean.lastTime = System.currentTimeMillis();
                                heartbeatBean.data = data;
                                int a_storehousestatus =
                                    Integer.parseInt(data.substring(18, 20), 16);//1号仓
                                int b_storehousestatus =
                                    Integer.parseInt(data.substring(20, 22), 16);//2号仓
                                int riceDoorState =
                                    Integer.parseInt(data.substring(28, 30), 16);//称重传感器
                                int packState = Integer.parseInt(data.substring(26, 28), 16);//塑料袋余量

                                int hengfeng_temp =
                                    Integer.parseInt(data.substring(30, 34), 16);//横封加热片温度
                                int shufeng_temp =
                                    Integer.parseInt(data.substring(34, 38), 16);//横封加热片温度
                                int gucang_temp =
                                    Integer.parseInt(data.substring(38, 42), 16);//谷仓温度
                                int weight = Integer.parseInt(data.substring(42, 50), 16);//重量
                                //int device_state = Integer.parseInt(data.substring(50, 52), 16);//设备状态
                                int DeviceState =
                                    Integer.parseInt(data.substring(52, 54), 16);//报警状态
                                int ElevatorState =
                                    Integer.parseInt(data.substring(54, 56), 16);//提升机报警状态
                                int down_rice = Integer.parseInt(data.substring(56, 58), 16);//下米传感器
                                //0到位1未到位
                                int baozhuang_gan = Integer.parseInt(data.substring(58, 60), 16);//包装传感器
                                //int lamo_gan = Integer.parseInt(data.substring(60, 62), 16);//拉膜传感器
                                heartbeatBean.status = (riceDoorState == 1
                                    || packState == 1
                                    || DeviceState != 0
                                    || ElevatorState == 1
                                    || down_rice == 1
                                    || baozhuang_gan == 1) ? 3 : 1;
                                if (a_storehousestatus==0){
                                    heartbeatBean.storehousestatus = 0;
                                }else {
                                    heartbeatBean.storehousestatus = 1;
                                }
                                if (riceDoorState == 1) {
                                    a = "称重传感器称重异常";
                                }
                                if (packState == 1) {
                                    b = "塑料袋余量为空";
                                }
                                if (ElevatorState == 1) {
                                    c = "提升机送谷超时报警";
                                }
                                if (DeviceState == 0) {
                                    //heartbeatBean.status = 1;
                                } else if (DeviceState == 1) {
                                    d = "温度超温报警";
                                } else if (DeviceState == 2) {
                                    e = "加热超时报警";
                                } else if (DeviceState == 3) {
                                    f = "碾米超时报警";
                                } else if (DeviceState == 4) {
                                    g = "下米超时报警";
                                } else if (DeviceState == 5) {
                                    h = "拉膜超时报警";
                                } else if (DeviceState == 6) {
                                    i = "封膜超时报警";
                                } else if (DeviceState == 7) {
                                    j = "送膜超时报警";
                                } else if (DeviceState == 8) {
                                    k = "主动力电机过流报警";
                                }
                                if (a_storehousestatus == 0){
                                    l = "1号仓缺米";
                                }
                                if (b_storehousestatus == 0){
                                    m = "2号仓缺米";
                                }
                                //if (lamo_gan == 1){
                                //    n = "拉膜传感器未到位";
                                //}
                                if (baozhuang_gan == 1){
                                    o = "包装传感器未到位";
                                }
                                if (down_rice == 1){
                                    p = "下米传感器未到位";
                                }
                                heartbeatBean.remarks = sb.append(a).append(b).append(c).append(d).append(e).append(f).append(g).append(h)
                                .append(i).append(j).append(k).append(l).append(m).append(n).append(o).append(p).toString();
                                //设备状态信息
                                heartbeatBean.info_msg = "横封加热片温度为："
                                    + hengfeng_temp
                                    + "，竖封加热片温度为："
                                    + shufeng_temp
                                    + "，谷仓温度为："
                                    + gucang_temp
                                    + "，重量为："
                                    + weight
                                    + "，设备状态：空闲"
                                    + "，报警状态："
                                    + heartbeatBean.remarks
                                    + "，心跳数据："
                                    + data;
                                //保存状态用于查看状态界面
                                deviceStateBean.hengfeng_temp = hengfeng_temp;
                                deviceStateBean.shufeng_temp = shufeng_temp;
                                deviceStateBean.gucang_temp = gucang_temp;
                                deviceStateBean.weight = weight;
                                deviceStateBean.alarmState = DeviceState;
                                deviceStateBean.elevatorState = ElevatorState;

                                if (runOrderBean != null && heartContent > 10) {
                                    LogPlus.e("未知异常");
                                    orderCallBack(runOrderBean, "未知异常", OrderCallBack.ERROR);
                                    updateDbOrder(runOrderBean.order_number, 2, 3, 11,
                                        "未知异常nextOrder");
                                    startCallBack("2", "11", "未知异常nextOrder",
                                        runOrderBean.order_number, 0);
                                    nextOrder();
                                } else {
                                    heartContent++;
                                }
                            }
                        }
                        break;
                    case "09CA":
                        LogPlus.e("09CA返回数据===========" + data);
                        heartbeatBean.lastTime = System.currentTimeMillis();
                        heartContent = 0;
                        int wa = Integer.parseInt(data.substring(6, 14), 16);
                        if (wa == 0 || wa == 2147483647) {
                            //0是测试是调试的流水
                            return;
                        }
                        if (runOrderBean == null) {
                            runOrderBean = selectByFlWater(wa);
                        } else if (runOrderBean.flowingWater != wa) {
                            runOrderBean = selectByFlWater(wa);
                        }
                        if (runOrderBean != null) {
                            String sign = data.substring(4, 6);
                            switch (sign) {
                                case "02"://交易请求回复
                                    packet_num = 0;
                                    PrefUtil.getDefault().saveInt(Constant.Packet_Num,0);
                                    PrefUtil.getDefault().saveString(Constant.Hitch_Order_Num,runOrderBean.order_number);
                                    PrefUtil.getDefault().saveString(Constant.Hitch_Weight,runOrderBean.weight+"");

                                    int r = Integer.parseInt(data.substring(22, 24), 16);
                                    if (r == 0) {//开启成功
                                        orderCallBack(runOrderBean, "开启成功",
                                            OrderCallBack.OPEN_SUCCESS);
                                        updateDbOrder(runOrderBean.order_number, 2, 1, 0, "");
                                        startCallBack("1", "", "" + data, runOrderBean.order_number,
                                            runOrderBean.weight);
                                    } else if (r == 1) {//重复流水 流水加一再发一次
                                        OrderBean orderBean = noteDao.queryBuilder()
                                            .orderDesc(OrderBeanDao.Properties.Id)
                                            .unique();
                                        int water;
                                        if (orderBean == null) {
                                            water = 1;
                                        } else {
                                            water = orderBean.getFlowingWater() + 1;
                                        }
                                        updateDbOrderWater(runOrderBean.order_number, water);
                                        portUtil.addCA(water, runOrderBean.weight,
                                            runOrderBean.precision);
                                    } else if (r == 2) {//交易失败,谷仓稻谷余量不足
                                        orderCallBack(runOrderBean, "谷仓稻谷余量不足",
                                            OrderCallBack.ERROR);
                                        updateDbOrder(runOrderBean.order_number, 2, 3, 8,
                                            "谷仓稻谷余量不足");
                                        startCallBack("2", "8", "谷仓稻谷余量不足" + data,
                                            runOrderBean.order_number, 0);
                                    } else if (r == 3) {//交易失败,机械故障
                                        orderCallBack(runOrderBean, "变送器通讯故障", OrderCallBack.ERROR);
                                        updateDbOrder(runOrderBean.order_number, 2, 3, 9, "机械故障");
                                        startCallBack("2", "9", "变送器通讯故障" + data,
                                            runOrderBean.order_number, 0);
                                    } else if (r == 5) {//重量为空
                                        orderCallBack(runOrderBean, "重量为空", OrderCallBack.ERROR);
                                        updateDbOrder(runOrderBean.order_number, 2, 3, 10, "重量为空");
                                        startCallBack("2", "10", "重量为空" + data,
                                            runOrderBean.order_number, 0);
                                    } else if (r == 4) {//正在交易
                                        orderCallBack(runOrderBean, "正在交易", OrderCallBack.ERROR);
                                        updateDbOrder(runOrderBean.order_number, 2, 3, 7, "正在交易");
                                        startCallBack("2", "7", "正在交易硬件返回" + data,
                                            runOrderBean.order_number, 0);
                                    } else {//未知异常
                                        orderCallBack(runOrderBean, "硬件未知异常", OrderCallBack.ERROR);
                                        updateDbOrder(runOrderBean.order_number, 2, 3, 11,
                                            "硬件未知异常");
                                        startCallBack("2", "11", "硬件未知异常" + data,
                                            runOrderBean.order_number, 0);
                                    }
                                    break;
                                case "03":
                                    //0x00:空闲0x01:正在碾米0x02:正在加热包装片，等待包装0x03:开始拉膜0x04:开始封袋0x05:开始送膜
                                    answer = false;
                                    workstate1 = workstate;
                                    workstate = Integer.parseInt(data.substring(48, 50), 16);//设备工作状态
                                    orderCallBack(runOrderBean, String.valueOf(workstate),
                                        OrderCallBack.RICEING);
                                    if (workstate1 != workstate&&workstate == 4){
                                        //保存数据
                                        packet_num ++;
                                        PrefUtil.getDefault().saveInt(Constant.Packet_Num,packet_num);
                                        PrefUtil.getDefault().saveString(Constant.Hitch_Order_Num,runOrderBean.order_number);
                                        PrefUtil.getDefault().saveString(Constant.Hitch_Weight,runOrderBean.weight+"");
                                    }
                                    break;
                                case "04":
                                    packet_num = 0;
                                    PrefUtil.getDefault().saveInt(Constant.Packet_Num,-1);

                                    int z = Integer.parseInt(data.substring(14, 22), 16);//终端交易流水
                                    int wt = Integer.parseInt(data.substring(22, 30), 16);//实时出米重量
                                    int rt =
                                        Integer.parseInt(data.substring(30, 32), 16);//上报结果改为报警状态
                                    int finishWeight = Integer.parseInt(data.substring(32, 34), 16);//完成斤数
                                    portUtil.addCA05(wa, z);
                                    if (!answer) {
                                        answer = true;
                                        if (rt == 0) {
                                            orderCallBack(runOrderBean, "碾米完成",
                                                OrderCallBack.FINISH_RICE);
                                            updateDbOrder(runOrderBean.order_number, 2, 2, 0,
                                                "碾米完成上报状态改失败接口返回成再次更改");
                                            endCallBack("1", "", "", runOrderBean.order_number,
                                                String.valueOf(runOrderBean.weight),finishWeight);
                                        } else if (rt == 1) {
                                            orderCallBack(runOrderBean, "温度超温报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "温度超温报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 2) {
                                            orderCallBack(runOrderBean, "加热超时报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "加热超时报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 3) {
                                            orderCallBack(runOrderBean, "碾米超时报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "碾米超时报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 4) {
                                            orderCallBack(runOrderBean, "下米超时报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "下米超时报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 5) {
                                            orderCallBack(runOrderBean, "拉膜超时报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "拉膜超时报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 6) {
                                            orderCallBack(runOrderBean, "封膜超时报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "封膜超时报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 7) {
                                            orderCallBack(runOrderBean, "送膜超时报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "送膜超时报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        } else if (rt == 8) {
                                            orderCallBack(runOrderBean, "主动力电机过流报警,超时未完成出米重量",
                                                OrderCallBack.ERROR);
                                            updateDbOrder(runOrderBean.order_number, 2, 3, 1,
                                                "超时未完成出米重量上报状态改失败接口返回成再次更改");
                                            endCallBack("2", "1", "主动力电机过流报警,超时未完成出米重量",
                                                runOrderBean.order_number, String.valueOf(wt),finishWeight);
                                        }
                                    } else {
                                        nextOrder();
                                    }
                                    break;
                            }
                        }
                        break;
                    case "0C09":
                        int maxOutTime = Integer.parseInt(data.substring(4, 8), 16);
                        PrefUtil.getDefault().saveInt("maxOutTime", maxOutTime);
                        if (openSpCallBack != null) {
                            openSpCallBack.openResult(true);
                        }
                        break;
                }
            }

            @Override
            public void sendErrorListener(String sendCmd) {
                if (sendCmd.substring(20, 24).equals("09CA")
                    && sendCmd.substring(24, 26)
                    .equals("01")
                    && runOrderBean != null
                    && Integer.parseInt(sendCmd.substring(26, 34), 16)
                    == runOrderBean.flowingWater) {
                    LogPlus.e("sendErrorListener启动失败");
                    orderCallBack(runOrderBean, "启动失败", OrderCallBack.ERROR);
                    updateDbOrder(runOrderBean.order_number, 1, 3, 8, "发送启动命令无回复");
                    startCallBack("2", "4", "发送启动命令无回复", runOrderBean.order_number, 0);
                } else if (sendCmd.substring(20, 24).equals("0C09")) {
                    if (openSpCallBack != null) {
                        openSpCallBack.openResult(false);
                    }
                }
            }

            @Override
            public void openFail() {
                if (openSpCallBack != null) {
                    openSpCallBack.openResult(false);
                }
            }
        });
        portUtil.openSerialPort();
        portUtil.addC09();
        heartbeat();
    }

    public void test() {
        portUtil.addC87(5);
    }

    //订单第一步 上报后台收到推送
    public void orderOperation(String message) {
        Gson gson = new Gson();
        OrderOperationBean orderOperationBean = gson.fromJson(message, OrderOperationBean.class);
        //收到出米指令
        orderCallBack(orderOperationBean, "出米中", OrderCallBack.RECEIVED);
        long sysTime = System.currentTimeMillis() / 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long maxTime = 0;
        try {
            maxTime =
                Math.abs(sysTime - simpleDateFormat.parse(orderOperationBean.timestamp).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogPlus.e("maxTime" + maxTime);
        long finalMaxTime = maxTime;
        RiceMillHttp.get()
            .orderAck(macNo, sysTime, orderOperationBean.order_number)
            .subscribeOn(Schedulers.io())
            .retryWhen(new RetryWithDelay((int) (finalMaxTime / 5), 5, false))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new OkObserver<OrderOperationAckBean>() {
                @Override
                public void onSuccess(OrderOperationAckBean bean) {
                    /**
                     * 收到订单和运行中单号相同不做处理
                     */
                    if (runOrderBean != null && orderOperationBean.order_number.equals(
                        runOrderBean.order_number)) {
                        return;
                    }

                    /**
                     * 超时订单
                     */
                    if (finalMaxTime > orderOperationBean.timeout) {
                        LogPlus.e("订单超时" + orderOperationBean.timeout);
                        orderCallBack(orderOperationBean, "订单超时", OrderCallBack.ERROR);
                        startCallBack("2", "5", "订单超时", orderOperationBean.order_number, 0);
                        return;
                    }

                    /**
                     * 订单号重复
                     */
                    OrderBean beans = noteDao.queryBuilder()
                        .where(
                            OrderBeanDao.Properties.OrderNumber.eq(orderOperationBean.order_number))
                        .unique();
                    if (beans != null) {
                        LogPlus.e("订单号重复");
                        orderCallBack(orderOperationBean, "订单号重复", OrderCallBack.ERROR);
                        startCallBack("2", "6", "订单号重复", orderOperationBean.order_number, 0);
                        return;
                    }

                    /**
                     * 有运行中订单
                     */
                    if (runOrderBean != null) {
                        LogPlus.e("设备正在运行Android");
                        orderCallBack(orderOperationBean,
                            "设备正在运行Android" + orderOperationBean.order_number, OrderCallBack.ERROR);
                        startCallBack("2", "7", "设备正在运行Android" + orderOperationBean.order_number,
                            orderOperationBean.order_number, 0);
                        return;
                    }

                    OrderBean orderBean = noteDao.queryBuilder()
                        .orderDesc(OrderBeanDao.Properties.Id)
                        .limit(1)
                        .unique();
                    int water;
                    if (orderBean == null) {
                        water = 1;
                    } else {
                        water = orderBean.getFlowingWater() + 1;
                    }
                    heartContent = 0;
                    runOrderBean = new OrderOperationBean();
                    runOrderBean.timeout = orderOperationBean.timeout;
                    runOrderBean.timestamp = orderOperationBean.timestamp;
                    runOrderBean.macno = orderOperationBean.macno;
                    runOrderBean.order_number = orderOperationBean.order_number;
                    runOrderBean.weight = orderOperationBean.weight;
                    runOrderBean.flowingWater = water;
                    saveDbOrder(runOrderBean.order_number, runOrderBean.weight, 1, 0, 0, "", water,
                        0);
                    //上报成功后才去碾米
                    portUtil.addCA(water, runOrderBean.weight, orderOperationBean.precision);
                    //模拟出米1.开启成功
                    //orderCallBack(runOrderBean, "开启成功", OrderCallBack.OPEN_SUCCESS);
                    //updateDbOrder(runOrderBean.order_number, 2, 1, 0, "");
                    //startCallBack("1", "", "", runOrderBean.order_number,
                    //    runOrderBean.weight);
                    //2.碾米过程
                    //new Handler().postDelayed(new Runnable() {
                    //    @Override
                    //    public void run() {
                    //        orderCallBack(runOrderBean,"1",OrderCallBack.RICEING);
                    //    }
                    //},1000);
                    ////3.出米成功
                    //new Handler().postDelayed(new Runnable() {
                    //    @Override
                    //    public void run() {
                    //        orderCallBack(runOrderBean, "碾米完成", OrderCallBack.FINISH_RICE);
                    //        //updateDbOrder(runOrderBean.order_number, 2, 2, 0, "碾米完成上报状态改失败接口返回成再次更改");
                    //        //endCallBack("1", "", "", runOrderBean.order_number, String.valueOf(runOrderBean.weight));
                    //    }
                    //},8000);

                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    LogPlus.e(message);
                    orderCallBack(orderOperationBean, message, OrderCallBack.ERROR);
                }
            });
    }

    private void startCallBack(String status, String err_code, String err_msg, String order_number,
        int weight) {
        int time = PrefUtil.getDefault().getInt("maxOutTime", 100);
        RiceMillHttp.get()
            .startCallBack(status, err_code, err_msg, macNo, order_number, weight,
                weight / 500 * time * 1000)
            .retryWhen(new RetryWithDelay(10, 5, true))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> {
                if ("2".equals(status) && runOrderBean != null && runOrderBean.order_number.equals(
                    order_number)) {
                    nextOrder();
                }
            })
            .subscribe(new OkObserver<StartCallBackBean>() {
                @Override
                public void onSuccess(StartCallBackBean bean) {
                    updateDbOrder(order_number, 1, 0, 0, "启动结果上报成功");
                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    if (tr instanceof ApiException) {
                        updateDbOrder(order_number, 1, 0, 0, message);
                    } else {
                        updateDbOrder(order_number, 2, 0, 0, message);
                    }
                }
            });
    }

    private void endCallBack(String status, String err_code, String err_msg, String order_number,
        String weight,int out_num) {
        RiceMillHttp.get()
            .endCallBack(status, err_code, err_msg, macNo, order_number, weight,out_num)
            .retryWhen(new RetryWithDelay(10, 5, true))
            .doFinally(() -> {
                if (runOrderBean != null && runOrderBean.order_number.equals(order_number)) {
                    LogPlus.e("下一个1");
                    nextOrder();
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new OkObserver<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    updateDbOrder(order_number, 1, 0, 0, "碾米完成上报成功");
                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    if (tr instanceof ApiException) {
                        updateDbOrder(order_number, 1, 0, 0, message);
                    } else {
                        updateDbOrder(order_number, 2, 0, 0, message);
                    }
                }
            });
    }

    //心跳
    private void heartbeat() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = Observable.interval(10, 30, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnEach(longNotification -> {
                //if (System.currentTimeMillis() - heartbeatBean.lastTime > 20 * 1000) {
                //    heartbeatBean.status = 3;
                //}
            })
            .subscribe(aLong -> {
                //控制设备是否可用
                if (heartbeatBean.status==3){
                    if (hitchCallBack != null) {
                        handler.post(() -> hitchCallBack.hitch(heartbeatBean.remarks));
                    }
                }else {
                    if (noHitchCallBack != null){
                        handler.post(() -> noHitchCallBack.nohitch());
                    }
                }
                //保存状态
                PrefUtil.getDefault()
                    .saveString(Constant.Hengfeng_temp, deviceStateBean.hengfeng_temp + "");
                PrefUtil.getDefault()
                    .saveString(Constant.Shufeng_temp, deviceStateBean.shufeng_temp + "");
                PrefUtil.getDefault()
                    .saveString(Constant.Gucang_temp, deviceStateBean.gucang_temp + "");
                PrefUtil.getDefault()
                    .saveString(Constant.Device_Weight, deviceStateBean.weight + "");
                PrefUtil.getDefault().saveInt(Constant.Alarm_state, deviceStateBean.alarmState);
                PrefUtil.getDefault()
                    .saveInt(Constant.ElevatorState_state, deviceStateBean.elevatorState);
                //心跳接口
                RiceMillHttp.get()
                    .heartbeat(String.valueOf(heartbeatBean.status),
                        String.valueOf(heartbeatBean.storehousestatus), heartbeatBean.remarks,
                        heartbeatBean.info_msg)
                    .retryWhen(new RetryWithDelay(2, 3, false))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogPlus.e(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                    })
                    .subscribe(new OkObserver<HeartbeatCallBackBean>() {
                        @Override
                        public void onSuccess(HeartbeatCallBackBean bean) {
                            //if (Math.abs(System.currentTimeMillis() / 1000 - bean.timestamp) > 20) {
                            //testDate(bean.timestamp);
                            //}
                        }

                        @Override
                        public void onFailure(String message, Throwable tr) {
                            LogPlus.e(message);
                        }
                    });
            });
    }

    private void orderCallBack(OrderOperationBean bean, String msg, int state) {
        if (orderCallBack != null) {
            handler.post(() -> orderCallBack.outRiceResult(bean, msg, state));
        }
    }

    private OrderOperationBean selectByFlWater(int water) {
        OrderBean bean =
            noteDao.queryBuilder().where(OrderBeanDao.Properties.FlowingWater.eq(water)).unique();
        OrderOperationBean operationBean = new OrderOperationBean();
        if (bean != null) {
            operationBean.macno = macNo;
            operationBean.weight = bean.getWeight();
            operationBean.order_number = bean.getOrderNumber();
            operationBean.flowingWater = bean.getFlowingWater();
            return operationBean;
        }
        return null;
    }

    private void saveDbOrder(String orderNum, int weight, int updateState, int successState,
        int failState, String msg, int water, int realWeight) {
        OrderBean bean = new OrderBean();
        bean.setOrderNumber(orderNum);
        bean.setWeight(weight);
        bean.setUpdateTime(System.currentTimeMillis());
        bean.setUpdateState(updateState);
        bean.setSuccessState(successState);
        bean.setFailState(failState);
        bean.setMsg(msg);
        bean.setFlowingWater(water);
        bean.setRealWeight(realWeight);
        noteDao.insert(bean);
    }

    private void updateDbOrder(String orderNum, int updateState, int successState, int failState,
        String msg) {
        OrderBean bean =
            noteDao.queryBuilder().where(OrderBeanDao.Properties.OrderNumber.eq(orderNum)).unique();
        if (bean == null) {
            return;
        }
        bean.setOrderNumber(orderNum);
        bean.setUpdateTime(System.currentTimeMillis());
        bean.setUpdateState(updateState);
        if (successState != 0) {
            bean.setSuccessState(successState);
        }
        if (failState != 0) {
            bean.setFailState(failState);
        }
        bean.setMsg(msg);
        noteDao.update(bean);
    }

    private void nextOrder() {
        runOrderBean = null;
    }

    private void updateDbOrderWater(String orderNum, int water) {
        OrderBean bean =
            noteDao.queryBuilder().where(OrderBeanDao.Properties.OrderNumber.eq(orderNum)).unique();
        if (bean == null) {
            return;
        }
        bean.setOrderNumber(orderNum);
        bean.setUpdateTime(System.currentTimeMillis());
        bean.setFlowingWater(water);
        noteDao.update(bean);
    }

    public void setConnectCallBack(ConnectCallBack connectCallBack) {
        this.connectCallBack = connectCallBack;
    }

    public void setOpenSpCallBack(OpenSpCallBack callBack) {
        this.openSpCallBack = callBack;
    }

    public void setUserOperationCallBack(UserOperationCallBack callBack) {
        this.userOperationCallBack = callBack;
    }

    public void setPayRechargeCallBack(PayRechargeCallBack callBack) {
        this.payRechargeCallBack = callBack;
    }

    public void setConfigureCallBack(ConfigureCallBack callBack) {
        this.configureCallBack = callBack;
    }

    public void setOrderCallBack(OrderCallBack callBack) {
        this.orderCallBack = callBack;
    }

    public void setVersionCallBack(VersionCallBack callBack) {
        this.versionCallBack = callBack;
    }

    public void setAboutCallBack(AboutCallBack callBack) {
        this.aboutCallBack = callBack;
    }

    public void setRenewCallBack(RenewCallBack callBack) {
        this.renewCallBack = callBack;
    }

    public void setDeviceAlarmCallBack(DeviceAlarmCallBack callBack) {
        this.deviceAlarmCallBack = callBack;
    }

    public void setRelieveAlarmCallBack(RelieveAlarmCallBack callBack){
        this.relieveAlarmCallBack = callBack;
    }

    public void setHitchCallBack(HitchCallBack callBack){
        this.hitchCallBack = callBack;
    }

    public void setNoHitchCallBack(NoHitchCallBack callBack){
        this.noHitchCallBack = callBack;
    }

    /**
     * 1 设备故障 2请取米 3请关闭米舱门 4运行中 0可以买米
     *
     * @return
     */
    public int getState() {
        if (heartbeatBean.status == 3) {
            return 1;
        }
        if (heartbeatBean.outRiceState == 1) {
            return 2;
        }

        if (heartbeatBean.riceDoorState == 1) {
            return 3;
        }
        if (heartbeatBean.status == 2) {
            return 4;
        }
        return 0;
    }

    public interface ConnectCallBack {
        /**
         * true 断开重连,false 首次连接
         *
         * @param reconnect
         */
        void subscribedSuccess(boolean reconnect);

        /**
         * 连接失败 不会重连 检查网络 ip端口
         *
         * @param message
         */
        void connectFail(String message);

        /**
         * 连接断开 会重连
         *
         * @param message
         */
        void connectLost(String message);
    }

    public interface OpenSpCallBack {
        void openResult(boolean state);
    }

    public interface PayRechargeCallBack {
        void rechargeSuccess();

        void rechargeFail();
    }

    public interface ShopOperationCallBack {
        void paySuccess(ShopOperationBean bean);
    }

    public interface LoginCallBack {
        void userInfo(String token, String openid, int type);
    }

    public interface UserOperationCallBack {
        void userOperation(UserOperationBean bean);
    }

    public interface ConfigureCallBack {
        void configure(ConfigureBean bean);
    }

    public interface VersionCallBack {
        void version(VersionBean bean);
    }

    public interface AboutCallBack {
        void about();
    }

    public interface RenewCallBack {
        void renew();
    }

    public interface DeviceAlarmCallBack {
        void deviceAlarm();
    }

    public interface RelieveAlarmCallBack{
        void relieveAlarm();
    }

    public interface HitchCallBack{
        void hitch(String remarks);
    }

    public interface NoHitchCallBack{
        void nohitch();
    }

    public void close() {
        if (portUtil != null) {
            portUtil.close();
        }
        if (mqttManager != null) {
            mqttManager.onDestroy();
        }
    }

    public void testDate(String string) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            String datetime = stampToDate(string); //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 将时间戳转换为时间
     */
    public String stampToDate(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd.HHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        String res = simpleDateFormat1.format(ts);
        //String formats = "yyyyMMdd.HHmmss";
        //Long timestamp = s * 1000;
        //String res = new SimpleDateFormat(formats, Locale.CHINA).format(s);
        return res;
    }
}
