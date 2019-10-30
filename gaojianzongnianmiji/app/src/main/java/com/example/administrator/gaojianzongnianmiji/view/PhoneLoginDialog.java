package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dlc.commonlibrary.utils.DialogUtil;
import cn.dlc.commonlibrary.utils.ToastUtil;
import com.example.administrator.gaojianzongnianmiji.R;

/**
 * Created by lixukang   on  2019/6/17.
 */

public class PhoneLoginDialog extends Dialog {
    @BindView(R.id.phone_et)
    EditText mPhoneEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;
    @BindView(R.id.code_tv)
    TextView mCodeTv;
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    private final Context mContext;
    @BindView(R.id.scan_tv)
    TextView mScanTv;
    private CallBack mCallBack;

    public PhoneLoginDialog(@NonNull Context context) {
        super(context, R.style.CommonDialogStyle);
        mContext = context;
        setContentView(R.layout.dialog_phone_login);
        DialogUtil.setGravity(this,Gravity.CENTER);
        ButterKnife.bind(this);
    }

    @OnClick({ R.id.code_tv, R.id.save_tv, R.id.scan_tv })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.code_tv:
                String phone = mPhoneEt.getText().toString();
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    ToastUtil.show(mContext, "请输入手机号码");
                    return;
                }
                mCallBack.sendCode(mCodeTv,phone);
                break;
            case R.id.save_tv:
                String phone1 = mPhoneEt.getText().toString();
                String code = mCodeEt.getText().toString();
                if (TextUtils.isEmpty(phone1) || phone1.length() != 11) {
                    ToastUtil.show(mContext, "请输入手机号码");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(mContext, "请输入验证码");
                    return;
                }
                if (mCallBack != null) {
                    mCallBack.save(phone1, code);
                    dismiss();
                }
                break;
            case R.id.scan_tv:
                if (mCallBack != null) {
                    mCallBack.ic();
                    dismiss();
                }
                break;
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {

        void ic();

        void save(String phone, String code);

        void sendCode(TextView tvSendCode,String phone);
    }
}
