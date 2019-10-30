package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseBean;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.bean.CommitBean;
import com.example.administrator.gaojianzongnianmiji.bean.DeviceInfoBean;
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;
import com.example.administrator.gaojianzongnianmiji.bean.PayOrderBean;
import com.example.administrator.gaojianzongnianmiji.bean.UserOperationBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.utils.OrderCallBack;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationBean;
import com.example.administrator.gaojianzongnianmiji.view.HintDialog;
import com.example.administrator.gaojianzongnianmiji.view.OrderDialog;
import com.example.administrator.gaojianzongnianmiji.view.PhoneLoginDialog;
import com.example.administrator.gaojianzongnianmiji.view.ScanDialog;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class MainShopItemFragment extends BaseFragment {
    @BindView(R.id.head_img)
    ImageView mHeadImg;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.price_tv)
    TextView mPriceTv;
    @BindView(R.id.jian_img)
    ImageView mJianImg;
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.add_img)
    ImageView mAddImg;
    @BindView(R.id.pay_img)
    ImageView mPayImg;
    @BindView(R.id.pay_ll)
    LinearLayout mPayLl;
    @BindView(R.id.huiyan_ll)
    LinearLayout mHuiyanLl;
    @BindView(R.id.card_ll)
    LinearLayout mCardLl;
    private int number = 1;
    private MainShopbean.DataBean mBean;
    ScanDialog scanDialog;
    PhoneLoginDialog mPhoneLoginDialog;
    OrderDialog orderDialog;
    private SerialPortUtil portUtil;
    private int warehouse;
    private int bag;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_shop_item;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        portUtil = SerialPortUtil.getInstance();
        initVIew();
        MQTTBusiness.getInstance(mActivity).setUserOperationCallBack(new MQTTBusiness.UserOperationCallBack() {
            @Override
            public void userOperation(UserOperationBean bean) {
                if (scanDialog!=null){
                    scanDialog.dismiss();
                }
                //订单详情生成
                createOrder(bean);
            }
        });
        AdvertUtil.showCountDownTime(60,mActivity.mBackTime, new TimeCallback() {
            @Override
            public boolean timeFinish() {
                if (scanDialog!=null&&scanDialog.isShowing()){
                    scanDialog.dismiss();
                }
                if (mPhoneLoginDialog!=null&&mPhoneLoginDialog.isShowing()){
                    mPhoneLoginDialog.dismiss();
                }
                if (orderDialog!=null&&orderDialog.isShowing()){
                    orderDialog.dismiss();
                }
                mActivity.replaceFragment(new MainFragment());
                return true;
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdvertUtil.disposable();
    }

    public static MainShopItemFragment newInstance(MainShopbean.DataBean bean) {
        Bundle args = new Bundle();
        MainShopItemFragment fragment = new MainShopItemFragment();
        args.putSerializable("bean", bean);
        fragment.setArguments(args);
        return fragment;
    }

    private void initVIew() {
        mBean = (MainShopbean.DataBean) getArguments().getSerializable("bean");
        Glide.with(mActivity)
            .load(mBean.img)
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(mHeadImg);
        mNameTv.setText(mBean.name);
        mPriceTv.setText(mBean.price + "元/斤");

        RiceMillHttp.get().deviceInfo(new Bean01Callback<DeviceInfoBean>() {
            @Override
            public void onSuccess(DeviceInfoBean bean) {
                warehouse = (int) Double.parseDouble(bean.data.warehouse);
                bag = Integer.parseInt(bean.data.bag);
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                showOneToast(message);
            }
        });
    }

    @OnClick({ R.id.jian_img, R.id.add_img, R.id.pay_ll, R.id.huiyan_ll, R.id.card_ll })
    public void onClick(View view) {
        switch (view.getId()) {
            //减
            case R.id.jian_img:
                if (number > 1) {
                    number--;
                    mNumberTv.setText(number + "斤");
                }
                break;
            //加
            case R.id.add_img:
                if (warehouse>number){
                    if (bag>number){
                        if (number<5){
                            number++;
                            mNumberTv.setText(number + "斤");
                        }
                    }else {
                        showOneToast("袋子库存不足，无法购买更多");
                    }
                }else {
                    showOneToast("稻谷库存不足，无法购买更多");
                }
                break;
            //微信和支付宝支付
            case R.id.pay_ll:
                //设置碾米精度
                portUtil.addC81(Integer.parseInt(mBean.level));
                showWaitingDialog("请稍候",false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissWaitingDialog();
                        mActivity.replaceFragment(OrderFragmen.newInstance(mBean, number));
                    }
                },2000);
                break;
            //会员支付
            case R.id.huiyan_ll:
                //设置碾米精度
                portUtil.addC81(Integer.parseInt(mBean.level));
                mPhoneLoginDialog = new PhoneLoginDialog(mActivity);
                mPhoneLoginDialog.show();
                mPhoneLoginDialog.setCallBack(new PhoneLoginDialog.CallBack() {
                    @Override
                    public void ic() {
                        //    扫描登录
                        scanDialog = new ScanDialog(mActivity);
                        scanDialog.show();
                    }

                    @Override
                    public void save(String phone, String code) {
                        login(phone, code);
                    }

                    @Override
                    public void sendCode(TextView tvSendCode,String phone) {
                        //发送验证码
                        RiceMillHttp.get().sendSms(phone, 2, new Bean01Callback<BaseBean>() {
                            @Override
                            public void onSuccess(BaseBean baseBean) {
                                CountDownTimer countDownTimer =
                                    new CountDownTimer(60000, 1000) {//第一个参数表示总时间，第二个参数表示间隔时间。

                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            int type = (int) (millisUntilFinished / 1000);
                                            if (tvSendCode!=null){
                                                tvSendCode.setText(type + "秒");
                                                tvSendCode.setClickable(false);
                                            }

                                        }

                                        @Override
                                        public void onFinish() {
                                            if (tvSendCode!=null){
                                                tvSendCode.setText("验证码");
                                                tvSendCode.setClickable(true);
                                            }
                                        }
                                    };
                                countDownTimer.start();
                            }

                            @Override
                            public void onFailure(String message, Throwable tr) {
                                showOneToast(message);
                            }
                        });
                    }
                });
                break;
            //IC卡支付
            case R.id.card_ll:
                //设置碾米精度
                portUtil.addC81(Integer.parseInt(mBean.level));
                showWaitingDialog("请稍候",false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissWaitingDialog();
                        mActivity.replaceFragment(CardPayFragment.newInstance(2, mBean, number));
                    }
                },2000);
                break;
        }
    }

    private void login(String phone, String code) {
        showWaitingDialog("", true);
        RiceMillHttp.get().login(phone, code, new Bean01Callback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean loginBean) {
                showOneToast(loginBean.msg);
                dismissWaitingDialog();
                LoginBean.DataBean dataBean = loginBean.data;
                //设置碾米精度
                //portUtil.addC81(Integer.parseInt(mBean.level));
                RiceMillHttp.get()
                    .pay_order(number, mBean.level, "3", mBean.goodsId, dataBean.token, "",
                        new Bean01Callback<PayOrderBean>() {
                            @Override
                            public void onSuccess(PayOrderBean payOrderBean) {
                                PayOrderBean.DataBean data = payOrderBean.data;
                                dismissWaitingDialog();

                                //订单详情生成
                                orderDialog = new OrderDialog(mActivity);
                                orderDialog.show();
                                orderDialog.setContext(dataBean, mBean, number, data);
                                orderDialog.setCallBack(new OrderDialog.CallBack() {
                                    @Override
                                    public void callBack() {
                                        showWaitingDialog("正在支付中...",true);
                                        RiceMillHttp.get().payOrder(data.order_number, new Bean01Callback<CommitBean>() {
                                            @Override
                                            public void onSuccess(CommitBean baseBean) {
                                                dismissWaitingDialog();
                                                //showOneToast(baseBean.msg);
                                                //mActivity.replaceFragment(new IntheshizpmentFragment());
                                            }

                                            @Override
                                            public void onFailure(String message, Throwable tr) {
                                                dismissWaitingDialog();
                                                showOneToast(message);
                                                HintDialog hintDialog = new HintDialog(mActivity);
                                                hintDialog.show();
                                                hintDialog.setCallBack(new HintDialog.CallBack() {
                                                    @Override
                                                    public void callBack() {
                                                        //2去到的会员信息
                                                        mActivity.replaceFragment(CardInfoFragment.newInstance(loginBean.data,2));

                                                    }
                                                });

                                            }
                                        });

                                        MQTTBusiness.getInstance(mActivity).setOrderCallBack(new OrderCallBack() {
                                            @Override
                                            public void outRiceResult(OrderOperationBean bean, String msg, int state) {
                                                //1收到推送 2开启成功 3完成碾米 4其他错误
                                                if (state == 1) {
                                                }else if (state == 2){
                                                    showOneToast(msg);
                                                    orderDialog.dismiss();
                                                    mActivity.replaceFragment(new IntheshizpmentFragment());
                                                }else if (state == 4){
                                                    showOneToast(msg);
                                                    orderDialog.dismiss();
                                                }
                                            }
                                        });

                                    }
                                });
                            }

                            @Override
                            public void onFailure(String message, Throwable tr) {
                                showOneToast(message);
                                dismissWaitingDialog();
                            }
                        });
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                showOneToast(message);
                dismissWaitingDialog();
            }
        });
    }

    private void createOrder(UserOperationBean bean){
        LoginBean.DataBean dataBean = new LoginBean.DataBean();
        dataBean.mobile = bean.mobile;
        dataBean.money = bean.money;
        dataBean.integral = bean.integral;
        dataBean.token = bean.token;
        //设置碾米精度
        //portUtil.addC81(Integer.parseInt(mBean.level));
        RiceMillHttp.get()
            .pay_order(number, mBean.level, "3", mBean.goodsId, bean.token, "",
                new Bean01Callback<PayOrderBean>() {
                    @Override
                    public void onSuccess(PayOrderBean payOrderBean) {
                        PayOrderBean.DataBean data = payOrderBean.data;
                        dismissWaitingDialog();

                        //订单详情生成
                        orderDialog = new OrderDialog(mActivity);
                        orderDialog.show();
                        orderDialog.setContext(dataBean, mBean, number, data);
                        orderDialog.setCallBack(new OrderDialog.CallBack() {
                            @Override
                            public void callBack() {
                                showWaitingDialog("正在支付中...",true);
                                RiceMillHttp.get().payOrder(data.order_number, new Bean01Callback<CommitBean>() {
                                    @Override
                                    public void onSuccess(CommitBean baseBean) {
                                        dismissWaitingDialog();
                                        //showOneToast(baseBean.msg);
                                        //mActivity.replaceFragment(new IntheshizpmentFragment());
                                    }

                                    @Override
                                    public void onFailure(String message, Throwable tr) {
                                        dismissWaitingDialog();
                                        showOneToast(message);
                                        HintDialog hintDialog = new HintDialog(mActivity);
                                        hintDialog.show();
                                        hintDialog.setCallBack(new HintDialog.CallBack() {
                                            @Override
                                            public void callBack() {
                                                //2去到的会员信息
                                                mActivity.replaceFragment(CardInfoFragment.newInstance(dataBean,2));

                                            }
                                        });

                                    }
                                });
                                MQTTBusiness.getInstance(mActivity).setOrderCallBack(new OrderCallBack() {
                                    @Override
                                    public void outRiceResult(OrderOperationBean bean, String msg, int state) {
                                        //1收到推送 2开启成功 3完成碾米 4其他错误
                                        if (state == 1) {
                                        }else if (state == 2){
                                            showOneToast(msg);
                                            orderDialog.dismiss();
                                            mActivity.replaceFragment(new IntheshizpmentFragment());
                                        }else if (state == 4){
                                            showOneToast(msg);
                                            orderDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message, Throwable tr) {
                        showOneToast(message);
                        dismissWaitingDialog();
                    }
                });
    }
}
