package com.example.locationapp.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IOUtil {
    static String TAG="IOUtil";
    public static File createFile(String AS) {
        File file=new File(AS);
        if(!file.exists()){
            try {
                if(file.createNewFile()){
                    Log.e(TAG,"IOUtil:创建"+file.getName()+"文件成功");
                }else{
                   Log.e(TAG,"IOUtil:创建"+file.getName()+"文件失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;

    }



    public static void deleteFile(String AS) {
        File file = new File(AS);
        if (file.delete()) {
           Log.e("IOUtil.delete",file.getName()+"删除文件成功");
        } else {
            Log.e("IOUtil.delete","删除文件失败");
        }

    }

    public static void renameFile(String oldName, String newName) {
        File oldFile = new File(oldName);
        File newFile = new File(newName);
        if (oldFile.renameTo(newFile)) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败 ");
        }

    }

    //
    public static File outputFile(String AS, String content,boolean append) {
//        Log.e("outputFile", "fileName " + FileName);
        File file=new File(AS);
        Log.e(TAG,"outputFile: fileName="+file.getName()+"  file.as="+AS);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fos=new FileOutputStream(AS,append);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
//            Log.e("outPutStream","outPutStream:"+append+"");
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String inputFile(String AS){
        File file=new File(AS);
        if(!file.exists()){
            createFile(AS);
        }

        StringBuilder sb=new StringBuilder();
        try {
            FileInputStream fis=new FileInputStream(AS);
            byte[] bytes=new byte[1024];
            int hasRead;
            while ((hasRead=fis.read(bytes))>0){
                sb.append(new String(bytes,0,hasRead));
            }
            fis.close();
            return sb.toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    //获取文件名 return byte[]
    public static  byte[] getFileName(String fileName){
        byte[] bytes=fileName.getBytes(StandardCharsets.UTF_8);
        return bytes;
    }

    //将content传给Server
    public static void outputData(byte[] content,DataOutputStream dataOutputStream){
        try {

            dataOutputStream.write(content,0,content.length);
            System.out.println("outputData:"+new String(content));
            dataOutputStream.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void dataOutputLong(long l,DataOutputStream dataOutputStream){

        try {
            dataOutputStream.writeLong(l);
            System.out.println("dataOutputLong:"+l);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public static void dataOutputString(String s,DataOutputStream dataOutputStream){
        try {
            dataOutputStream.writeBytes(s);
            System.out.println("dataOutputString:"+s);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }



    //获取file内容
    public static byte[] getData(File file) {
        byte[] bytes=new byte[(int)file.length()];
        try {
            FileInputStream fip=new FileInputStream(file);
            fip.read(bytes);
            fip.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
//            Log.e(TAG,"getData()");
        }
        return bytes;

    }


    //new一个子线程在里面传输文件
    public static void socketOutputFileInNewThread(File file,String fileName, String ip, int port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socketOutputFile(file,fileName,ip,port);
            }
        }).start();
    }

    //将file穿给Server
    public static void socketOutputFile(File file,String fileName, String ip, int port){

        long startTime = System.currentTimeMillis();
        try {
            Socket socket = new Socket(ip,port);
//            Log.e(TAG,"socket="+socket.hashCode());
            String s="client->server File";

            DataInputStream dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//            Log.e(TAG,"       "+(long)s.length());
            dataOutputLong(s.length(),dataOutputStream);
            dataOutputString("client->server File",dataOutputStream);
            dataOutputLong(fileName.getBytes(StandardCharsets.UTF_8).length,dataOutputStream);
            outputData(getFileName(fileName),dataOutputStream);
            dataOutputLong(file.length(),dataOutputStream);
            outputData(getData(file),dataOutputStream);
            System.out.println("---------------------------------------");
            //判断是否结束
            if(isCloseSocket(dataInputStream,socket)){
                System.out.println("正常关闭socket="+socket.hashCode());
            }else{
                System.out.println("非正常关闭socket="+socket.hashCode());
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            Log.e(TAG,"socketOutputFile耗时: " + duration + " 毫秒");
            GongGongZiYuan.sendMsg("申请File:/n"+fileName);
        } catch (IOException e) {

            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }

    //读取内容
    public static byte[] inputContent(DataInputStream dataInputStream,long l){

        int i=0;
        byte[] contentBytes=new byte[2048];
        System.out.println("inputContent:l="+l);
        try {

            byte[] bytes=new byte[1];

            for(i=0;i<l;i++){
                System.out.println(TAG+":l="+l+"      i="+i);
                if(dataInputStream.read(bytes)!=-1) {
                    contentBytes[i] = bytes[0];
                }
            }


        } catch (IOException e) {
            System.out.println("IOUTIL:inputContent");
            throw new RuntimeException(e);
        }

        byte[] content=new byte[i];
        System.arraycopy(contentBytes,0,content,0,i);
        return content;
    }

    //是否关闭socket
    public static Boolean isCloseSocket(DataInputStream dataInputStream,Socket socket){
        //判断是否结束
        long l;
        byte[] isCloseBytes;
        String isClose;
        try {
            l=dataInputStream.readLong();

        System.out.println("l="+l);
        if (l > 100)
            l =100;
        isCloseBytes=inputContent(dataInputStream,l);
        isClose=new String(isCloseBytes);
        System.out.println("isClose="+isClose);
        if (new String(isCloseBytes).equals("断开连接")) {
            socket.close();
            System.out.println("socket断开连接");
            return true;
        }else{
            socket.close();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }








    // 客户端  socket   write（server.get File）                                             read(file.lenth)      read(file)
    // 服务端 ssocket                          read(server.get File)   write(file.length)                                              write(file)

    //从server获取file
    public static File socketInputFlie(File file,String ip,int port){

        long startTime = System.currentTimeMillis();

        try {
            Socket socket=new Socket(ip,port);
            DataInputStream dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            //发送信号
            sendShortLinkSignalToProcessed(dataOutputStream,"client->server.get File");


            //发送fileName
            sendFileName(dataOutputStream,file);

            //获取文件内容
            socketInputWriteFileContent(dataInputStream,file);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            Log.e(TAG,"socketInputFile耗时: " + duration + " 毫秒");
            //判断是否结束
            if(isCloseSocket(dataInputStream,socket)){
                System.out.println("正常关闭socket="+socket.hashCode());
                GongGongZiYuan.sendMsg("client->server图片下载完成:");
            }else{
                System.out.println("非正常关闭socket="+socket.hashCode());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return file;
    }


    public static File socketInputFileInNewThread(String fileAP,String ip,int port){

        File file=createFile(fileAP);

        Thread inputFileThread=new Thread(new Runnable() {
            @Override
            public void run() {
                socketInputFlie(file,ip,port);
            }
        });
        inputFileThread.start();


        return file;
    }


    //从server中读取文件内容，同时写入文件
    public static File socketInputWriteFileContent(DataInputStream dataInputStream,File file){
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            long fileLength=dataInputStream.readLong();
            System.out.println("socketInputWriteFileContent:"+"fileLength="+fileLength);
            long readLength=0;
            byte[] readBigBytes=new byte[8192];
            byte[] readSmallBytes=new byte[1];
//            System.out.println(TAG+"socketInputWriteFileContent:"+"bigCirculation="+bigCirculation+"     smallCirclation="+smallCirculation);
            long onceReadLength=0;
//            for(int i=0;i<bigCirculation;i++){
//                onceReadLength=dataInputStream.read(readBigBytes);
//                fileOutputStream.write(readBigBytes,0, (int) onceReadLength);
//                readLength=readLength+onceReadLength;
//                System.out.println("socketInputWriteFileContent:"+"bigCirculation:readLength="+readLength+"    i="+i);
//            }
//            for(int i=0;i<smallCirculation;i++){
//                onceReadLength=dataInputStream.read(readSmallBytes);
//                fileOutputStream.write(readSmallBytes,0, (int) onceReadLength);
//                readLength=readLength+onceReadLength;
//                System.out.println("socketInputWriteFileContent:"+"readLength="+readLength+"    i="+i);
//            }
            while((fileLength-readLength)>8192){
                onceReadLength=dataInputStream.read(readBigBytes);
                fileOutputStream.write(readBigBytes,0, (int) onceReadLength);
                readLength=readLength+onceReadLength;
            }

            while (readLength<fileLength){
                onceReadLength=dataInputStream.read(readSmallBytes);
                Log.e(TAG,"最后一次读取onceReadLength="+onceReadLength);
                fileOutputStream.write(readSmallBytes, 0, (int) onceReadLength);
                readLength=readLength+onceReadLength;
            }

            System.out.println("socketInputWriteFileContent:smallCirculation:"+"readLength="+readLength);
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //发送短链接需要处理的信号
    public static void sendShortLinkSignalToProcessed(DataOutputStream dataOutputStream,String s){

        try {
            dataOutputStream.writeLong(s.getBytes().length);
            dataOutputStream.write(s.getBytes());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //发送文件名
    public static void sendFileName(DataOutputStream dataOutputStream,File file){
        System.out.println("socketInputFlie"+"fileName="+file.getName());
        try {
            dataOutputStream.writeLong(file.getName().getBytes().length);
            dataOutputStream.write(file.getName().getBytes());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
