package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dlc.commonlibrary.utils.DialogUtil;
import com.example.administrator.gaojianzongnianmiji.R;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class HintDialog extends Dialog {
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    @BindView(R.id.cancel_tv)
    TextView mCancelTv;
    private  CallBack mCallBack;
    public HintDialog(@NonNull Context context) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_hint);
        DialogUtil.setGravity(this, Gravity.CENTER);
        ButterKnife.bind(this);
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

    public  void  setCallBack(CallBack callBack){
        mCallBack =callBack;
    }

    public  interface   CallBack{
        void  callBack();
    }
}
