package com.example.administrator.gaojianzongnianmiji.adapter;

import android.content.Context;
import android.widget.ImageView;
import cn.dlc.commonlibrary.ui.adapter.BaseRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.bean.MainShopbean;

/**
 * Created by lixukang   on  2019/6/20.
 */

public class MainShopAdapter extends BaseRecyclerAdapter<MainShopbean.DataBean> {
    
    private Context mContext;

    public MainShopAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.adapter_main_shop;
    }

    @Override
    public void onBindViewHolder(CommonHolder holder, int position) {
        MainShopbean.DataBean item = getItem(position);
        holder.setText(R.id.name_tv,item.name);
        ImageView image = holder.getImage(R.id.head_img);
        Glide.with(mContext)
            .load( item.img)
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))   .into(image);
    }
}
