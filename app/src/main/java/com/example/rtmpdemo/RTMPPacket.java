package com.example.rtmpdemo;

/**
 * RTMP数据包
 */
public class RTMPPacket {
    private byte[] buffer;
    private long tms;//时间戳

    public RTMPPacket(byte[] outData, long l) {
        this.buffer = outData;
        this.tms = l;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public long getTms() {
        return tms;
    }
}
