package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class OperationHomeFragment extends BaseFragment {
    @BindView(R.id.replenishment_tv)
    TextView mReplenishmentTv;
    @BindView(R.id.detection_tv)
    TextView mDetectionTv;
    @BindView(R.id.parameter_tv)
    TextView mParameterTv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_operation_home;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.mBackTime.setVisibility(View.GONE);
        mActivity.mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(new LoginFragment());
            }
        });
    }

    @OnClick({ R.id.replenishment_tv, R.id.parameter_tv,R.id.detection_tv,R.id.alarm_tv,R.id.check_state_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            //补稻谷
            case R.id.replenishment_tv:
                mActivity.replaceFragment(ReplenishmentFragment.newInstance(1));
                break;
                //补袋子
            //case R.id.add_pack_tv:
            //    mActivity.replaceFragment(ReplenishmentFragment.newInstance(2));
            //    break;
            //硬件检测
            case R.id.detection_tv:
                mActivity.replaceFragment(new DetectionFragment());
                break;
            //参数设计
            case R.id.parameter_tv:
                mActivity.replaceFragment(new CheckFragment());
                break;
            //报警设置
            case R.id.alarm_tv:
                mActivity.replaceFragment(new AlarmFragment());
                break;
            //查看状态
            case R.id.check_state_tv:
                mActivity.replaceFragment(new CheckStateFragment());
                break;
        }
    }
}
