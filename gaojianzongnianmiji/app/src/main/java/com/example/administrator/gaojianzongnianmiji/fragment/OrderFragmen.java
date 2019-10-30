package com.example.administrator.gaojianzongnianmiji.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.bean.CommitBean;
import com.example.administrator.gaojianzongnianmiji.bean.IntegralBean;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;
import com.example.administrator.gaojianzongnianmiji.bean.PayOrderBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.utils.OrderCallBack;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationBean;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class OrderFragmen extends BaseFragment {
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.price_tv)
    TextView mPriceTv;
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.deduction_tv)
    TextView mDeductionTv;
    @BindView(R.id.discounts_tv)
    TextView mDiscountsTv;
    @BindView(R.id.all_tv)
    TextView mAllTv;
    @BindView(R.id.pay_tv)
    TextView mPayTv;
    @BindView(R.id.code_tv)
    ImageView mCodeTv;
    @BindView(R.id.integral_tv)
    TextView mIntegralTv;
    private SerialPortUtil portUtil;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        portUtil = SerialPortUtil.getInstance();
        initView();
        MQTTBusiness.getInstance(mActivity).setOrderCallBack(new OrderCallBack() {
            @Override
            public void outRiceResult(OrderOperationBean bean, String msg, int state) {
                //1收到推送 2开启成功 3完成碾米 4其他错误
                if (state == 1) {
                }else if (state == 2){
                    showOneToast(msg);
                    mActivity.replaceFragment(new IntheshizpmentFragment());
                }else if (state == 4){
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
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdvertUtil.disposable();
    }

    public static OrderFragmen newInstance(MainShopbean.DataBean bean, int number) {
        Bundle args = new Bundle();
        OrderFragmen fragment = new OrderFragmen();
        args.putInt("number", number);
        args.putSerializable("bean", bean);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        int number = getArguments().getInt("number");
        MainShopbean.DataBean bean = (MainShopbean.DataBean) getArguments().getSerializable("bean");
        showWaitingDialog("", true);
        //设置碾米精度
        //portUtil.addC81(Integer.parseInt(bean.level));
        RiceMillHttp.get()
            .pay_order(number, bean.level, "2", bean.goodsId, "", "",
                new Bean01Callback<PayOrderBean>() {
                    @Override
                    public void onSuccess(PayOrderBean payOrderBean) {
                        dismissWaitingDialog();
                        mNameTv.setText(bean.name);
                        mPriceTv.setText("￥" + bean.price + "/斤");
                        mNumberTv.setText(number + "斤");
                        mDeductionTv.setText("￥" + 0);
                        mDiscountsTv.setText("￥" + 0);
                        mAllTv.setText("￥" + payOrderBean.data.pay_price);
                        mPayTv.setText("￥" + payOrderBean.data.pay_price);
                        RiceMillHttp.get().payOrder(payOrderBean.data.order_number, new Bean01Callback<CommitBean>() {
                            @Override
                            public void onSuccess(CommitBean baseBean) {
                                Bitmap bitmap =
                                    QRCodeEncoder.syncEncodeQRCode(baseBean.data.url, BGAQRCodeUtil.dp2px(mActivity, 400), R.color.black,
                                        null);
                                mCodeTv.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onFailure(String message, Throwable tr) {
                                Bitmap bitmap =
                                    QRCodeEncoder.syncEncodeQRCode("二维码异常，请重试", BGAQRCodeUtil.dp2px(mActivity, 400), R.color.black,
                                        null);
                                mCodeTv.setImageBitmap(bitmap);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message, Throwable tr) {
                        showOneToast(message);
                        dismissWaitingDialog();
                    }
                });

        RiceMillHttp.get().integral(new Bean01Callback<IntegralBean>() {
            @Override
            public void onSuccess(IntegralBean integralBean) {
                mIntegralTv.setText("( 注："
                    + integralBean.data.dh_money
                    + "积分抵扣"
                    + integralBean.data.dh_score
                    + "元 )");
            }

            @Override
            public void onFailure(String message, Throwable tr) {

            }
        });


    }
}
