package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseBean;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.bean.AlarmBean;
import com.example.administrator.gaojianzongnianmiji.utils.MyToastUtils;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.licheedev.myutils.LogPlus;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

/**
 * Created by liuzhipeng on 2019/9/10.
 */

public class AlarmFragment extends BaseFragment {

    @BindView(R.id.tv_last1)
    TextView mTvLast1;
    @BindView(R.id.et_set1)
    EditText mEtSet1;
    @BindView(R.id.tv_last2)
    TextView mTvLast2;
    @BindView(R.id.et_set2)
    EditText mEtSet2;
    @BindView(R.id.tv_last4)
    TextView mTvLast4;
    @BindView(R.id.et_set4)
    EditText mEtSet4;
    @BindView(R.id.tv_last5)
    TextView mTvLast5;
    @BindView(R.id.et_set5)
    EditText mEtSet5;
    @BindView(R.id.tv_last6)
    TextView mTvLast6;
    @BindView(R.id.et_set6)
    EditText mEtSet6;
    @BindView(R.id.tv_last7)
    TextView mTvLast7;
    @BindView(R.id.et_set7)
    EditText mEtSet7;
    @BindView(R.id.tv_last8)
    TextView mTvLast8;
    @BindView(R.id.et_set8)
    EditText mEtSet8;
    private SerialPortUtil portUtil;
    private Handler mHandler = new Handler();
    private int etset1, etset2, etset3, etset4, etset5, etset6, etset7, etset8;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_alarm;
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

        portUtil = SerialPortUtil.getInstance();
        initData();
        initSerialPort();
    }



    private void initSerialPort() {

        portUtil.setAlarmResult(new SerialPortUtil.alarmResult() {
            @Override
            public void receiveResult(String data) {
                String command = data.substring(0, 4);
                runOnUiThread(() -> {
                    if (!command.equals("0902")) {
                        dismissWaitingDialog();
                    }
                    switch (command) {
                        case "0C16":
                            int a = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast1.setText(a);
                            break;
                        case "0C18":
                            int b = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast2.setText(b);
                            break;
                        case "0C1A":
                            int c = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast4.setText(c);
                            break;
                        case "0C1C":
                            int d = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast5.setText(d);
                            break;
                        case "0C1E":
                            int e = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast6.setText(e);
                            break;
                        case "0C20":
                            int f = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast7.setText(f);
                            break;
                        case "0C22":
                            int g = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast8.setText(g);
                            break;
                        case "0C17":
                            MyToastUtils.showMyToast("设置温度超温报警成功");
                            int h = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast1.setText(h+"");
                            updateAlarm(1,h);
                            break;
                        case "0C19":
                            MyToastUtils.showMyToast("设置加热超时报警成功");
                            int i = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast2.setText(i+"");
                            updateAlarm(2,i);
                            break;
                        case "0C1B":
                            MyToastUtils.showMyToast("设置下米超时报警时间成功");
                            int j = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast4.setText(j+"");
                            updateAlarm(3,j);
                            break;
                        case "0C1D":
                            MyToastUtils.showMyToast("设置拉膜超时报警时间成功");
                            int k = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast5.setText(k+"");
                            updateAlarm(4,k);
                            break;
                        case "0C1F":
                            MyToastUtils.showMyToast("设置封膜超时报警时间成功");
                            int l = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast6.setText(l+"");
                            updateAlarm(5,l);
                            break;
                        case "0C21":
                            MyToastUtils.showMyToast("设置送谷超时报警时间成功");
                            int m = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast7.setText(m+"");
                            updateAlarm(6,m);
                            break;
                        case "0C23":
                            MyToastUtils.showMyToast("设置送膜超时报警时间");
                            int n = Integer.parseInt(data.substring(4, 8), 16);
                            mTvLast8.setText(n+"");
                            updateAlarm(7,n);
                            break;
                        case "0C15":
                            int o = Integer.parseInt(data.substring(4,6), 16);
                            if (o==0){
                                MyToastUtils.showMyToast("解除提升机超时报警成功");
                            }else {
                                MyToastUtils.showMyToast("解除提升机超时报警失败");
                            }
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

    }

    @OnClick({
        R.id.tv_set1, R.id.tv_set2, R.id.tv_set4, R.id.tv_set5, R.id.tv_set6,
        R.id.tv_set7, R.id.tv_set8, R.id.tv_recover1, R.id.tv_recover2,
        R.id.tv_recover4, R.id.tv_recover5, R.id.tv_recover6, R.id.tv_recover7, R.id.tv_recover8,R.id.tv_remove_alarm
    })
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.tv_set1:
                String set1 = mEtSet1.getText().toString();
                if (TextUtils.isEmpty(set1)) {
                    MyToastUtils.showMyToast("请输入正确的值0~999");
                    return;
                }
                int set11 = Integer.parseInt(mEtSet1.getText().toString());
                if (set11 < 0 || set11 > 999) {
                    MyToastUtils.showMyToast("请输入正确的值0~999");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC17(set11);
                break;
            case R.id.tv_set2:
                String set2 = mEtSet2.getText().toString();
                if (TextUtils.isEmpty(set2)) {
                    MyToastUtils.showMyToast("请输入正确的值0~900");
                    return;
                }
                int set12 = Integer.parseInt(mEtSet2.getText().toString());
                if (set12 < 0 || set12 > 900) {
                    MyToastUtils.showMyToast("请输入正确的值0~900");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC19(set12);
                break;
            //case R.id.tv_set3:
            //    break;
            case R.id.tv_set4:
                String set4 = mEtSet4.getText().toString();
                if (TextUtils.isEmpty(set4)) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                int set14 = Integer.parseInt(mEtSet4.getText().toString());
                if (set14 < 0 || set14 > 100) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC1B(set14);
                break;
            case R.id.tv_set5:
                String set5 = mEtSet5.getText().toString();
                if (TextUtils.isEmpty(set5)) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                int set15 = Integer.parseInt(mEtSet5.getText().toString());
                if (set15 < 0 || set15 > 100) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC1D(set15);
                break;
            case R.id.tv_set6:
                String set6 = mEtSet6.getText().toString();
                if (TextUtils.isEmpty(set6)) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                int set16 = Integer.parseInt(mEtSet6.getText().toString());
                if (set16 < 0 || set16 > 100) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC1F(set16);
                break;
            case R.id.tv_set7:
                String set7 = mEtSet7.getText().toString();
                if (TextUtils.isEmpty(set7)) {
                    MyToastUtils.showMyToast("请输入正确的值0~1000");
                    return;
                }
                int set17 = Integer.parseInt(mEtSet7.getText().toString());
                if (set17 < 0 || set17 > 1000) {
                    MyToastUtils.showMyToast("请输入正确的值0~1000");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC21(set17);
                break;
            case R.id.tv_set8:
                String set8 = mEtSet8.getText().toString();
                if (TextUtils.isEmpty(set8)) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                int set18 = Integer.parseInt(mEtSet8.getText().toString());
                if (set18 < 0 || set18 > 100) {
                    MyToastUtils.showMyToast("请输入正确的值0~100");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC23(set18);
                break;
            case R.id.tv_recover1:
                showWaitingDialog("请稍后", false);
                portUtil.addC17(150);
                break;
            case R.id.tv_recover2:
                showWaitingDialog("请稍后", false);
                portUtil.addC19(200);
                break;
            //case R.id.tv_recover3:
            //    break;
            case R.id.tv_recover4:
                showWaitingDialog("请稍后", false);
                portUtil.addC1B(5);
                break;
            case R.id.tv_recover5:
                showWaitingDialog("请稍后", false);
                portUtil.addC1D(15);
                break;
            case R.id.tv_recover6:
                showWaitingDialog("请稍后", false);
                portUtil.addC1F(5);
                break;
            case R.id.tv_recover7:
                showWaitingDialog("请稍后", false);
                portUtil.addC21(300);
                break;
            case R.id.tv_recover8:
                showWaitingDialog("请稍后", false);
                portUtil.addC23(15);
                break;
            case R.id.tv_remove_alarm:
                showWaitingDialog("请稍后", false);
                portUtil.addC15();
                break;
        }
    }

    private void initData(){
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC16();
        //    }
        //},1000);
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC18();
        //    }
        //},2000);
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC1A();
        //    }
        //},3000);
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC1C();
        //    }
        //},4000);
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC1E();
        //    }
        //},5000);
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC20();
        //    }
        //},6000);
        //mHandler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        portUtil.addC22();
        //    }
        //},7000);
        RiceMillHttp.get().getAlarm(new Bean01Callback<AlarmBean>() {
            @Override
            public void onSuccess(AlarmBean bean) {
                mTvLast1.setText(bean.data.overheat+"");
                mTvLast2.setText(bean.data.warm_overtime+"");
                mTvLast4.setText(bean.data.down_overtime+"");
                mTvLast5.setText(bean.data.lamo_overtime+"");
                mTvLast6.setText(bean.data.fengmo_overtime+"");
                mTvLast7.setText(bean.data.give_rice_overtime+"");
                mTvLast8.setText(bean.data.give_mo_overtime+"");
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                showOneToast(message);
            }
        });

    }

    private void updateAlarm(int type,int content){
        RiceMillHttp.get().updateAlarm(type, content, new Bean01Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                showOneToast("设置成功");
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                showOneToast(message);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
