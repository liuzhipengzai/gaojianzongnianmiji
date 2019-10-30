package com.example.administrator.gaojianzongnianmiji.base;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.utils.MyToastUtils;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.licheedev.myutils.LogPlus;

public class CheckActivity extends BaseActivity {
    @BindView(R.id.et_out_weight)
    AppCompatEditText mEtOutWeight;
    @BindView(R.id.bt_start_rice)
    Button mBtStartRice;
    //@BindView(R.id.et_replenish_rice)
    //AppCompatEditText mEtReplenishRice;
    //@BindView(R.id.bt_open_barn)
    //Button mBtOpenBarn;
    @BindView(R.id.sp_grade)
    AppCompatSpinner mSpGrade;
    @BindView(R.id.tv_tip1)
    TextView mTvTip1;
    @BindView(R.id.tv1)
    TextView mTv1;
    @BindView(R.id.bt_read_w)
    Button mBtReadW;
    @BindView(R.id.tv_weight)
    TextView mTvWeight;
    @BindView(R.id.tv_tip2)
    TextView mTvTip2;
    @BindView(R.id.tv2)
    TextView mTv2;
    @BindView(R.id.sp_grade1)
    AppCompatSpinner mSpGrade1;
    @BindView(R.id.et_repair_weight)
    AppCompatEditText mEtRepairWeight;
    @BindView(R.id.bt_set_w)
    Button mBtSetW;
    @BindView(R.id.bt_read_stop_dtime)
    Button mBtReadStopDtime;
    @BindView(R.id.tv_close_dtime)
    TextView mTvCloseDtime;
    @BindView(R.id.et_setdtime)
    AppCompatEditText mEtSetdtime;
    @BindView(R.id.bt_setdtime)
    Button mBtSetdtime;
    @BindView(R.id.bt_read_stop_mtime)
    Button mBtReadStopMtime;
    @BindView(R.id.tv_close_mtime)
    TextView mTvCloseMtime;
    @BindView(R.id.et_setmtime)
    AppCompatEditText mEtSetmtime;
    @BindView(R.id.bt_setmtime)
    Button mBtSetmtime;
    @BindView(R.id.tv3)
    TextView mTv3;
    @BindView(R.id.sp_grade3)
    AppCompatSpinner mSpGrade3;
    @BindView(R.id.bt_read_max_time)
    Button mBtReadMaxTime;
    @BindView(R.id.tv_max_time)
    TextView mTvMaxTime;
    @BindView(R.id.tv4)
    TextView mTv4;
    @BindView(R.id.sp_grade4)
    AppCompatSpinner mSpGrade4;
    @BindView(R.id.et_max_time)
    AppCompatEditText mEtMaxTime;
    @BindView(R.id.bt_set_max_time)
    Button mBtSetMaxTime;
    @BindView(R.id.tv_out_weight)
    TextView mTvOutWeight;
    @BindView(R.id.et_dian_su)
    AppCompatEditText mEtDianSu;
    @BindView(R.id.bt_set_su)
    Button mBtSetSu;
    @BindView(R.id.bt_read_su)
    Button mBtReadSu;
    @BindView(R.id.tv_su_dtime)
    TextView mTvSuDtime;
    //@BindView(R.id.et_weight)
    //AppCompatEditText mEtWeight;
    //@BindView(R.id.bt_check_weight)
    //Button mBtCheckWeight;
    //@BindView(R.id.tv_fail_report)
    //TextView mTvFailReport;
    private String[] mStringArray;
    private int checkWeight;

    private SerialPortUtil portUtil;
    private int read_w = 1, set_w = 1, read_max_time = 1, set_max_time = 1;
    private String staff_token;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_check;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        staff_token = getIntent().getStringExtra("staff_token");
        mStringArray = getResources().getStringArray(R.array.rice_weight);
        mSpGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                read_w = Integer.parseInt(mStringArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpGrade1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                set_w = Integer.parseInt(mStringArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpGrade3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                read_max_time = Integer.parseInt(mStringArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpGrade4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                set_max_time = Integer.parseInt(mStringArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                                    mTvOutWeight.setText("出米重量:" + wegiht + "克");
                                    break;
                                case "04"://交易完成告诉后台
                                    int wa = Integer.parseInt(data.substring(6, 14), 16);//系统机交易流水
                                    int z = Integer.parseInt(data.substring(14, 22), 16);//终端交易流水
                                    int wt = Integer.parseInt(data.substring(22, 30), 16);//实时出米重量
                                    String rt = data.substring(30, 32);//上报结果
                                    portUtil.addCA05(wa, z);
                                    mTvOutWeight.setText("出米重量:" + wt + "克出米完成");
                                    break;
                            }
                            break;
                        //case "0C0E":
                        //    MyToastUtils.showMyToast("谷仓仓门已打开");
                        //    String et_replenish_rice = mEtReplenishRice.getText().toString();
                        //    break;
                        case "0C05":
                            int grade = Integer.parseInt(data.substring(6, 8), 16);
                            int weight = Integer.parseInt(data.substring(8, 12), 16);
                            mTvWeight.setText("每" + grade * 500 + "克补偿" + weight + "克");
                            break;
                        case "0C85":
                            int grade1 = Integer.parseInt(data.substring(6, 8), 16);
                            int weight1 = Integer.parseInt(data.substring(8, 12), 16);
                            MyToastUtils.showMyToast("设置补偿质量成功",
                                "每" + grade1 * 500 + "克补偿" + weight1 + "克", true, true);
                            break;
                        case "0C07":
                            int stopTime = Integer.parseInt(data.substring(4, 8), 16);
                            mTvCloseDtime.setText("电机延迟停转时间 " + stopTime + "秒");
                            break;
                        case "0C87":
                            int s = Integer.parseInt(data.substring(4, 8), 16);
                            MyToastUtils.showMyToast("设置电机延迟停转时间成功", "电机延迟停转时间 " + s + "秒", true,
                                true);
                            break;
                        case "0C08":
                            int t = Integer.parseInt(data.substring(4, 8), 16);
                            mTvCloseMtime.setText("碾米机米仓门关闭延迟时间 " + t + "秒");
                            break;
                        case "0C88":
                            int q = Integer.parseInt(data.substring(4, 8), 16);
                            MyToastUtils.showMyToast("设置碾米机米仓门关闭延迟时间成功", "碾米机米仓门关闭延迟时间 " + q + "秒",
                                true, true);
                            break;
                        case "0C09":
                            LogPlus.e("0c09==================="+data);
                            int g = Integer.parseInt(data.substring(4, 6), 16);
                            int m = Integer.parseInt(data.substring(6, 10), 16);
                            mTvMaxTime.setText("碾米机交易超时时间每" + g * 500 + "克" + m + "秒");
                            break;
                        case "0C89":
                            int z = Integer.parseInt(data.substring(4, 6), 16);
                            int p = Integer.parseInt(data.substring(6, 10), 16);
                            MyToastUtils.showMyToast("设置碾米机交易超时时间成功",
                                "碾米机交易超时时间每 " + z * 500 + "克" + p + "秒", true, true);
                            break;
                        case "0C8C":
                            int su = Integer.parseInt(data.substring(4, 6), 16);
                            MyToastUtils.showMyToast("设置谷仓电机速度成功", "谷仓电机速度 " + su, true, true);
                            break;
                        case "0C0C":
                            int sut = Integer.parseInt(data.substring(4, 6), 16);
                            mTvSuDtime.setText("谷仓电机速度 " + sut);
                            break;
                        //case "0C82":
                        //    if (checkWeight == 1) {
                        //        int wg = Integer.parseInt(mEtWeight.getText().toString());
                        //        if (wg > 65535) {
                        //            MyToastUtils.showMyToast("不能超过65535克");
                        //            return;
                        //        }
                        //        checkWeight = 2;
                        //        showWaitingDialog("请稍后", false);
                        //        SystemClock.sleep(1000);
                        //        portUtil.addC82(3, wg);
                        //    }
                        //    if (checkWeight == 2) {
                        //        MyToastUtils.showMyToast("校准成功", "", true, true);
                        //    }
                        //    break;
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
        //LinearLayout linearLayout = findViewById(R.id.ll_back);
        //linearLayout.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        finishActivity();
        //    }
        //});
    }

    @Override
    protected void onDestroy() {
        if (portUtil != null) {
            portUtil.setCheckResult(null);
        }
        super.onDestroy();
    }

    private int oldWater;

    @OnClick({
        R.id.bt_start_rice,  R.id.bt_read_w, R.id.bt_set_w,
        R.id.bt_read_stop_dtime, R.id.bt_setdtime, R.id.bt_read_stop_mtime, R.id.bt_setmtime,
        R.id.bt_read_max_time, R.id.bt_set_max_time, R.id.bt_set_su, R.id.bt_read_su,
        //R.id.bt_check_weight, R.id.bt_check_weight2
        //,R.id.tv_fail_report，R.id.bt_open_barn,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_rice:
                String s = mEtOutWeight.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                float p = Float.parseFloat(s);
                if (p == 0) {
                    MyToastUtils.showMyToast("请输入出米重量");
                    return;
                }
                showWaitingDialog("请稍后", false);
                if (oldWater == 0) {
                    oldWater = 2147483647;
                } else {
                    oldWater = 0;
                }
                //3AA3000000000001000E 09CA 01 7FFFFFFF 0100000 E8010131F3
                portUtil.addCA(oldWater, (int) (p * 1000),1);
                break;
            //case R.id.bt_open_barn:
            //    showWaitingDialog("请稍后", false);
            //    portUtil.addC0E();
            //    break;
            case R.id.bt_read_w:
                showWaitingDialog("请稍后", false);
                mTvWeight.setText("");
                portUtil.addC05(1, read_w);
                break;
            case R.id.bt_set_w:
                String t = mEtRepairWeight.getText().toString();
                if (TextUtils.isEmpty(t)) {
                    MyToastUtils.showMyToast("请输入补偿质量");
                    return;
                }
                int q = Integer.parseInt(t);
                if (q < 0) {
                    MyToastUtils.showMyToast("请输入补偿质量");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC85(1, set_w, q);
                break;
            case R.id.bt_read_stop_dtime:
                showWaitingDialog("请稍后", false);
                mTvCloseDtime.setText("");
                portUtil.addC07();
                break;
            case R.id.bt_setdtime:
                String d = mEtSetdtime.getText().toString();
                if (TextUtils.isEmpty(d)) {
                    MyToastUtils.showMyToast("请输电机停转时间");
                    return;
                }
                int r = Integer.parseInt(d);
                if (r < 0) {
                    MyToastUtils.showMyToast("请输电机停转时间");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC87(r);
                break;
            case R.id.bt_read_stop_mtime:
                showWaitingDialog("请稍后", false);
                mTvCloseMtime.setText("");
                portUtil.addC08();
                break;
            case R.id.bt_setmtime:
                String y = mEtSetmtime.getText().toString();
                if (TextUtils.isEmpty(y)) {
                    MyToastUtils.showMyToast("请输米仓门关闭延迟时间");
                    return;
                }
                int f = Integer.parseInt(y);
                if (f < 0) {
                    MyToastUtils.showMyToast("请输米仓门关闭延迟时间");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC88(f);
                break;
            case R.id.bt_read_max_time:
                showWaitingDialog("请稍后", false);
                portUtil.addC09(read_max_time);
                mTvMaxTime.setText("");
                break;
            case R.id.bt_set_max_time:
                String u = mEtMaxTime.getText().toString();
                if (TextUtils.isEmpty(u)) {
                    MyToastUtils.showMyToast("请输入交易时间");
                    return;
                }
                int i = Integer.parseInt(u);
                if (i < 0) {
                    MyToastUtils.showMyToast("请输入交易时间");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC89(set_max_time, i);
                break;
            case R.id.bt_set_su:
                int su = Integer.parseInt(mEtDianSu.getText().toString());
                if (su < 0 || su > 100) {
                    MyToastUtils.showMyToast("请输入正确的速度0~100");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC8C(su);
                break;
            case R.id.bt_read_su:
                showWaitingDialog("请稍后", false);
                portUtil.addC0C();
                break;
            //case R.id.bt_check_weight:
            //    checkWeight = 0;
            //    showWaitingDialog("请稍后", false);
            //    portUtil.addC82(1, 0);
            //    break;
            //case R.id.bt_check_weight2:
            //    int weight = Integer.parseInt(mEtWeight.getText().toString());
            //    if (weight > 65535) {
            //        MyToastUtils.showMyToast("不能超过65535克");
            //        return;
            //    }
            //    checkWeight = 1;
            //    showWaitingDialog("请稍后", false);
            //    portUtil.addC82(2, weight);
            //    break;
            //case R.id.tv_fail_report:
                ////Intent intent = new Intent(this, FailReportActivity.class);
                //intent.putExtra("staff_token",staff_token);
                //startActivity(intent);
                //break;
            //case R.id.ll_back:
            //    finish();
            //    break;
        }
    }
}
