package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.bean.CommitBean;
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;
import com.example.administrator.gaojianzongnianmiji.bean.PayOrderBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.utils.OrderCallBack;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationBean;
import com.example.administrator.gaojianzongnianmiji.view.HintDialog;
import com.example.administrator.gaojianzongnianmiji.view.OrderDialog;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class CardPayFragment extends BaseFragment {
    @BindView(R.id.edit_et)
    EditText mEditEt;
    private int mType;
    private SerialPortUtil portUtil;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_card_pay;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        portUtil = SerialPortUtil.getInstance();
        mType = getArguments().getInt("type");
        initEditText();
        MQTTBusiness.getInstance(mActivity).setOrderCallBack(new OrderCallBack() {
            @Override
            public void outRiceResult(OrderOperationBean bean, String msg, int state) {
                //1收到推送 2开启成功 3完成碾米 4其他错误
                if (state == 1) {
                } else if (state == 2) {
                    showOneToast(msg);
                    mActivity.replaceFragment(new IntheshizpmentFragment());
                } else if (state == 4) {
                    showOneToast(msg);
                }
            }
        });

        AdvertUtil.showCountDownTime(60,mActivity.mBackTime, new TimeCallback() {
            @Override
            public boolean timeFinish() {
                mActivity.replaceFragment(new MainFragment());
                return true;
            }
        });
        //save("6666");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdvertUtil.disposable();
    }

    public static CardPayFragment newInstance(int type, MainShopbean.DataBean bean, int number) {
        Bundle args = new Bundle();
        CardPayFragment fragment = new CardPayFragment();
        args.putInt("type", type);
        args.putSerializable("bean", bean);
        args.putInt("number", number);
        fragment.setArguments(args);
        return fragment;
    }

    private long time = 0;

    private void initEditText() {
        mEditEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //监听回车事件
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (SystemClock.uptimeMillis() - time < 1000) {
                        return true;
                    }
                    time = SystemClock.uptimeMillis();
                    Log.e("MainA", "读卡=" + mEditEt.getText().toString());
                    String card = mEditEt.getText().toString();
                    switch (mType) {
                        //进去Ic卡的信息
                        case 1:
                            save(card);
                            break;
                        //去生成详情
                        case 2:
                            initView(card);
                            break;
                    }
                    mEditEt.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private void save(String card) {
        showWaitingDialog("", true);
        RiceMillHttp.get().apkuserInfo(card, new Bean01Callback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean loginBean) {
                //showOneToast(loginBean.msg);
                dismissWaitingDialog();
                mActivity.replaceFragment(CardInfoFragment.newInstance(loginBean.data, mType));
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                dismissWaitingDialog();
                showOneToast(message);
            }
        });
    }

    private void initView(String card) {
        int number = getArguments().getInt("number");
        MainShopbean.DataBean bean = (MainShopbean.DataBean) getArguments().getSerializable("bean");
        showWaitingDialog("", true);
        RiceMillHttp.get().apkuserInfo(card, new Bean01Callback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean loginBean) {
                LoginBean.DataBean dataBean = loginBean.data;
                //设置碾米精度
                //portUtil.addC81(Integer.parseInt(bean.level));
                RiceMillHttp.get()
                    .pay_order(number, bean.level, "4", bean.goodsId, "", dataBean.idcard,
                        new Bean01Callback<PayOrderBean>() {
                            @Override
                            public void onSuccess(PayOrderBean payOrderBean) {
                                PayOrderBean.DataBean data = payOrderBean.data;
                                dismissWaitingDialog();

                                //订单详情生成
                                OrderDialog orderDialog = new OrderDialog(mActivity);
                                orderDialog.show();
                                orderDialog.setContext(dataBean, bean, number, data);
                                orderDialog.setCallBack(new OrderDialog.CallBack() {
                                    @Override
                                    public void callBack() {
                                        showWaitingDialog("正在支付中...", true);
                                        RiceMillHttp.get()
                                            .payOrder(data.order_number,
                                                new Bean01Callback<CommitBean>() {
                                                    @Override
                                                    public void onSuccess(CommitBean baseBean) {
                                                        dismissWaitingDialog();
                                                        //showOneToast(baseBean.msg);
                                                        //mActivity.replaceFragment(new IntheshizpmentFragment());
                                                    }

                                                    @Override
                                                    public void onFailure(String message,
                                                        Throwable tr) {
                                                        dismissWaitingDialog();
                                                        showOneToast(message);
                                                        HintDialog hintDialog =
                                                            new HintDialog(mActivity);
                                                        hintDialog.show();
                                                        hintDialog.setCallBack(
                                                            new HintDialog.CallBack() {
                                                                @Override
                                                                public void callBack() {
                                                                    mActivity.replaceFragment(
                                                                        CardInfoFragment.newInstance(
                                                                            loginBean.data, 1));
                                                                }
                                                            });
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
                dismissWaitingDialog();
                showOneToast(message);
            }
        });
    }
}
