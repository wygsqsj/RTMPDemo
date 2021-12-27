package com.example.rtmpdemo.x264;

import android.util.Log;

import static com.example.rtmpdemo.MainActivity.LOG_TAG;

/**
 * 软编推流，与native连接的类
 */
public class LivePush {

    static {
        System.loadLibrary("native-lib");
    }

    public LivePush() {
        native_init();
    }

    public void startLive(String rtmpurl) {
        native_start(rtmpurl);
    }


    public void stopLive() {
        native_stop();
        native_release();
    }


    public native void native_init();

    public native void native_start(String url);

    public native void native_setVideoEncInfo(int width, int height, int fps, int bitrate);

    public native void native_pushVideo(byte[] data);

    public native void native_stop();

    public native void native_release();

}
