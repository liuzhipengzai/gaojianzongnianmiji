package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.utils.DialogUtil;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.bean.IntegralBean;
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;
import com.example.administrator.gaojianzongnianmiji.bean.PayOrderBean;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class OrderDialog extends Dialog {
    @BindView(R.id.phone_tv)
    TextView mPhoneTv;
    @BindView(R.id.monney_tv)
    TextView mMonneyTv;
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
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    @BindView(R.id.cancel_tv)
    TextView mCancelTv;
    @BindView(R.id.integral_tv)
    TextView mIntegralTv;
    private CallBack mCallBack;

    public OrderDialog(@NonNull Context context) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_order);
        DialogUtil.setGravity(this, Gravity.CENTER);
        ButterKnife.bind(this);
    }

    public void setContext(LoginBean.DataBean dataBean, MainShopbean.DataBean bean, int number,
        PayOrderBean.DataBean data) {
        mPhoneTv.setText((TextUtils.isEmpty(dataBean.idcard) ? dataBean.mobile : dataBean.idcard));
        mMonneyTv.setText("￥" + dataBean.money);
        mNameTv.setText(bean.name);
        mPriceTv.setText("￥" + bean.price + "/斤");
        mNumberTv.setText(number + "斤");
        mDeductionTv.setText("￥" + data.integral_price);
        mDiscountsTv.setText("￥" + data.integral_price);
        mAllTv.setText("￥" + data.pay_price);

        RiceMillHttp.get().integral(new Bean01Callback<IntegralBean>() {
            @Override
            public void onSuccess(IntegralBean integralBean) {
                String integral = dataBean.integral;
                if (integral==null){
                    integral = "0";
                }
                mIntegralTv.setText("( 注："
                    + integralBean.data.dh_score
                    + "积分抵扣"
                    + integralBean.data.dh_money
                    + "元账户当前积分为"+integral+"积分）");
                //+ "元账户当前积分为"+dataBean.integral+"积分）");
            }

            @Override
            public void onFailure(String message, Throwable tr) {

            }
        });
    }

    @OnClick({ R.id.save_tv, R.id.cancel_tv })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_tv:
                mCallBack.callBack();
                dismiss();
                break;
            case R.id.cancel_tv:
                dismiss();
                break;
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void callBack();
    }
}
