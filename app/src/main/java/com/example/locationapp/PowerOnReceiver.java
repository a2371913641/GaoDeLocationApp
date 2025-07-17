package com.example.locationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class PowerOnReceiver extends BroadcastReceiver {

    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals(ACTION_BOOT_COMPLETED)){
           Intent intent1=new Intent(context,MyService.class);

           //针对Android 8.0及以上版本
           if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
               context.startForegroundService(intent1);
           }else{
               context.startService(intent1);
           }

           Toast.makeText(context,"好友定位系统已启动！",Toast.LENGTH_SHORT).show();
       }
    }
}