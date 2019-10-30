package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.utils.MyToastUtils;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.licheedev.myutils.LogPlus;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class DetectionFragment extends BaseFragment {

    @BindView(R.id.out_tv)
    TextView mOutTv;
    @BindView(R.id.main_power_tv)
    TextView mMainPowerTv;
    @BindView(R.id.song_liao_tv)
    TextView mSongLiaoTv;
    @BindView(R.id.la_mo_tv)
    TextView mLaMoTv;
    @BindView(R.id.song_mo_tv)
    TextView mSongMoTv;
    @BindView(R.id.puff_tv)
    TextView mPuffTv;
    @BindView(R.id.go_rice_tv)
    TextView mGoRiceTv;
    @BindView(R.id.package_tv)
    TextView mPackageTv;
    private SerialPortUtil portUtil;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detection;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.mBackTime.setVisibility(View.GONE);
        portUtil = SerialPortUtil.getInstance();
        portUtil.setCheckResult(new SerialPortUtil.checkResult() {
            @Override
            public void receiveResult(String data) {
                String command = data.substring(0, 4);
                runOnUiThread(() -> {
                    if (!command.equals("0902")) {
                        dismissWaitingDialog();
                    }
                    switch (command) {
                        case "09CA":
                            String sign = data.substring(4, 6);
                            switch (sign) {
                                case "02"://交易请求回复
                                    String w = data.substring(6, 14);
                                    String r = data.substring(22, 24);
                                    break;
                                case "03"://
                                    int wegiht = Integer.parseInt(data.substring(22, 30), 16);
                                    //mTvOutWeight.setText("出米重量:" + wegiht + "克");
                                    break;
                                case "04"://交易完成告诉后台
                                    int wa = Integer.parseInt(data.substring(6, 14), 16);//系统机交易流水
                                    int z = Integer.parseInt(data.substring(14, 22), 16);//终端交易流水
                                    int wt = Integer.parseInt(data.substring(22, 30), 16);//实时出米重量
                                    String rt = data.substring(30, 32);//上报结果
                                    portUtil.addCA05(wa, z);
                                    //mTvOutWeight.setText("出米重量:" + wt + "克出米完成");
                                    break;
                            }
                            break;
                        case "0C83":
                            LogPlus.e("0C83命令返回：=========="+data);
                            showOneToast("命令已发出");
                            break;
                    }
                });
            }

            @Override
            public void sendErrorListener(String sendCmd) {
                runOnUiThread(() -> {
                    dismissWaitingDialog();
                    MyToastUtils.showMyToast(sendCmd + "超时");
                });
            }
        });
        mActivity.mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //portUtil.addC83(1,0);
                //portUtil.addC83(2,0);
                //portUtil.addC83(3,0);
                //portUtil.addC83(4,0);
                //portUtil.addC83(5,0);
                //portUtil.addC83(6,0);
                //portUtil.addC83(7,0);
                mActivity.replaceFragment(new OperationHomeFragment());
            }
        });
    }
    private int oldWater;
    private boolean isOpen1 = false;private boolean isOpen2 = false;private boolean isOpen3 = false;
    private boolean isOpen4 = false;private boolean isOpen5 = false;private boolean isOpen6 = false;
    private boolean isOpen7 = false;private boolean isOpen8 = false;

    @OnClick({ R.id.out_tv, R.id.main_power_tv, R.id.song_liao_tv, R.id.la_mo_tv,R.id.song_mo_tv,R.id.puff_tv,
        R.id.go_rice_tv,R.id.package_tv,R.id.fan_tv })
    public void onClick(View view) {
        switch (view.getId()) {
            //开始碾米
            case R.id.out_tv:
                if (oldWater == 0) {
                    oldWater = 2147483647;
                } else {
                    oldWater = 0;
                }
                portUtil.addCA(oldWater, 500,1);
                break;
            //主动力电机
            case R.id.main_power_tv:
                if (!isOpen1){
                    isOpen1 = true;
                    portUtil.addC83(1,1);
                }else {
                    isOpen1 = false;
                    portUtil.addC83(1,0);
                }
                break;
            //送料电机
            case R.id.song_liao_tv:
                if (!isOpen2){
                    isOpen2 = true;
                    portUtil.addC83(2,1);
                }else {
                    isOpen2 = false;
                    portUtil.addC83(2,0);
                }
                break;
            //拉膜电机
            case R.id.la_mo_tv:
                if (!isOpen3){
                    isOpen3 = true;
                    portUtil.addC83(3,1);
                }else {
                    isOpen3 = false;
                    //portUtil.addC83(3,0);
                }
                break;
            //送膜电机
            case R.id.song_mo_tv:
                if (!isOpen4){
                    isOpen4 = true;
                    portUtil.addC83(5,1);
                }else {
                    isOpen4 = false;
                    //portUtil.addC83(5,0);
                }
                break;
            //吹糠电机
            case R.id.puff_tv:
                if (!isOpen5){
                    isOpen5 = true;
                    portUtil.addC83(6,1);
                }else {
                    isOpen5 = false;
                    portUtil.addC83(6,0);
                }
                break;
            //下米电机
            case R.id.go_rice_tv:
                if (!isOpen6){
                    isOpen6 = true;
                    portUtil.addC83(7,1);
                }else {
                    isOpen6 = false;
                    portUtil.addC83(7,0);
                }
                break;
            //包装电机
            case R.id.package_tv:
                if (!isOpen7){
                    isOpen7 = true;
                    portUtil.addC83(8,1);
                }else {
                    isOpen7 = false;
                    portUtil.addC83(8,0);
                }
                break;
                //风扇电机
            case R.id.fan_tv:
                if (!isOpen8){
                    isOpen8= true;
                    portUtil.addC83(10,1);
                }else {
                    isOpen8= false;
                    portUtil.addC83(10,0);
                }
                break;
        }
    }

}
