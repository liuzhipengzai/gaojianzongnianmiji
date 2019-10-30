package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.bean.OperationBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class LoginFragment extends BaseFragment {
    @BindView(R.id.phone_et)
    EditText mPhoneEt;
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    @BindView(R.id.tv_password)
    EditText mTvPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdvertUtil.showCountDownTime(60,mActivity.mBackTime, new TimeCallback() {
            @Override
            public boolean timeFinish() {
                mActivity.replaceFragment(new MainFragment());
                return true;
            }
        });
    }

    @OnClick(R.id.save_tv)
    public void onClick() {
        String phone = mPhoneEt.getText().toString();
        String password = mTvPassword.getText().toString();
        //phone = "18575416041";
        //password = "123456";
        //phone = "32132";
        //password = "123456";
        //if (phone.length()<11){
        //    showOneToast("请输入正确的手机号");
        //    return;
        //}
        if (password.equals("")){
            showOneToast("请输入密码");
            return;
        }
        showWaitingDialog("", true);
        RiceMillHttp.get()
            .replenish_land(phone, password, new Bean01Callback<OperationBean>() {
                @Override
                public void onSuccess(OperationBean rechargeBean) {
                    dismissWaitingDialog();
                    PrefUtil.getDefault().saveString(Constant.operation_land_token,rechargeBean.data.token);
                    showOneToast(rechargeBean.msg);
                    mActivity.replaceFragment(new OperationHomeFragment());
                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    dismissWaitingDialog();
                    showOneToast(message);
                }
            });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdvertUtil.disposable();
    }

}
