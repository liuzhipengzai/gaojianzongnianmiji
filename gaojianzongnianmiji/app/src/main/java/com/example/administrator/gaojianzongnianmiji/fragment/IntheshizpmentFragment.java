package com.example.administrator.gaojianzongnianmiji.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.utils.OrderCallBack;
import com.example.administrator.gaojianzongnianmiji.utils.bean.OrderOperationBean;
import com.example.administrator.gaojianzongnianmiji.view.ResultDialog;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class IntheshizpmentFragment extends BaseFragment {
    @BindView(R.id.tv_rice_state)
    TextView mTvRiceState;
    String ricestate = "正在碾米，请等待...";
    private static final int MSG_BED_TDATA = 0x0012;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BED_TDATA:
                    mTvRiceState.setText(ricestate);
                    break;
            }
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_in_the_shizpment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ResultDialog dialog = new ResultDialog(mActivity);
        mActivity.mLlBack.setVisibility(View.GONE);
        MQTTBusiness.getInstance(mActivity).setOrderCallBack(new OrderCallBack() {
            @Override
            public void outRiceResult(OrderOperationBean bean, String msg, int state) {
                //1收到推送 2开启成功 3完成碾米 4其他错误 5碾米过程
                if (state == 1) {

                } else if (state == 3) {
                    showOneToast(msg);
                    dialog.setContent("出米完成！请取走\n" + "欢迎下次光临", R.mipmap.ic_ichenggong);
                    dialog.show();
                    AdvertUtil.showCountDownTime(15,mActivity.mBackTime, new TimeCallback() {
                        @Override
                        public boolean timeFinish() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            return true;
                        }
                    });
                } else if (state == 4) {
                    String mobile =
                        PrefUtil.getDefault().getString(Constant.About_Mobile, "123456");
                    dialog.setContent(msg+"\n" + "请联系客服" + mobile, R.mipmap.ic_shibai);
                    dialog.show();
                    AdvertUtil.showCountDownTime(60, mActivity.mBackTime,new TimeCallback() {
                        @Override
                        public boolean timeFinish() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            return true;
                        }
                    });
                } else if (state == 5) {
                    if (msg.equals("1")){
                        ricestate = "正在碾米，请等待...";
                    }else if (msg.equals("2")){
                        ricestate = "正在加热包装片，等待包装，请等待...";
                    }
                    else if (msg.equals("3")){
                        ricestate = "开始拉膜，请等待...";
                    }
                    else if (msg.equals("4")){
                        ricestate = "开始封袋，请等待...";
                    }
                    else if (msg.equals("5")){
                        ricestate = "开始送膜，请等待...";
                    }
                    if (handler != null) {
                        handler.sendEmptyMessage(MSG_BED_TDATA);
                        //handler.sendEmptyMessageDelayed(MSG_BED_TDATA, 2000);
                    }
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mActivity.replaceFragment(new MainFragment());
            }
        });
    }
}
