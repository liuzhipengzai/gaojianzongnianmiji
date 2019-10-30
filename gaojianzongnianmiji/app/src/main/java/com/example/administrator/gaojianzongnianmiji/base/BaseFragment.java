package com.example.administrator.gaojianzongnianmiji.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cn.dlc.commonlibrary.ui.base.BaseCommonFragment;
import com.example.administrator.gaojianzongnianmiji.fragment.MainFragment;

public abstract class BaseFragment extends BaseCommonFragment {

    public MainActivity mActivity;
    private int count;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) getActivity();
    }

    protected abstract int getLayoutId();
    

    //处理每个Fragment都需要执行的逻辑
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        count = 60;
        mActivity.mBackImg.setVisibility(View.VISIBLE);
        mActivity.mLlBack.setVisibility(View.VISIBLE);
        mActivity.mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(new MainFragment());
                
            }
        });
    }

}

