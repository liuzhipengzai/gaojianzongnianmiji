package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class CardInfoFragment extends BaseFragment {
    @BindView(R.id.money_tv)
    TextView mMoneyTv;
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    @BindView(R.id.cancel_tv)
    TextView mCancelTv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.title_type_tv)
    TextView mTitleTypeTv;
    private LoginBean.DataBean mBean;
    private int mType;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_card_info;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        AdvertUtil.showCountDownTime(60, mActivity.mBackTime,new TimeCallback() {
            @Override
            public boolean timeFinish() {
                mActivity.replaceFragment(new MainFragment());
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdvertUtil.disposable();
    }

    public static CardInfoFragment newInstance(LoginBean.DataBean bean, int type) {
        Bundle args = new Bundle();
        CardInfoFragment fragment = new CardInfoFragment();
        args.putSerializable("bean", bean);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        mType = getArguments().getInt("type");
        switch (mType) {
            //1是刷卡信息
            case 1:
                mTitleTv.setText("IC卡信息");
                mTitleTypeTv.setText("IC卡号：");
                break;
            //2是会员登录信息
            case 2:
                mTitleTv.setText("会员信息");
                mTitleTypeTv.setText("手机号：");
                break;
        }
        mBean = (LoginBean.DataBean) getArguments().getSerializable("bean");
        mMoneyTv.setText(mBean.money);
        mNumberTv.setText((TextUtils.isEmpty(mBean.idcard)?mBean.mobile:mBean.idcard));
    }

    @OnClick({ R.id.save_tv, R.id.cancel_tv })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_tv:
                mActivity.replaceFragment(CardPayOrderFragment.newInstance(mBean,mType));
                break;
            case R.id.cancel_tv:
                mActivity.replaceFragment(new MainFragment());
                break;
        }
    }
}
