package com.example.rtmpdemo.x264;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.example.rtmpdemo.util.LiveTaskManager;

import static com.example.rtmpdemo.MainActivity.LOG_TAG;

/**
 * 采集音频数据推流到服务器
 */
public class AudioHelper extends Thread {

    private AudioRecord audioRecord;
    private boolean isRecoding;

    /**
     * 通道数
     */
    private final int channelCount = 2;
    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    private final int SAMPLE_RATE_INHZ = 44100;

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     * CHANNEL_IN_STEREO 双通道使用
     */
    private int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 缓冲区，此处的最小buffer只能作为参考值，不同于MedeaCodec我们可以直接使用此缓冲区大小，当设备不支持硬编时
     * getMinBufferSize会返回-1，所以还要根据faac返回给我们的输入区大小来确定
     * faac会返回给我们一个缓冲区大小，将他和此缓冲区大小比较之后采用最大值
     */
    private int minBufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
    private long startTime;
    //传输层
    private LivePush livePush;
    private byte[] buffer;


    public void startLive(LivePush livePush) {
        this.livePush = livePush;
        int inputByteNum = livePush.native_initAudioCodec(SAMPLE_RATE_INHZ, channelCount);
        Log.i(LOG_TAG, "初始化faac完成，获取到输入缓冲区大小：" + inputByteNum);
        minBufferSize = Math.max(inputByteNum, minBufferSize);
        Log.i(LOG_TAG, "初始化faac完成，最终的输入缓冲区大小：" + minBufferSize);
        //输入容器，AudioRecord获取到的音频数据
        buffer = new byte[minBufferSize];
        try {
            //初始化录音器
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_INHZ,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    minBufferSize);
            LiveTaskManager.getInstance().execute(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //开始录音
        audioRecord.startRecording();
        isRecoding = true;

        if (startTime == 0) {
            startTime = System.currentTimeMillis();//得到时间，毫秒
        }

        //不断的读取数据
        while (isRecoding) {
            int len = audioRecord.read(buffer, 0, buffer.length);
            if (livePush != null && len > 0) {
                livePush.native_pushAudio(buffer, len);
            }
        }

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        startTime = 0;
    }

    public void stopAudio() {
        isRecoding = false;
    }
}
