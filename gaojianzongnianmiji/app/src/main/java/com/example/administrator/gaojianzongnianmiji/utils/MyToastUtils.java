package com.example.administrator.gaojianzongnianmiji.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.gaojianzongnianmiji.App;
import com.example.administrator.gaojianzongnianmiji.R;
import java.lang.ref.WeakReference;

public class MyToastUtils {
    private static WeakReference<Toast> mToastRef = null;

    public static void showMyToast(String content) {
        showMyToast(content, "", false, false);
    }

    public static void showMyToast(String content, String content1, boolean isSuccess, boolean showImg) {
        Toast toast;
        TextView textView, textView1;
        ImageView imageView;
        Context context = App.instance();
        if (mToastRef != null && (toast = mToastRef.get()) != null) {
            textView = toast.getView().findViewById(R.id.tv_content);
            textView1 = toast.getView().findViewById(R.id.tv_content1);
            imageView = toast.getView().findViewById(R.id.iv_img);
        } else {
            LayoutInflater inflate = LayoutInflater.from(context);
            View layout = inflate.inflate(R.layout.toast_tips, null);
            textView = layout.findViewById(R.id.tv_content);
            textView1 = layout.findViewById(R.id.tv_content1);
            imageView = layout.findViewById(R.id.iv_img);
            toast = new Toast(context);
            toast.setGravity(Gravity.CENTER | Gravity.FILL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            mToastRef = new WeakReference<>(toast);
        }
        textView.setText(content);
        if (showImg) {
            textView1.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView1.setText(content1);
            imageView.setImageDrawable(isSuccess ? context.getResources().getDrawable(R.mipmap.ic_success) : context.getResources()
                    .getDrawable(R.mipmap.ic_fall));
        } else {
            textView1.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }
        toast.show();

    }
}
