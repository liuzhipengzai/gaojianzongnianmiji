package com.example.administrator.gaojianzongnianmiji;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.administrator.gaojianzongnianmiji.base.MainActivity;

/**
 * Created by Administrator on 2018/4/28.
 * 开机广播
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent ootStartIntent = new Intent(context, MainActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }
    }
}
