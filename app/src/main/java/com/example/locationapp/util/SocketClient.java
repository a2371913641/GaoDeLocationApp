package com.example.locationapp.util;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {
    String TAG="SocketClient";
    Socket socket;
    private ReaderThread readerThread;
    private Writer writer;
    private  static SocketClient sInst=null;
    List<ReceiveListener> listenerList=new ArrayList<>();
    //isStart==0--未开始    isStart==1--开始
    private int isStart=0;
    // 1. socketClient newSocket
    // 2. writeThread ↑  3. readThread  ↑
    //
    // 23 -> 1
    // synchronized
    // 23 -> newSocket

    public static SocketClient getInst(){
        if(sInst==null) {
            synchronized (SocketClient.class) {
                if (sInst == null) {
                    sInst = new SocketClient();
                    sInst.initSocket();
                }
            }
        }
        return sInst;
    }

    public synchronized  Socket initSocket() {
        Log.e(TAG,"initSocket0000000000");

        if (this.socket == null || this.socket.isClosed()) {

            try {

                socket = new Socket(GongGongZiYuan.ServerIP, 8000);
                Log.e("SocketClient", "衣联网1,socket=" + socket.hashCode());
                setStart();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"e!!!!");

            }
        }
        Log.e(TAG,"initSocket:this.socket.isClosed()="+this.socket.isClosed());
        Log.e(TAG,"initSocket11111111111");
        return socket;
    }

    public Writer getWriter(){
        return this.writer;
    }

    public void addListener(ReceiveListener l){
        listenerList.add(l);
        Log.e(TAG,"listener.hashCode="+l.hashCode());
    }
    public void destroyLintener(ReceiveListener l){
        Log.e("SocketClient","remove"+(listenerList.size()-1));
        listenerList.remove(l);
    }

    public void allDestoryListener(){
        Log.e(TAG,"allDestoryListener");
        for(int i=listenerList.size()-1;i>=0;i=i-1){
            listenerList.remove(i);
        }
    }

    public int getListenerListSize(){
        for(int i=0;i<listenerList.size();i++){
            Log.e(TAG,"getListenerListSize["+i+"]="+listenerList.get(i).hashCode());
        }
        return listenerList.size();
    }

    public void setStart(){
        readerThread=new ReaderThread(this,listenerList);
        Log.e(TAG,"setStart()   readerThread.hashCode()="+readerThread.hashCode());
        readerThread.start();
        writer =new Writer(this);
        Log.e(TAG,"setStart()   writer.hashCode()="+writer.hashCode());
        isStart=1;
        Log.e(TAG,"开始");
    }

//    public void setEnd() {
//        Log.e(TAG,"setEnd()");
//        readerThread.setJieshu(true);
//        Log.e(TAG,"setEnd  readerThread="+readerThread.hashCode());
//        writer.setJieshu(true);
//        Log.e(TAG,"setEnd  writer="+writer.hashCode());
//        isStart=0;
////        Looper.loop();
//        try {
//            socket.close();
//            socket=null;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
