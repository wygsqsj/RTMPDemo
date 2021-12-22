package com.example.rtmpdemo.x264;

/**
 * 软编推流，与native连接的类
 */
public class LivePush {

    static {
        System.loadLibrary("x264-lib");
    }

    public LivePush() {
        native_init();
    }

    public void stopLive() {
    }

    public void startLive(String rtmpurl) {
//        native_start(rtmpurl);
    }

    public native void native_init();
//
//    public native void native_start(String url);
//
//    public native void native_setVideoEncInfo(int width, int height, int fps, int bitrate);
//
//    public native void native_pushVideo(byte[] data);
//
//    public native void native_stop();
//
//    public native void native_release();

}
