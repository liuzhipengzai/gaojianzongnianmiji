package com.example.administrator.gaojianzongnianmiji.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.ui.adapter.BaseRecyclerAdapter;
import cn.dlc.commonlibrary.utils.PrefUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.gaojianzongnianmiji.Constant;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.adapter.MainShopAdapter;
import com.example.administrator.gaojianzongnianmiji.base.BaseFragment;
import com.example.administrator.gaojianzongnianmiji.bean.AboutBean;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;
import com.example.administrator.gaojianzongnianmiji.utils.MQTTBusiness;
import com.example.administrator.gaojianzongnianmiji.view.HitchDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class MainShopFragment extends BaseFragment {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.scan_img)//公众号
    ImageView mScanImg;
    @BindView(R.id.ic_card_ll)
    LinearLayout mIcCardLl;
    @BindView(R.id.login_ll)
    LinearLayout mLoginLl;
    private ArrayList<MainShopbean> mList;
    private MainShopAdapter mAdapter;
    private ScheduledThreadPoolExecutor scheduled;
    HitchDialog dialog;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_shop;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.mBackImg.setVisibility(View.INVISIBLE);
        mActivity.mLlBack.setVisibility(View.GONE);
        initRecyclerView();
        initData();
        initview();

        MQTTBusiness.getInstance(mActivity).setAboutCallBack(new MQTTBusiness.AboutCallBack() {
            @Override
            public void about() {
                initview();
            }
        });
        MQTTBusiness.getInstance(mActivity).setHitchCallBack(new MQTTBusiness.HitchCallBack() {
            @Override
            public void hitch(String remarks) {
                PrefUtil.getDefault().saveBoolean(Constant.Hitch,true);
                PrefUtil.getDefault().saveString(Constant.Remarks,remarks);
                //if (dialog!=null&&dialog.isShowing()){
                //    return;
                //}else {
                //    dialog = new HitchDialog(mActivity);
                //    dialog.show(remarks);
                //    dialog.setCancelable(false);
                //}
            }
        });
        MQTTBusiness.getInstance(mActivity).setNoHitchCallBack(new MQTTBusiness.NoHitchCallBack() {
            @Override
            public void nohitch() {
                PrefUtil.getDefault().saveBoolean(Constant.Hitch,false);
                //if (dialog!=null&&dialog.isShowing()){
                //    dialog.dismiss();
                //}
            }
        });
    }

    private void initData() {
        scheduled = new ScheduledThreadPoolExecutor(2);
        scheduled.scheduleAtFixedRate(new SwitchTask(), 0, 60 * 1000, TimeUnit.MILLISECONDS);
    }
    private class SwitchTask extends TimerTask {
        @Override
        public void run() {
            RiceMillHttp.get().getCommodity(new Bean01Callback<MainShopbean>() {
                @Override
                public void onSuccess(MainShopbean mainShopbean) {
                    //保存精度
                    PrefUtil.getDefault().saveString(Constant.GoodsID1,mainShopbean.data.get(0).level);
                    PrefUtil.getDefault().saveString(Constant.GoodsID2,mainShopbean.data.get(1).level);
                    PrefUtil.getDefault().saveString(Constant.GoodsID3,mainShopbean.data.get(2).level);
                    List<MainShopbean.DataBean> data = mainShopbean.data;
                    mAdapter.setNewData(data);
                    dismissWaitingDialog();
                }

                @Override
                public void onFailure(String message, Throwable tr) {
                    dismissWaitingDialog();
                }
            });
        }
    }

    private void initview() {
        RiceMillHttp.get()
            .about()
            .subscribeOn(Schedulers.io())
            .retry(new BiPredicate<Integer, Throwable>() {
                @Override
                public boolean test(Integer integer, Throwable throwable) throws Exception {
                    SystemClock.sleep(3000);
                    return true;
                }
            })
            .timeout(10 * 60, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<AboutBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(AboutBean bean) {
                    try {
                        Glide.with(mActivity).load(bean.data.url).apply(new RequestOptions().error(R.mipmap.ic_close).diskCacheStrategy
                            (DiskCacheStrategy.ALL).transform(new CenterCrop())).into(mActivity.mLogoImg);
                        Glide.with(mActivity).load(bean.data.qrcode).apply(new RequestOptions().error(R.mipmap.ic_close).diskCacheStrategy
                            (DiskCacheStrategy.ALL).transform(new CenterCrop())).into(mScanImg);
                        mActivity.mTvCompanyName.setText(bean.data.name);
                        PrefUtil.getDefault().saveString(Constant.About_Mobile,bean.data.mobile);
                    }catch (Exception e){
                    }
                }

                @Override
                public void onError(Throwable e) {
                    showOneToast(e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
    }

    private void initRecyclerView() {
        mRecycler.setLayoutManager(
            new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new MainShopAdapter(mActivity);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, BaseRecyclerAdapter.CommonHolder holder,
                int position) {
                boolean hitch = PrefUtil.getDefault().getBoolean(Constant.Hitch,false);
                if (hitch){
                    if (dialog!=null&&dialog.isShowing()){
                        return;
                    }else {
                        String mRemarks = PrefUtil.getDefault().getString(Constant.Remarks,"");
                        RiceMillHttp.get().about(new Bean01Callback<AboutBean>() {
                            @Override
                            public void onSuccess(AboutBean aboutBean) {
                                dialog = new HitchDialog(mActivity);
                                dialog.show(mRemarks,aboutBean.data.mobile);
                            }

                            @Override
                            public void onFailure(String message, Throwable tr) {
                                showOneToast(message);
                            }
                        });

                    }
                    return;
                }
                MainShopbean.DataBean item = mAdapter.getItem(position);
                MainFragment parentFragment = (MainFragment) getParentFragment();
                if (parentFragment != null) {
                    parentFragment.initFragment( MainShopItemFragment.newInstance(item));
                }
            }
        });
    }

    

    @OnClick({ R.id.ic_card_ll, R.id.login_ll })
    public void onClick(View view) {
        switch (view.getId()) {
            //ic卡充值
            case R.id.ic_card_ll:
                mActivity.replaceFragment(CardPayFragment.newInstance(1,null,0));
                break;
            //维护登录
            case R.id.login_ll:
                //mActivity.replaceFragment(new CheckFragment());
                mActivity.replaceFragment(new LoginFragment());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
