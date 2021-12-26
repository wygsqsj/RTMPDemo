package com.example.rtmpdemo.util;


import android.os.Environment;

import com.example.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/06/22
 *     desc  : utils about file io
 * </pre>
 */


public class FileUtil {

    public static FileOutputStream fos;

    public static synchronized void writeBytes(byte[] data) {
        try {
            if (fos == null) {
                File out264File = new File(App.getInstance().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "demo.h264");
                out264File.createNewFile();
                fos = new FileOutputStream(out264File);
            }
            fos.write(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void destory() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
