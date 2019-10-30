package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.dlc.commonlibrary.utils.DialogUtil;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;

/**
 * Created by liuwenzhuo on 2019/7/3.
 */

public class MacNoDialog extends Dialog {
    @BindView(R.id.tv_macno)
    TextView mTvMacno;
    private Activity mActivity;

    public MacNoDialog(@NonNull Context context) {
        super(context, R.style.MyDialogStyle);
        setContentView(R.layout.dialog_macno);
        DialogUtil.setGravity(this, Gravity.CENTER);
        ButterKnife.bind(this);
        mTvMacno.setText("设备号：" + Constant.ANDROID_ID);
        mActivity = (Activity) context;
    }

    public boolean isFinishing() {
        if (mActivity != null) {
            return mActivity.isFinishing();
        }
        return false;
    }
}
