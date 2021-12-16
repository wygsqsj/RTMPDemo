
package com.example.rtmpdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rtmpdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    public static String LOG_TAG = "音视频";
    public static final String BILIBILIURL = "rtmp://live-push.bilivideo.com/live-bvc/?streamname=live_253004675_57099388&key=d7bae0426c3adc1eec7c9786d25c99fb&schedule=rtmp&pflag=9";
//    private final String BILIBILIURL = "rtmp://192.168.28.139:1935/live/?room";

    private Camera2Helper helper;

    private ActivityMainBinding binding;
    private ScreenLive screenLive;
    private VideoCodec codec;
    private AudioCodec audioCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                        }, 666);
            } else {
                binding.textureView.setSurfaceTextureListener(this);
            }
        } else {
            binding.textureView.setSurfaceTextureListener(this);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
       startRTMP();
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.closeCamera();
        }

        if (audioCodec != null) {
            audioCodec.stopAudio();
        }

        if (screenLive != null) {
            screenLive.stopLive();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        binding.textureView.setSurfaceTextureListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRTMP() {
        //发送层
        screenLive = new ScreenLive();
        screenLive.startLive(BILIBILIURL);
        //视频数据
        helper = new Camera2Helper(this, binding.textureView, screenLive);
        helper.start();
//        //音频数据
        audioCodec = new AudioCodec();
        audioCodec.startLive(screenLive);
    }
}
