package com.example.locationapp.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Writer {
    String TAG="Writer";
    Socket socket;
    volatile Boolean jieshu=false;
    static volatile String data=null;
    public Handler wHandler;
    HandlerThread handlerThread;
    SocketClient socketClient;
    // 负责发送
    // 主动触发, 调用一个方法来发送
    public Writer(SocketClient socketClient){
        super();
        this.socketClient = socketClient;
        handlerThread=new HandlerThread("huoqvxiaoxi");
        handlerThread.start();
        wHandler=new Handler(handlerThread.getLooper());
        Log.e(TAG,"wHandler="+wHandler.hashCode());
    }

    public void setData(String d) throws IOException {
        wHandler.post(new Runnable() {
            @Override
            public void run() {
                if (socket == null) {
                    socket = socketClient.initSocket();
                    Log.e(TAG,"socketClient="+socketClient.hashCode()+",socket="+socket.hashCode());
                }

                if (socket.isClosed()) {
                    // 判断当前Socket是否可用
                    socket = socketClient.initSocket();
                    Log.e(TAG,"socket.isClosed()=:"+socketClient.hashCode()+",socket="+socket.hashCode());
                }



                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(d.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                } catch (IOException e) {
                    Log.e(TAG,e.getLocalizedMessage());

                    Log.e(TAG,"writer.hashCode="+this.hashCode()+"   发送数据失败");
                    e.fillInStackTrace();
                }
            }
        });

    }




    // 1. 判空并等待
    // 2. 设计好流程， 保证它不是空
//

    public void setJieshu(Boolean jieshu){
        this.jieshu=jieshu;
    }
}
