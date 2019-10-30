package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.ui.adapter.BaseRecyclerAdapter;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.adapter.CardPayOrderAdapter;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.base.TimeCallback;
import com.example.administrator.gaojianzongnianmiji.bean.LoginBean;
import com.example.administrator.gaojianzongnianmiji.bean.MealListBean;
import com.example.administrator.gaojianzongnianmiji.utils.AdvertUtil;
import java.util.List;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class CardPayOrderFragment extends BaseFragment {
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.money_tv)
    TextView mMoneyTv;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.title_type_tv)
    TextView mTitleTypeTv;
    private CardPayOrderAdapter mAdapter;
    private LoginBean.DataBean mBean;
    private int mPosition = 0;
    private int mType;
    private String PayPrice;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_card_pay_order;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVIew();
        initRecyclerView();
        initData();
        AdvertUtil.showCountDownTime(60,mActivity.mBackTime, new TimeCallback() {
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

    public static CardPayOrderFragment newInstance(LoginBean.DataBean bean, int type) {
        Bundle args = new Bundle();
        CardPayOrderFragment fragment = new CardPayOrderFragment();
        args.putSerializable("bean", bean);
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    private void initRecyclerView() {
        mRecycler.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mAdapter = new CardPayOrderAdapter(mActivity);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, BaseRecyclerAdapter.CommonHolder holder,
                int position) {
                mPosition = position;
            }
        });
        mAdapter.setCallBack(new CardPayOrderAdapter.CallBack() {
            @Override
            public void select(String price,int position) {
                PayPrice = price;
                mPosition = position;
            }
        });
    }

    private void initData() {
        showWaitingDialog("", true);
        RiceMillHttp.get().setMealList(new Bean01Callback<MealListBean>() {
            @Override
            public void onSuccess(MealListBean mealListBean) {
                List<MealListBean.DataBean> data = mealListBean.data;
                dismissWaitingDialog();
                mAdapter.setNewData(data);
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                dismissWaitingDialog();
                showOneToast(message);
            }
        });
    }

    private void initVIew() {
        mBean = (LoginBean.DataBean) getArguments().getSerializable("bean");
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
        mNumberTv.setText((TextUtils.isEmpty(mBean.idcard)?mBean.mobile:mBean.idcard));
        mMoneyTv.setText("￥" + mBean.money);
    }

    @OnClick(R.id.save_tv)
    public void onClick() {
        mActivity.replaceFragment(
            CardPayDetailsFragment.newInstance(mBean, mAdapter.getItem(mPosition).id,mType));
    }
}
