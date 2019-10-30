package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.base.BaseBean;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.bean.DeviceInfoBean;
import com.example.administrator.gaojianzongnianmiji.view.ResultDialog;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class ReplenishmentFragment extends BaseFragment {

    @BindView(R.id.tv_read_rice_num)
    TextView mTvReadRiceNum;
    @BindView(R.id.tv_rice_num)
    TextView mTvRiceNum;
    @BindView(R.id.tv_read_pack_num)
    TextView mTvReadPackNum;
    @BindView(R.id.tv_pack_num)
    TextView mTvPackNum;
    @BindView(R.id.et_rice)
    EditText mEtRice;
    @BindView(R.id.tv_save_rice)
    TextView mTvSaveRice;
    @BindView(R.id.et_pack)
    EditText mEtPack;
    @BindView(R.id.tv_save_pack)
    TextView mTvSavePack;
    String token;
    String riceNum;
    String packNum;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_replenishment;
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
        token = PrefUtil.getDefault().getString(Constant.operation_land_token, "");
    }

    public static ReplenishmentFragment newInstance(int type) {
        ReplenishmentFragment fragment = new ReplenishmentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.tv_save_pack,R.id.tv_save_rice,R.id.tv_read_rice_num,R.id.tv_read_pack_num})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_read_rice_num:
                getNum(mTvRiceNum,1);
                break;
            case R.id.tv_read_pack_num:
                getNum(mTvPackNum,2);
                break;
            case R.id.tv_save_rice:
                getNum(mTvRiceNum,3);
                break;
            case R.id.tv_save_pack:
                getNum(mTvPackNum,4);
                break;
        }

    }

    private void getNum(TextView textView,int type){
        RiceMillHttp.get().deviceInfo(new Bean01Callback<DeviceInfoBean>() {
            @Override
            public void onSuccess(DeviceInfoBean bean) {
                riceNum = bean.data.warehouse;
                packNum = bean.data.bag;
                if (type == 1){
                    textView.setText("稻谷数量为："+bean.data.warehouse+"斤");
                }else if (type == 2){
                    textView.setText("袋子数量为："+bean.data.bag+"个");
                }else if (type == 3){
                    String warehouse = mEtRice.getText().toString();
                    if (warehouse==null){
                        showOneToast("请输入数量");
                        return;
                    }
                    changeNum(packNum,warehouse);
                }else if (type == 4){
                    String bag = mEtPack.getText().toString();
                    if (bag==null){
                        showOneToast("请输入数量");
                        return;
                    }
                    changeNum(bag,riceNum);
                }
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                showOneToast(message);
            }
        });
    }
    private void changeNum(String bag,String warehouse){
        ResultDialog dialog = new ResultDialog(mActivity);
        RiceMillHttp.get().editDevice(token, bag, warehouse, new Bean01Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                dialog.setContent("提交成功！", R.mipmap.ic_ichenggong);
                //dialog.setContent("提交成功！",R.mipmap.ic_shibai);
                dialog.show();
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                showOneToast(message);
                dialog.setContent("提交失败！", R.mipmap.ic_shibai);
                dialog.show();
            }
        });
    }
}
