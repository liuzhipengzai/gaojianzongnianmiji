package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.dlc.commonlibrary.utils.DialogUtil;
import com.example.administrator.gaojianzongnianmiji.R;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class ResultDialog extends Dialog {
    @BindView(R.id.head_img)
    ImageView mHeadImg;
    @BindView(R.id.content_tv)
    TextView mContentTv;
    private final Context mContext;

    public ResultDialog(@NonNull Context context) {
        super(context, R.style.CommonDialogStyle);
        mContext = context;
        setContentView(R.layout.dialog_result);
        DialogUtil.setGravity(this, Gravity.CENTER);
        ButterKnife.bind(this);
    }
    
    public void  setContent(String content,int  img ){
        mContentTv.setText(content);
        mHeadImg.setBackground(mContext.getResources().getDrawable(img));
    }
}
