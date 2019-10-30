package com.example.administrator.gaojianzongnianmiji.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.bean.PayRechargeOrderBean;
import com.example.administrator.gaojianzongnianmiji.bean.RechargeBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.view.ResultDialog;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class CardPayDetailsFragment extends BaseFragment {
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.phone_tv)
    TextView mPhoneTv;
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.deduction_tv)
    TextView mDeductionTv;
    @BindView(R.id.pay_tv)
    TextView mPayTv;
    @BindView(R.id.code_tv)
    ImageView mCodeTv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_card_pay_details;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        ResultDialog dialog = new ResultDialog(mActivity);
        MQTTBusiness.getInstance(mActivity)
            .setPayRechargeCallBack(new MQTTBusiness.PayRechargeCallBack() {
                @Override
                public void rechargeSuccess() {
                    dialog.setContent("充值成功！", R.mipmap.ic_ichenggong);
                    dialog.show();
                }

                @Override
                public void rechargeFail() {
                    dialog.setContent("充值失败！", R.mipmap.ic_shibai);
                    dialog.show();
                }
            });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mActivity.replaceFragment(new MainFragment());
            }
        });
        AdvertUtil.showCountDownTime(60,mActivity.mBackTime, new TimeCallback() {
            @Override
            public boolean timeFinish() {
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                //mActivity.replaceFragment(new MainFragment());
                return true;
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdvertUtil.disposable();
    }

    public static CardPayDetailsFragment newInstance(LoginBean.DataBean bean, int id, int type) {
        Bundle args = new Bundle();
        CardPayDetailsFragment fragment = new CardPayDetailsFragment();
        args.putSerializable("bean", bean);
        args.putInt("id", id);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        LoginBean.DataBean bean = (LoginBean.DataBean) getArguments().getSerializable("bean");
        int id = getArguments().getInt("id");
        int type = getArguments().getInt("type");
        switch (type) {
            //1是刷卡信息
            case 1:
                mTitleTv.setText("IC卡信息");
                mNameTv.setText("IC卡号：");
                break;
            //2是会员登录信息
            case 2:
                mTitleTv.setText("会员信息");
                mNameTv.setText("手机号：");
                break;
        }
        mPhoneTv.setText((TextUtils.isEmpty(bean.idcard) ? bean.mobile : bean.idcard));
        showWaitingDialog("", true);
        RiceMillHttp.get().apkRecharge(bean, id, type, new Bean01Callback<RechargeBean>() {
            @Override
            public void onSuccess(RechargeBean rechargeBean) {
                dismissWaitingDialog();
                //showOneToast(rechargeBean.msg);

                mNumberTv.setText("￥" + rechargeBean.data.money);
                mDeductionTv.setText("￥" + rechargeBean.data.money);
                mPayTv.setText("￥" + rechargeBean.data.money);

                RiceMillHttp.get()
                    .payRechargeOrder(rechargeBean.data.order_number,
                        new Bean01Callback<PayRechargeOrderBean>() {
                            @Override
                            public void onSuccess(PayRechargeOrderBean payRechargeOrderBean) {
                                Bitmap bitmap =
                                    QRCodeEncoder.syncEncodeQRCode(payRechargeOrderBean.data.url,
                                        BGAQRCodeUtil.dp2px(mActivity, 400), R.color.black, null);
                                mCodeTv.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onFailure(String message, Throwable tr) {
                                showOneToast(message);
                                Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode("二维码获取异常，请重试",
                                    BGAQRCodeUtil.dp2px(mActivity, 400), R.color.black, null);
                                mCodeTv.setImageBitmap(bitmap);
                            }
                        });
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                dismissWaitingDialog();
                showOneToast(message);
            }
        });

        //Bitmap bitmap =
        //    QRCodeEncoder.syncEncodeQRCode("王浩", BGAQRCodeUtil.dp2px(mActivity, 400), R.color.black,
        //        null);
        //mCodeTv.setImageBitmap(bitmap);

        //ResultDialog dialog = new ResultDialog(mActivity);
        //dialog.setContent("充值成功！", R.mipmap.ic_ichenggong);
        ////dialog.setContent("充值失败！",R.mipmap.ic_shibai);
        //dialog.show();
    }
}
