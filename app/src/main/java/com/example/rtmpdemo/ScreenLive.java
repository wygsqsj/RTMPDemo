package com.example.rtmpdemo;

import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.concurrent.LinkedBlockingQueue;

import static com.example.rtmpdemo.MainActivity.LOG_TAG;

/**
 * 推流服务
 */
public class ScreenLive extends Thread {


    static {
        System.loadLibrary("native-lib");
    }

    //数据
    private LinkedBlockingQueue<RTMPPacket> queue = new LinkedBlockingQueue<>();

    private boolean isLive = true;
    private String url;
    private MediaProjection mediaProjection;
    private VideoCodec videoCodec;

    public void addPacket(RTMPPacket packet) {
        if (isLive) {
            queue.add(packet);
        }
    }

    //开启推送模式
    public void startLive(String url, MediaProjection mediaProjection) {
        this.url = url;
        this.mediaProjection = mediaProjection;
        start();
    }

    //开启推送模式
    public void startLive(String url) {
        this.url = url;
        start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        super.run();
        if (!connect(url)) {
            Log.i(LOG_TAG, "RTMP服务器连接失败");
            return;
        }
        while (isLive) {
            RTMPPacket packet = null;
            try {
                packet = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (packet != null && packet.getBuffer() != null && packet.getBuffer().length != 0) {
                Log.i(LOG_TAG, "获取到编码数据，开始推送:" + packet.getTms());
                sendData(packet.getBuffer(), packet.getBuffer().length, packet.getTms());
            }
        }

    }

    public void stopLive() {
        isLive = false;
        if (videoCodec != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                videoCodec.stopCode();
            }
        }
    }

    //连接rtmp服务器
    private native boolean connect(String url);

    //推送数据
    private native boolean sendData(byte[] data, int len, long tms);


}
