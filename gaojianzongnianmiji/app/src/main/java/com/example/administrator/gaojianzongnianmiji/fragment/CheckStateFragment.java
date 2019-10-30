package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.bean.CheckStateBean;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;

/**
 * Created by liuzhipeng on 2019/9/10.
 */

public class CheckStateFragment extends BaseFragment {

    @BindView(R.id.tv_hengfeng_temp)
    TextView mTvHengfengTemp;
    @BindView(R.id.tv_shufeng_temp)
    TextView mTvShufengTemp;
    @BindView(R.id.tv_gucang_temp)
    TextView mTvGucangTemp;
    @BindView(R.id.tv_check_weight)
    TextView mTvCheckWeight;
    @BindView(R.id.tv_device_state)
    TextView mTvDeviceState;
    @BindView(R.id.tv_baojing_state)
    TextView mTvBaojingState;
    private SerialPortUtil portUtil;
    private CheckStateBean mCheckStateBean = new CheckStateBean();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_state;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.mBackTime.setVisibility(View.GONE);
        mActivity.mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(new OperationHomeFragment());
            }
        });
        initSerialPort();
    }

    private void initSerialPort() {
        String baojing_state = "正常";
        String heng = PrefUtil.getDefault().getString(Constant.Hengfeng_temp,"");
        String shu = PrefUtil.getDefault().getString(Constant.Shufeng_temp,"");
        String gucang = PrefUtil.getDefault().getString(Constant.Gucang_temp,"");
        String weight = PrefUtil.getDefault().getString(Constant.Device_Weight,"");
        int alarmstate = PrefUtil.getDefault().getInt(Constant.Alarm_state,0);
        int elevatorState_state = PrefUtil.getDefault().getInt(Constant.ElevatorState_state,0);
        mTvHengfengTemp.setText("当前横封温度为："+heng+"℃");
        mTvShufengTemp.setText("当前竖封温度为："+shu+"℃");
        mTvGucangTemp.setText("当前谷仓温度为："+gucang+"℃");
        mTvCheckWeight.setText("重量为："+weight+"g");
        mTvDeviceState.setText("设备状态：空闲");

        if (alarmstate==0){
            //heartbeatBean.status = 1;
        }else if (alarmstate==1){
            baojing_state = "温度超温报警";
        }else if (alarmstate==2){
            baojing_state = "加热超时报警";
        }else if (alarmstate==3){
            baojing_state = "碾米超时报警";
        }else if (alarmstate==4){
            baojing_state = "下米超时报警";
        }else if (alarmstate==5){
            baojing_state = "拉膜超时报警";
        }else if (alarmstate==6){
            baojing_state = "封膜超时报警";
        }else if (alarmstate==7){
            baojing_state = "送膜超时报警";
        }else if (alarmstate==8){
            baojing_state = "主动力电机过流报警";
        }
        if (elevatorState_state==0){
        }else {
            baojing_state = "提升机送谷超时报警";
        }
        mTvBaojingState.setText("当前报警状态："+baojing_state);
    }
}
