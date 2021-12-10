package com.example.rtmpdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.example.rtmpdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    public static String LOG_TAG = "音视频";
    private final String BILIBILIURL = "rtmp://live-push.bilivideo.com/live-bvc/?streamname=live_253004675_57099388&key=d7bae0426c3adc1eec7c9786d25c99fb&schedule=rtmp&pflag=9";
//    private final String BILIBILIURL = "rtmp://192.168.28.138:1935/live/?room";

    private MediaProjection mMediaProjection;    //录屏api
    private MediaProjectionManager mediaManager;

    private Camera2Helper helper;

    private ActivityMainBinding binding;
    private ScreenLive screenLive;
    private VideoCodec codec;

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


    //开始录屏
    public void startRecording(View view) {
        initMediaProjection();
    }

    public void screenRecording(View view) {
        if (screenLive != null) {
            screenLive.stopLive();
        }
    }

    private void initMediaProjection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = mediaManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, 666);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == RESULT_OK) {
            mMediaProjection = mediaManager.getMediaProjection(resultCode, data);
            screenLive = new ScreenLive();
            screenLive.startLive(BILIBILIURL, mMediaProjection);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
//        helper = new Camera2Helper(this, binding.textureView);
//        helper.start();
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        binding.textureView.setSurfaceTextureListener(this);
    }


}