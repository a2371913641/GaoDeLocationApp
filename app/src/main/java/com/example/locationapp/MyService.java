package com.example.locationapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

import com.example.locationapp.util.ReceiveListener;
import com.example.locationapp.util.SocketClient;

public class MyService extends Service {


    public String TAG = "MyService";
    LocationManager locationManager;
    MyLocationListener myLocationListener;
    double longitude=-1;
    double latitude=-1;
    NotificationManager manager;
    private String channel_id="channelId";
    private int notification_id =100;
    private String provider;
    private ReceiveListener receiveListener;

    public MyService() {

    }

    private void init(){
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗


        provider = locationManager.getBestProvider(criteria, true);
        Log.e(TAG, "定位的provider=" + provider);
        setReceiveListener();

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, "channelName", NotificationManager.IMPORTANCE_HIGH);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            Log.e(TAG, "打开了渠道");
        }else{
            Log.e(TAG, "无法打开渠道");
        }
    }

    class MyIBinder extends IMyAidlInterface.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        public double getLongitude(){
            return longitude;
        }

        public double getLatitude(){
            return latitude;
        }
        public String getProvider(){
            return provider;
        }




    };

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
           stopSelf();
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
               longitude=location.getLongitude();
               latitude=location.getLatitude();
        }

        locationManager.requestLocationUpdates(provider,1000,1,myLocationListener);
        Log.d(TAG, "onCreate: Service created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {

        return new MyIBinder();
    }



   @TargetApi(31)
   private void startForeground(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            stopSelf();
            return;
       }

        try {
            Intent notifactionIntent=new Intent(this,MainActivity.class);
            PendingIntent pi=PendingIntent.getActivity(this,0,notifactionIntent,PendingIntent.FLAG_IMMUTABLE);
            Notification notification = new NotificationCompat.Builder(this, channel_id)
                    .setSmallIcon(R.drawable.tubiao)
                    .setContentTitle("My notification")
                    .setContentText("点击查看好友位置")
                    .setContentIntent(pi)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
            }
            Log.e(TAG,"startForegroundService:type="+type);
            ServiceCompat.startForeground(this, notification_id,notification,type);
            Log.e(TAG,"startForeground():"+"前台服务启动成功");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        Log.e(TAG,"longitude"+longitude+",latitude="+latitude);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }catch (Exception e){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R&&
            e instanceof ForegroundServiceStartNotAllowedException){
                Log.e(TAG,"startForeground():"+"service当前状态无法启动前台服务");
            }

        }


   }

   private void setReceiveListener(){
       SocketClient.getInst().addListener(new ReceiveListener() {
           @Override
           public void onReceive(String s) {

           }
       });
   }



    class MyLocationListener implements LocationListener{
        // 位置改变时获取经纬度
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
           latitude = location.getLatitude();


        }

        // 状态改变时
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG,"onStatusChanged - provider:"+provider +" status:"+status);
        }

        // 提供者可以使用时
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG,"GPS开启了");
        }

        // 提供者不可以使用时
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG,"GPS关闭了");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(myLocationListener); // 停止所有的定位服务
    }

}