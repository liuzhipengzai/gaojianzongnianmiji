package com.example.administrator.gaojianzongnianmiji.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.dlc.commonlibrary.ui.adapter.BaseRecyclerAdapter;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.bean.MealListBean;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class CardPayOrderAdapter extends BaseRecyclerAdapter<MealListBean.DataBean> {
    private  int mPosition=0;
    private Context mContext;

    public CardPayOrderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.adapter_card_pay_order;
    }

    @Override
    public void onBindViewHolder(CommonHolder holder, final int position) {
        MealListBean.DataBean item = getItem(position);
        ImageView image = holder.getImage(R.id.select_img);
        TextView text = holder.getText(R.id.title_tv);
        FrameLayout title_fl = holder.getView(R.id.title_fl);
        //if (item.give_money.compareTo("0")==0){
        //    text.setText("充"+item.price);
        //}
        //text.setText("充"+item.price+"送"+item.give_money);
        text.setText("充"+item.price);
        if (mPosition==position){
            image.setVisibility(View.VISIBLE);
            text.setTextColor(mContext.getResources().getColor(R.color.color_CF2518));
            title_fl.setSelected(true);
        }else {
            image.setVisibility(View.GONE);
            text.setTextColor(mContext.getResources().getColor(R.color.color_333));
            title_fl.setSelected(false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPosition=position;
                notifyDataSetChanged();
                mCallback.select(item.price,position);
            }
        });
    }
    CallBack mCallback;

    public interface CallBack{
        void select(String price,int position);
    }
    public void setCallBack(CallBack callBack){
        mCallback = callBack;
    }
}
