package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.bean.UpdaePrecisionBean;
import com.example.administrator.gaojianzongnianmiji.utils.MyToastUtils;
import com.example.administrator.gaojianzongnianmiji.utils.SerialPortUtil;
import com.licheedev.myutils.LogPlus;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

/**
 * Created by lixukang   on  2019/7/17.
 */

public class CheckFragment extends BaseFragment {
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
    @BindView(R.id.sp_grade5)
    AppCompatSpinner mSpGrade5;
    @BindView(R.id.sp_grade6)
    AppCompatSpinner mSpGrade6;
    @BindView(R.id.et_pack_temp_su)
    AppCompatEditText mEtPackTemp;
    @BindView(R.id.tv_su_pack_temp)
    TextView mTvSuPackTemp;
    @BindView(R.id.et_goods1_id)
    AppCompatEditText mEtGoods1Id;
    @BindView(R.id.bt_set_goods1_id)
    Button mBtSetGoods1Id;
    @BindView(R.id.et_goods2_id)
    AppCompatEditText mEtGoods2Id;
    @BindView(R.id.bt_set_goods2_id)
    Button mBtSetGoods2Id;
    @BindView(R.id.et_goods3_id)
    AppCompatEditText mEtGoods3Id;
    @BindView(R.id.bt_set_goods3_id)
    Button mBtSetGoods3Id;
    @BindView(R.id.bt_read_goods1_id)
    Button mBtReadGoods1Id;
    @BindView(R.id.tv_goods1_id)
    TextView mTvGoods1Id;
    @BindView(R.id.bt_read_goods2_id)
    Button mBtReadGoods2Id;
    @BindView(R.id.tv_goods2_id)
    TextView mTvGoods2Id;
    @BindView(R.id.bt_read_goods3_id)
    Button mBtReadGoods3Id;
    @BindView(R.id.tv_goods3_id)
    TextView mTvGoods3Id;
    Unbinder unbinder;
    @BindView(R.id.sp_grade7)
    AppCompatSpinner mSpGrade7;
    @BindView(R.id.sp_grade8)
    AppCompatSpinner mSpGrade8;
    Unbinder unbinder1;
    //@BindView(R.id.et_weight)
    //AppCompatEditText mEtWeight;
    //@BindView(R.id.bt_check_weight)
    //Button mBtCheckWeight;
    //@BindView(R.id.tv_fail_report)
    //TextView mTvFailReport;
    private String[] mStringArray;
    private String[] mStringArray1;
    private int checkWeight;

    private SerialPortUtil portUtil;
    private int read_w = 1, set_w = 1, read_max_time = 1, set_max_time = 1, read_t = 1, set_t = 1,Grade7_num=1,Grade8_num=1;
    private String staff_token;
    private String goodsid1, goodsid2, goodsid3;
    String token;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.mBackTime.setVisibility(View.GONE);
        //goodsid1 = PrefUtil.getDefault().getString(Constant.GoodsID1,"");
        //goodsid2 = PrefUtil.getDefault().getString(Constant.GoodsID2,"");
        //goodsid3 = PrefUtil.getDefault().getString(Constant.GoodsID3,"");
        token = PrefUtil.getDefault().getString(Constant.operation_land_token, "");
        mActivity.mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(new OperationHomeFragment());
            }
        });

        mStringArray = getResources().getStringArray(R.array.rice_weight);
        mStringArray1 = getResources().getStringArray(R.array.rice_temp);
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
        mSpGrade5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                set_t = Integer.parseInt(mStringArray1[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpGrade6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                read_t = Integer.parseInt(mStringArray1[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpGrade7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Grade7_num = 1;
                }else {
                    Grade7_num = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpGrade8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Grade8_num = 1;
                }else {
                    Grade8_num = 2;
                }
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
                            LogPlus.e("0C05命令返回：==========" + data);
                            int grade = Integer.parseInt(data.substring(6, 8), 16);
                            int weight = Integer.parseInt(data.substring(8, 12), 16);
                            mTvWeight.setText("每" + grade * 500 + "克补偿" + weight + "克");
                            break;
                        case "0C85":
                            LogPlus.e("0C85命令返回：==========" + data);
                            int grade1 = Integer.parseInt(data.substring(6, 8), 16);
                            int weight1 = Integer.parseInt(data.substring(8, 12), 16);
                            MyToastUtils.showMyToast("设置补偿质量成功",
                                "每" + grade1 * 500 + "克补偿" + weight1 + "克", true, true);
                            break;
                        case "0C07":
                            LogPlus.e("0C07命令返回：==========" + data);
                            int stopTime = Integer.parseInt(data.substring(4, 8), 16);
                            mTvCloseDtime.setText("电机延迟停转时间 " + stopTime + "秒");
                            break;
                        case "0C87":
                            LogPlus.e("0C87命令返回：==========" + data);
                            int s = Integer.parseInt(data.substring(4, 8), 16);
                            MyToastUtils.showMyToast("设置电机延迟停转时间成功", "电机延迟停转时间 " + s + "秒", true,
                                true);
                            setPrecision1(3, s);
                            break;
                        case "0C08":
                            LogPlus.e("0C08命令返回：==========" + data);
                            int t = Integer.parseInt(data.substring(4, 8), 16);
                            mTvCloseMtime.setText("碾米机米仓门关闭延迟时间 " + t + "秒");
                            break;
                        case "0C88":
                            LogPlus.e("0C88命令返回：==========" + data);
                            int q = Integer.parseInt(data.substring(4, 8), 16);
                            MyToastUtils.showMyToast("设置碾米机米仓门关闭延迟时间成功", "碾米机米仓门关闭延迟时间 " + q + "秒",
                                true, true);
                            setPrecision1(2, q);
                            break;
                        case "0C09":
                            LogPlus.e("0C09命令返回：==========" + data);
                            int g = Integer.parseInt(data.substring(4, 8), 16);
                            //int m = Integer.parseInt(data.substring(6, 10), 16);
                            mTvMaxTime.setText("碾米机交易超时时间" + g + "秒");
                            break;
                        case "0C89":
                            LogPlus.e("0C89命令返回：==========" + data);
                            int z = Integer.parseInt(data.substring(4, 8), 16);
                            //int p = Integer.parseInt(data.substring(6, 10), 16);
                            MyToastUtils.showMyToast("设置碾米机交易超时时间成功", "碾米机交易超时时间 " + z + "秒", true,
                                true);
                            setPrecision1(1, z);
                            break;
                        case "0C8C":
                            LogPlus.e("0C8C命令返回：==========" + data);
                            int su = Integer.parseInt(data.substring(4, 6), 16);
                            MyToastUtils.showMyToast("设置谷仓电机速度成功", "谷仓电机速度 " + su, true, true);
                            break;
                        case "0C11":
                            LogPlus.e("0C11命令返回：==========" + data);
                            int temp = Integer.parseInt(data.substring(4, 8), 16);
                            mTvSuDtime.setText("风扇启动设定温度：" + temp);
                            break;
                        case "0C12":
                            LogPlus.e("0C12命令返回：==========" + data);
                            int temp1 = Integer.parseInt(data.substring(4, 8), 16);
                            MyToastUtils.showMyToast("设置风扇启动设定温度成功", "温度 " + temp1, true, true);
                            break;
                        case "0C13":
                            LogPlus.e("0C13命令返回：==========" + data);
                            String c13;
                            int hengshuC13 = Integer.parseInt(data.substring(6, 8), 16);
                            int temp3 = Integer.parseInt(data.substring(8, 12), 16);
                            if (hengshuC13==1){
                                c13 = "横封加热片温度";
                            }else {
                                c13 = "竖封加热片温度";
                            }
                            mTvSuPackTemp.setText("封袋" + c13 + temp3);
                            break;
                        case "0C14":
                            LogPlus.e("0C14命令返回：==========" + data);
                            String c14;
                            int hengshuC14 = Integer.parseInt(data.substring(6, 8), 16);
                            if (hengshuC14==1){
                                c14 = "横封加热片温度";
                            }else {
                                c14 = "竖封加热片温度";
                            }
                            int temp4 = Integer.parseInt(data.substring(8, 12), 16);
                            MyToastUtils.showMyToast("设置封袋加热片设定温度成功", c14 + temp4, true, true);
                            break;
                        //case "0C0C":
                        //    LogPlus.e("0C0C命令返回：=========="+data);
                        //    int sut = Integer.parseInt(data.substring(4, 6), 16);
                        //    mTvSuDtime.setText("谷仓电机速度 " + sut);
                        //    break;
                        //case "0C82":
                        //    LogPlus.e("0C82命令返回：=========="+data);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (portUtil != null) {
            portUtil.setCheckResult(null);
        }
        super.onDestroy();
    }

    private int oldWater;

    @OnClick({
        R.id.bt_start_rice, R.id.bt_read_w, R.id.bt_set_w, R.id.bt_read_stop_dtime,
        R.id.bt_setdtime, R.id.bt_read_stop_mtime, R.id.bt_setmtime, R.id.bt_read_max_time,
        R.id.bt_set_max_time, R.id.bt_read_su, R.id.bt_set_su, R.id.bt_set_pack_temp,
        R.id.bt_read_pack_temp, R.id.bt_read_goods1_id, R.id.bt_read_goods2_id,
        R.id.bt_read_goods3_id, R.id.bt_set_goods1_id, R.id.bt_set_goods2_id, R.id.bt_set_goods3_id
        //R.id.bt_check_weight
        //,R.id.bt_check_weight2
        //,R.id.tv_fail_report,R.id.bt_open_barn,
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
                //portUtil.addCA(oldWater, (int) (p * 500), 1);
                portUtil.addCA(oldWater, 500, 1);
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
                portUtil.addC09();
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
                String diansu = mEtDianSu.getText().toString();
                if (TextUtils.isEmpty(diansu)) {
                    MyToastUtils.showMyToast("正确的温度0~150");
                    return;
                }
                int su = Integer.parseInt(mEtDianSu.getText().toString());
                if (su < 0 || su > 150) {
                    MyToastUtils.showMyToast("请输入正确的温度0~150");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC12(su);
                break;
            case R.id.bt_read_su:
                showWaitingDialog("请稍后", false);
                portUtil.addC11();
                break;
            case R.id.bt_set_pack_temp:
                String pack_temp = mEtPackTemp.getText().toString();
                if (TextUtils.isEmpty(pack_temp)) {
                    MyToastUtils.showMyToast("正确的温度0~150");
                    return;
                }
                int temp = Integer.parseInt(mEtPackTemp.getText().toString());
                if (temp < 0 || temp > 150) {
                    MyToastUtils.showMyToast("请输入正确的温度0~150");
                    return;
                }
                showWaitingDialog("请稍后", false);
                portUtil.addC14(set_t,Grade7_num, temp);
                break;
            case R.id.bt_read_pack_temp:
                showWaitingDialog("请稍后", false);
                portUtil.addC13(read_t,Grade8_num);
                break;
            case R.id.bt_read_goods1_id:
                goodsid1 = PrefUtil.getDefault().getString(Constant.GoodsID1, "1");
                mTvGoods1Id.setText("商品1的精度为" + goodsid1);
                break;
            case R.id.bt_read_goods2_id:
                goodsid2 = PrefUtil.getDefault().getString(Constant.GoodsID2, "1");
                mTvGoods2Id.setText("商品2的精度为" + goodsid2);
                break;
            case R.id.bt_read_goods3_id:
                goodsid3 = PrefUtil.getDefault().getString(Constant.GoodsID3, "1");
                mTvGoods3Id.setText("商品3的精度为" + goodsid3);
                break;
            case R.id.bt_set_goods1_id:
                String precision1 = mEtGoods1Id.getText().toString();
                if (TextUtils.isEmpty(precision1)) {
                    MyToastUtils.showMyToast("请输入正确的精度1~100");
                    return;
                }
                int int_precision1 = Integer.parseInt(mEtGoods1Id.getText().toString());
                if (int_precision1 < 0 || int_precision1 > 100) {
                    MyToastUtils.showMyToast("请输入正确的精度1~100");
                    return;
                }
                goodsid2 = PrefUtil.getDefault().getString(Constant.GoodsID2, "1");
                goodsid3 = PrefUtil.getDefault().getString(Constant.GoodsID3, "1");
                setPrecision(precision1, goodsid2, goodsid3);
                break;
            case R.id.bt_set_goods2_id:
                String precision2 = mEtGoods2Id.getText().toString();
                if (TextUtils.isEmpty(precision2)) {
                    MyToastUtils.showMyToast("请输入正确的精度1~100");
                    return;
                }
                int int_precision2 = Integer.parseInt(mEtGoods2Id.getText().toString());
                if (int_precision2 < 0 || int_precision2 > 100) {
                    MyToastUtils.showMyToast("请输入正确的精度1~100");
                    return;
                }
                goodsid1 = PrefUtil.getDefault().getString(Constant.GoodsID1, "1");
                goodsid3 = PrefUtil.getDefault().getString(Constant.GoodsID3, "1");
                setPrecision(goodsid1, precision2, goodsid3);
                break;
            case R.id.bt_set_goods3_id:
                String precision3 = mEtGoods3Id.getText().toString();
                if (TextUtils.isEmpty(precision3)) {
                    MyToastUtils.showMyToast("请输入正确的精度1~100");
                    return;
                }
                int int_precision3 = Integer.parseInt(mEtGoods3Id.getText().toString());
                if (int_precision3 < 0 || int_precision3 > 100) {
                    MyToastUtils.showMyToast("请输入正确的精度1~100");
                    return;
                }
                goodsid1 = PrefUtil.getDefault().getString(Constant.GoodsID1, "1");
                goodsid2 = PrefUtil.getDefault().getString(Constant.GoodsID2, "1");
                setPrecision(goodsid1, goodsid2, precision3);
                break;
        }
    }

    private void setPrecision(String precision1, String precision2, String precision3) {
        RiceMillHttp.get()
            .updaePrecision(precision1, precision2, precision3, token,
                new Bean01Callback<UpdaePrecisionBean>() {
                    @Override
                    public void onSuccess(UpdaePrecisionBean updaePrecisionBean) {
                        showOneToast("设置成功");
                        //保存精度
                        PrefUtil.getDefault()
                            .saveString(Constant.GoodsID1, updaePrecisionBean.data.precision1);
                        PrefUtil.getDefault()
                            .saveString(Constant.GoodsID2, updaePrecisionBean.data.precision2);
                        PrefUtil.getDefault()
                            .saveString(Constant.GoodsID3, updaePrecisionBean.data.precision3);
                    }

                    @Override
                    public void onFailure(String message, Throwable tr) {
                        showOneToast(message);
                    }
                });
    }

    //交易超时时间（秒）
    //关闭延迟时间（秒）
    //延迟停转时间（秒）
    private void setPrecision1(int type, int time) {
        RiceMillHttp.get()
            .updaePrecision1(type, time, token, new Bean01Callback<UpdaePrecisionBean>() {
                @Override
                public void onSuccess(UpdaePrecisionBean updaePrecisionBean) {
                    showOneToast("设置成功");
                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    showOneToast(message);
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}
