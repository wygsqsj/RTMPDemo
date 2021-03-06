
package com.example.rtmpdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.rtmpdemo.databinding.ActivityMainBinding;
import com.example.rtmpdemo.mediacodec.MediaCodecActivity;
import com.example.rtmpdemo.x264.CameraXActivity;
import com.example.rtmpdemo.x264.X264Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "音视频";
    public static final String RTMPURL = "rtmp://live-push.bilivideo.com/live-bvc/?streamname=live_253004675_57099388&key=d7bae0426c3adc1eec7c9786d25c99fb&schedule=rtmp&pflag=9";
//    private final String RTMPURL = "rtmp://192.168.28.139:1935/live/?room";


    private ActivityMainBinding binding;

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
            }
        }
    }


    public void medeaCodecStart(View view) {
        Intent intent = new Intent(this, MediaCodecActivity.class);
        this.startActivity(intent);
    }

    public void x264Start(View view) {
        Intent intent = new Intent(this, X264Activity.class);
        this.startActivity(intent);
    }

    public void cameraXStart(View view) {
        Intent intent = new Intent(this, CameraXActivity.class);
        this.startActivity(intent);
    }
}
