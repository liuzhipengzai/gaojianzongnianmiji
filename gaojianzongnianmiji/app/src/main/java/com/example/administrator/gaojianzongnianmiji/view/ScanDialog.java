package com.example.administrator.gaojianzongnianmiji.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.utils.DialogUtil;
import com.example.administrator.gaojianzongnianmiji.R;
import com.example.administrator.gaojianzongnianmiji.RiceMillHttp;
import com.example.administrator.gaojianzongnianmiji.bean.DeviceInfoBean;

/**
 * Created by lixukang   on  2019/6/21.
 */

public class ScanDialog extends Dialog {
    @BindView(R.id.back_img)
    ImageView mBackImg;
    @BindView(R.id.code_tv)
    ImageView mCodeTv;

    public ScanDialog(@NonNull Context context) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_scan);
        DialogUtil.setGravity(this,Gravity.CENTER);
        ButterKnife.bind(this);
        initView(context);
    }

    private void initView(Context context) {
        RiceMillHttp.get().deviceInfo(new Bean01Callback<DeviceInfoBean>() {
            @Override
            public void onSuccess(DeviceInfoBean deviceInfoBean) {
                String url = deviceInfoBean.data.qrcode;
                Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(url,BGAQRCodeUtil.dp2px(context, 400), R.color.black,null );
                mCodeTv.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(String message, Throwable tr) {
                Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode("二维码异常，请重试", BGAQRCodeUtil.dp2px(context, 400), R.color.black,null );
                mCodeTv.setImageBitmap(bitmap);
            }
        });

    }

    @OnClick(R.id.back_img)
    public void onClick() {
        dismiss();
    }
}
