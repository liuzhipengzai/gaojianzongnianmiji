package com.example.administrator.gaojianzongnianmiji;

import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.okgo.callback.MyCallback;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.base.BaseBean;
import com.example.administrator.gaojianzongnianmiji.bean.AboutBean;
import com.example.administrator.gaojianzongnianmiji.bean.AlarmBean;
import com.example.administrator.gaojianzongnianmiji.bean.Banner;
import com.example.administrator.gaojianzongnianmiji.bean.CommitBean;
import com.example.administrator.gaojianzongnianmiji.bean.DeviceInfoBean;
import com.example.administrator.gaojianzongnianmiji.bean.IntegralBean;
import com.example.administrator.gaojianzongnianmiji.bean.LatelyOrderBean;
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;
import com.example.administrator.gaojianzongnianmiji.bean.MealListBean;
import com.example.administrator.gaojianzongnianmiji.bean.OperationBean;
import com.example.administrator.gaojianzongnianmiji.bean.PayOrderBean;
import com.example.administrator.gaojianzongnianmiji.bean.PayRechargeOrderBean;
import com.example.administrator.gaojianzongnianmiji.bean.RechargeBean;
import com.example.administrator.gaojianzongnianmiji.bean.UpdaePrecisionBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.HeartbeatCallBackBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationAckBean;
import com.example.administrator.gaojianzongnianmiji.utils.bean.StartCallBackBean;
import com.lzy.okgo.model.HttpParams;
import io.reactivex.Observable;

public class RiceMillHttp {
    private final OkGoWrapper mOkGoWrapper;

    private RiceMillHttp() {
        mOkGoWrapper = OkGoWrapper.instance();
    }

    private static class InstanceHolder {
        private static final RiceMillHttp sInstance = new RiceMillHttp();
    }

    public static RiceMillHttp get() {
        return InstanceHolder.sInstance;
    }

    public Observable<OrderOperationAckBean> orderAck(String macno, long timestamp,
        String order_number) {
        HttpParams params = new HttpParams();
        params.put("api_name", "orderOperationAck");
        params.put("macno", macno);
        params.put("timestamp", timestamp);
        params.put("order_number", order_number);
        return mOkGoWrapper.rxPostBean01(BaseUrl.URL+"/wxsite/device/api", params,
            OrderOperationAckBean.class);
    }
    //是否开始碾米
    public Observable<StartCallBackBean> startCallBack(String status, String err_code,
        String err_msg, String macno, String order_number, int weight, int interval) {
        HttpParams params = new HttpParams();
        params.put("api_name", "outcome");
        params.put("status", status);
        params.put("err_code", err_code);
        params.put("err_msg", err_msg);
        params.put("macno", macno);
        params.put("order_number", order_number);
        params.put("weight", weight);
        params.put("interval", interval);
        return mOkGoWrapper.rxPostBean01(BaseUrl.URL+"/wxsite/device/api", params, StartCallBackBean.class);
    }

    //碾米结束
    public Observable<BaseBean> endCallBack(String status, String err_code, String err_msg,
        String macno, String order_number, String weight,int out_num) {
        HttpParams params = new HttpParams();
        params.put("api_name", "checkOrderStatus");
        params.put("status", status);
        params.put("err_code", err_code);
        params.put("err_msg", err_msg);
        params.put("macno", macno);
        params.put("order_number", order_number);
        params.put("weight", weight);
        params.put("out_num",out_num);
        return mOkGoWrapper.rxPostBean01(BaseUrl.URL+"/wxsite/device/api", params, BaseBean.class);
    }

    public Observable<HeartbeatCallBackBean> heartbeat( String status,
        String storehousestatus,String remarks,String info_msg) {
        HttpParams params = new HttpParams();
        params.put("api_name", "health");
        params.put("macno", Constant.ANDROID_ID);
        params.put("status", status);
        params.put("storehousestatus", storehousestatus);
        params.put("remarks", remarks);
        params.put("info_msg",info_msg);
        return mOkGoWrapper.rxPostBean01(BaseUrl.URL + "/wxsite/device/api", params,
            HeartbeatCallBackBean.class);
    }

    //获取广告
    //public void getBanner(MyCallback<Banner> callback) {
    //    HttpParams httpParams = new HttpParams();
    //    httpParams.put("api_name", "banner");
    //    httpParams.put("macno", Constant.ANDROID_ID);
    //    mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, Banner.class,
    //        callback);
    //}
    /**
     * 获取广告
     */
    public Observable<Banner> getBanner() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "banner");
        httpParams.put("macno", Constant.ANDROID_ID);
        return  mOkGoWrapper.rxPostBean01(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, Banner.class);
    }

    //商品列表
    public void getCommodity(MyCallback<MainShopbean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "commodity");
        httpParams.put("macno", Constant.ANDROID_ID);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, MainShopbean.class,
            callback);
    }

    //近期消费列表
    public void getLatelyOrder(MyCallback<LatelyOrderBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "latelyOrder");
        httpParams.put("macno", Constant.ANDROID_ID);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, LatelyOrderBean.class,
            callback);
    }

    //会员登陆(apk)
    public void login(String mobile, String code, MyCallback<LoginBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "login");
        httpParams.put("mobile", mobile);
        httpParams.put("code", code);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/user/api", null, httpParams, LoginBean.class,
            callback);
    }

    //会员卡信息
    public void apkuserInfo(String idcard, MyCallback<LoginBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "apkuserInfo");
        httpParams.put("idcard", idcard);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/goods/api", null, httpParams, LoginBean.class,
            callback);
    }

    //充值套餐
    public void setMealList(MyCallback<MealListBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "setMealList");
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/goods/api", null, httpParams, MealListBean.class,
            callback);
    }

    //充值支付（IC卡生成二维码）
    public void apkRecharge(LoginBean.DataBean bean, int meal_id, int type,
        MyCallback<RechargeBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "apkRecharge");
        httpParams.put("meal_id", meal_id);
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("type", type);
        httpParams.put("number", bean.idcard);
        httpParams.put("token", bean.token);

        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/goods/api", null, httpParams, RechargeBean.class,
            callback);
    }

    //运维人员登录
    public void replenish_land(String account, String password, MyCallback<OperationBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "operation_land");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("account", account);
        httpParams.put("password", password);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, OperationBean.class,
            callback);
    }

    //买米支付生成订单详情
    public void pay_order(int goods_num, String precision, String pay_type, String goodsId,
        String token, String number, MyCallback<PayOrderBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "pay_order");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("goods_num", goods_num);
        httpParams.put("precision", precision);
        httpParams.put("pay_type", pay_type);
        httpParams.put("goodsId", goodsId);
        httpParams.put("token", token);
        httpParams.put("number", number);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, PayOrderBean.class,
            callback);
    }
    
    //积分抵扣
    public void integral( MyCallback<IntegralBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "integral");
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, IntegralBean.class,
            callback);
    }
    
    //确认支付
    public void payOrder(String order_number, MyCallback<CommitBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "payOrder");
        httpParams.put("order_number", order_number);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, CommitBean.class,
            callback);
    }
    //发送验证码
    public void sendSms(String mobile,int type, MyCallback<BaseBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("mobile", mobile);
        httpParams.put("type", type);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/Public/sendSms", null, httpParams, BaseBean.class,
            callback);
    }
    //设备信息获取二维码扫描登录
    public void deviceInfo(MyCallback<DeviceInfoBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "deviceInfo");
        httpParams.put("macno", Constant.ANDROID_ID);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, DeviceInfoBean.class,
            callback);
    }
    //获取充值参数（APK）
    public void payRechargeOrder(String order_number,MyCallback<PayRechargeOrderBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "payRechargeOrder");
        httpParams.put("order_number", order_number);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/goods/api", null, httpParams, PayRechargeOrderBean.class,
            callback);
    }
    //补货
    public void replenish(String inventory,String token,String bag_num,MyCallback<BaseBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "replenish");
        httpParams.put("inventory", inventory);
        httpParams.put("token", token);
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("bag_num",bag_num);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, BaseBean.class,
            callback);
    }
    //关于我们
    public void about(MyCallback<AboutBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "about");
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/user/api", null, httpParams, AboutBean.class,
            callback);
    }
    //关于我们
    public Observable<AboutBean> about() {
        HttpParams Params = new HttpParams();
        Params.put("api_name", "about");
        return  mOkGoWrapper.rxPostBean01(BaseUrl.URL + "/wxsite/user/api", null, Params, AboutBean.class);
    }
    //关于我们
    public void about(Bean01Callback<AboutBean> callback) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "about");
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/user/api", null, httpParams, AboutBean.class,
            callback);
    }
    //更新精度
    public void updaePrecision(String precision1,String precision2,String precision3,String token,Bean01Callback<UpdaePrecisionBean> callback){
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "updaePrecision");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("token", token);
        httpParams.put("precision1", precision1);
        httpParams.put("precision2", precision2);
        httpParams.put("precision3", precision3);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/device/api", null, httpParams, UpdaePrecisionBean.class,
            callback);
    }
    //日志
    public void rizhi(String rizhi,Bean01Callback<BaseBean> callback){
        HttpParams httpParams = new HttpParams();
        httpParams.put("msa", rizhi);
        mOkGoWrapper.post("http://sbygxnmj.app.xiaozhuschool.com/wxsite/scan/dd" , null, httpParams, BaseBean.class, callback);
    }
    //修改稻谷或袋子的数量
    public void editDevice(String token,String bag,String warehouse,Bean01Callback<BaseBean> callback){
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "editDevice");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("token", token);
        httpParams.put("bag", bag);
        httpParams.put("warehouse", warehouse);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, BaseBean.class,
            callback);
    }
    //更新交易超时时间
    //关闭延迟时间
    //延迟停转时间
    public void updaePrecision1(int type,int time,String token,Bean01Callback<UpdaePrecisionBean> callback){
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "updaePrecision");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("token", token);
        if (type==1){
            httpParams.put("overtime", time);
        }else if (type==2){
            httpParams.put("colse_delay_time", time);
        }else {
            httpParams.put("stop_delay_time", time);
        }
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/device/api", null, httpParams, UpdaePrecisionBean.class,
            callback);
    }

    //更新报警
    public void updateAlarm(int type,int content,Bean01Callback<BaseBean> callback){
        String token =  PrefUtil.getDefault().getString(Constant.operation_land_token, "");
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "updateAlarm");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("token", token);
        if (type==1){
            httpParams.put("overheat", content);
        }else if (type==2){
            httpParams.put("warm_overtime", content);
        }else if (type==3){
            httpParams.put("down_overtime", content);
        }else if (type==4){
            httpParams.put("lamo_overtime", content);
        }else if (type==5){
            httpParams.put("fengmo_overtime", content);
        }else if (type==6){
            httpParams.put("give_rice_overtime", content);
        }else if (type==7){
            httpParams.put("give_mo_overtime", content);
        }
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/device/api", null, httpParams, BaseBean.class,
            callback);
    }

    //报警设置（查询）
    public void getAlarm(Bean01Callback<AlarmBean> callback){
        String token =  PrefUtil.getDefault().getString(Constant.operation_land_token, "");
        HttpParams httpParams = new HttpParams();
        httpParams.put("api_name", "getAlarm");
        httpParams.put("macno", Constant.ANDROID_ID);
        httpParams.put("token", token);
        mOkGoWrapper.post(BaseUrl.URL + "/wxsite/scan/api", null, httpParams, AlarmBean.class,
            callback);
    }
}
