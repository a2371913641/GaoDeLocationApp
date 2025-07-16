package com.example.locationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.locationapp.util.GongGongZiYuan;
import com.example.locationapp.util.IOUtil;
import com.example.locationapp.util.MyApplication;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MAinActivity";
    IMyAidlInterface myIBinder = null;
    double longitude = -1;
    double latitude = -1;
    String provider="null";
    MyServiceConnect connect = null;
    TextView longitudeTextView, latitudeTextView,providerTextView;
    Button startForegroundServiceButton, bindForegroundServiceButton, unbindServiceButton, stopServiceButton;
    private Handler handler = null;
    private boolean update=false;
    private MapView mMapView=null;
    private AMap aMap=null;
    private MyLocationStyle myLocationStyle;
    private LocationListener mListener;
    private Menu menu;
    private List<HaoYou> haoYouList;
    private ImageView imageView;
    private ImageButton  outflotButton;
    private GongGongZiYuan gongGongZiYuan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyPermission();
        init();
        startForegroundService();
        bindForegroundService();
//        setUnbindServiceButton();
//        setStopServiceButton();

        //
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if(aMap==null){
            aMap=mMapView.getMap();
            initMyLocationStyle(myLocationStyle,R.mipmap.my_icon);
            setUpLocationListener(latitude,longitude);
            setHaoYouIconShow();
        }
        setOutflotButton();


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(intent.getDoubleExtra("latitude",-1)!=-1){
            setFirstPosition(intent.getDoubleExtra("latitude",-1),intent.getDoubleExtra("longitude",-1));
        }
    }

    private void init() {
        gongGongZiYuan=new GongGongZiYuan();
        mMapView=(MapView)findViewById(R.id.map);
//        providerTextView=(TextView)findViewById(R.id.main_provider_textview);
//        longitudeTextView = (TextView) findViewById(R.id.main_longitude_textview);
//        latitudeTextView = (TextView) findViewById(R.id.main_latitude_textview);
        menu=(Menu)findViewById(R.id.main_menu);
        imageView=(ImageView)findViewById(R.id.main_ImageView);
        imageView.setImageBitmap(ImageCircleProcessor.processToCircle(this,R.mipmap.my_icon));
//        startForegroundServiceButton = (Button) findViewById(R.id.main_start_foreground_service);
//        bindForegroundServiceButton = (Button) findViewById(R.id.main_bind_foreground_service);
//        unbindServiceButton = (Button) findViewById(R.id.main_unbind_foreground_service);
//        stopServiceButton = (Button) findViewById(R.id.main_stop_foreground_service);
        handler = new Handler();
        setHaoYouList();
        outflotButton=(ImageButton) findViewById(R.id.main_over_flot_button);


    }

    private void setOutflotButton(){
        outflotButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    //显示弹出菜单
    private void showPopupMenu(View anchorView){
        // 创建PopupMenu实例，传入上下文和锚点视图
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        // 从menu资源文件加载菜单项
        popupMenu.getMenuInflater().inflate(R.menu.main_menu_layout, popupMenu.getMenu());
        // 设置菜单项点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        popupMenu.show();
    }

    private void setHaoYouList(){
        haoYouList=gongGongZiYuan.getHaoYouList();
    }

    private void setHaoYouIconShow(){
        for(int i=0;i<haoYouList.size();i++){
            Log.e(TAG,"haoyouList.size="+haoYouList.size());
             // 设置第一个定位图标的经纬度
            haoYouList.get(i).markerOption.position(new LatLng(haoYouList.get(i).latitude,haoYouList.get(i).longitude));
            haoYouList.get(i).markerOption.icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.createCustomMarker(this,
                    "昵称："+haoYouList.get(i).name+"\n纬度："+haoYouList.get(i).latitude+"\n经度："+haoYouList.get(i).longitude,haoYouList.get(i).image)));
            aMap.addMarker(haoYouList.get(i).markerOption);
            Log.e(TAG," aMap.addMarker(haoYouList.get(i).markerOption); la="+haoYouList.get(i).latitude+"   long="+haoYouList.get(i).longitude);
        }
    }

    private void initMyLocationStyle(MyLocationStyle myLocationStyle,int imageResource){
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//           myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //连续定位，定位点旋转但地图不移动

//这里我在上面的imageView中用了R.mapmip.my_icon这下面的代码就一直报错： Caused by: java.lang.IllegalArgumentException: cannot use a recycled source in createBitmap
//未解
//        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromBitmap(ImageCircleProcessor.processToCircle(this,imageResource));
//        myLocationStyle.myLocationIcon(customIcon);
        // 应用样式到地图(只有添加这一部，我们为myLocationStyle设置的定位方式和样式才有效）
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        // 开启定位蓝点显示
        aMap.setMyLocationEnabled(true);
    }

    private void setUpLocationListener(double latitude,double longitude){
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                // 手动构造定位数据（示例：北京天安门经纬度）
                Location manualLocation = new Location("manual");
                // 设置指定经纬度（北纬39.9042°，东经116.4074°）
                manualLocation.setLatitude(latitude);
                manualLocation.setLongitude(longitude);
                // 设置定位时间（必须大于0）
                manualLocation.setTime(System.currentTimeMillis());
                // 传递模拟的定位数据到地图，更新蓝点位置
                if (mListener != null) {
                    mListener.onLocationChanged(manualLocation);
                }

//                // （可选）移动地图视角到指定经纬度
//                LatLng targetLatLng = new LatLng(latitude,longitude);
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 16));
//                //第二个参数指的是地图的缩放级别
            }
        });
    }



    private void startForegroundService(){

        Context context = getApplicationContext();
        Intent intent = new Intent(MainActivity.this, MyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
            Log.e(TAG,"setStartForegroundServiceButton() :"+"MainActivity.this.startForegroundService(intent);");
        } else {
            Log.e(TAG,"setStartForegroundServiceButton() :"+"无法开启service");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Build.VERSION.SDK_INT < Build.VERSION_CODES.O", Toast.LENGTH_SHORT).show();
                }});
        }

    }

    private void bindForegroundService() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, connect, Context.BIND_AUTO_CREATE);

    }

//    private void setUnbindServiceButton() {
//        unbindServiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                unbindService(connect);
//                Toast.makeText(MainActivity.this, "取消绑定service", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void setStopServiceButton() {
//        stopServiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MyService.class);
//                stopService(intent);
//                Toast.makeText(MainActivity.this, "销毁service", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS
            }, 1);
        } else {
            connect = new MyServiceConnect();
        }
    }

    class MyServiceConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myIBinder = IMyAidlInterface.Stub.asInterface(service);
            Log.e(TAG, "onServiceConnected:myIBinder");
            update=true;
            try {
                Log.e(TAG,"setFirstPosition(!!!!!!!!!!);");
                setFirstPosition(myIBinder.getLatitude(),myIBinder.getLongitude());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            setUpdate();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myIBinder = null;
        }
    }

    private void setFirstPosition(double latitude,double longitude){
        // （可选）移动地图视角到指定经纬度
        LatLng targetLatLng = new LatLng(latitude,longitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 16));
        //第二个参数指的是地图的缩放级别
    }


    private void setTextView(double longitude, double latitude,String provider) {
        longitudeTextView.setText("longitude:" + longitude);
        latitudeTextView.setText("latitude:" + latitude);
        providerTextView.setText("provider="+provider);
    }

    private void setUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                        if (myIBinder != null && update) {
                            Log.e(TAG, "myIBinder!=null");

                            longitude = myIBinder.getLongitude();
                            latitude = myIBinder.getLatitude();
                            provider=myIBinder.getProvider();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    setTextView(longitude, latitude,provider);
                                }
                            });
                            Log.e(TAG, "onServiceConnected" + "坐标更新");


                        } else {
                            Log.e(TAG, "myIBinder==null");
                            break;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onServiceDisconnected:myIBinder=null");
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu_layout,menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_set_personal_information:
                Intent intent;
                if(new File(new File(MainActivity.this.getFilesDir(), "已登录.txt").getAbsolutePath()).exists()){
                    intent=new Intent(this,SecondSetPersonalInformationActivity.class);
                }else {
                    intent = new Intent(this, SixLogActivity.class);
                }
                startActivity(intent);

                break;

            case R.id.main_menu_add_friend:
                Intent intent1 = new Intent(this, FourAddFriendActivity.class);
                startActivity(intent1);
                break;

            case R.id.main_menu_friend:
                Intent intent2 = new Intent(this,FiveFriendActivity.class);
                startActivity(intent2);

                break;

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connect);
        myIBinder=null;
        update=false;
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

//    private void setMenuIsNewInformation(boolean isNewInformation){
//        // 创建 BadgeDrawable 实例
//        BadgeDrawable badgeDrawable=new BadgeDrawable()
//    }


}