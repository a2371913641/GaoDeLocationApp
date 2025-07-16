package com.example.locationapp.util;

import android.util.Log;

import com.example.locationapp.ClientTest;
import com.example.locationapp.HaoYou;
import com.example.locationapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GongGongZiYuan {
    private static String TAG="GongGongZiYuan";
    public static String ServerIP="172.22.246.9";
    private List<ClientTest> clientTests=new ArrayList<>();
    private List<HaoYou> haoYouList=new ArrayList<>();
    public GongGongZiYuan(){
        setClientTests();
        setHaoYouList();
    }

    private void setClientTests(){

        clientTests.add(new ClientTest("吸吸","123456","2371913641", R.mipmap.client1_icon,106.80,32.70));
        clientTests.add(new ClientTest("加载","123456","252539783",R.mipmap.client2_icon,108.80,34.05));

    }

    private void setHaoYouList(){
        haoYouList.add(new HaoYou("李明","1234567890",39.906217, 116.3912757,R.mipmap.friend1_icon));
        haoYouList.add(new HaoYou("表妹","13259283100",22.84,112.07,R.mipmap.friend2_icon));
        haoYouList.add(new HaoYou("吸吸","2371913641",32.70,106.80,R.mipmap.client1_icon));
        haoYouList.add(new HaoYou("加载","252539783",34.27,108.95,R.mipmap.client2_icon));
    }

    public List<ClientTest> getClientTests(){
        return clientTests;
    }

    public List<HaoYou> getHaoYouList(){
        return haoYouList;
    }

    public static void sendMsg(String data) {
        try {
            Log.e(TAG,"data");
            SocketClient.getInst().getWriter().setData(data+"_");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
