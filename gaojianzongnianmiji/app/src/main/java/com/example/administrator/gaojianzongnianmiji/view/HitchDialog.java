package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.dlc.commonlibrary.utils.DialogUtil;
import com.example.administrator.gaojianzongnianmiji.R;

/**
 * Created by liuzhipeng on 2019/9/24.
 */

public class HitchDialog extends Dialog {

    @BindView(R.id.tv_htich_result)
    TextView mTvHtichResult;
    @BindView(R.id.tv_mag)
    TextView mTvMag;

    public HitchDialog(@NonNull Context context) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_hitch);
        DialogUtil.setGravity(this, Gravity.CENTER);
        ButterKnife.bind(this);
    }

    public void show(String remarks,String phone) {
        super.show();
        mTvHtichResult.setText("故障原因："+remarks);
        //String mobile =
        //    PrefUtil.getDefault().getString(Constant.About_Mobile, "123456");
        mTvMag.setText("请联系管理员："+phone);
    }
}
