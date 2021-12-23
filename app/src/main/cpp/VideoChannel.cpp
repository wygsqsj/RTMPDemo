//
// Created by DELL on 2021/12/22.
//

#include <x264.h>
#include "VideoChannel.h"

//初始化x264
void VideoChannel::createX264Encode(int width, int height, int fps, int bitrate) {
    mWidth = width;
    mHeight = height;
    mFps = fps;
    mBitrate = bitrate;
    mYSize = width * height;
    mUVSize = mYSize / 4;
    //初始化
    if (mVideoCodec) {
        x264_encoder_close(mVideoCodec);
        mVideoCodec = nullptr;
    }
    //类比与MedeaFormat
    x264_param_t param;
    x264_param_default_preset(&param,
                              "ultrafast",//编码器速度,越快质量越低，适合直播
                              "zerolatency"//编码质量
    );
    //编码等级
    param.i_level_idc = 32;
    param.i_csp = X264_CSP_I420;    //nv12
    param.i_width = width;
//    param.i_height = height;
    //设置没有B帧
    param.i_bframe = 0;
    /*
     * 码率控制方式
     * X264_RC_CBR:恒定码率 cpu紧张时画面质量差，以网络传输稳定为先
     * X264_RC_VBR:动态码率，cpu紧张时花费更多时间，画面质量比较均衡，适合本地播放
     * X264_RC_ABR:平均码率，是一种折中方式，也是网络传输中最常用的方式
     *
     */
    param.rc.i_rc_method = X264_RC_ABR;
    //码率，k为单位，所以字节数除以1024
    param.rc.i_bitrate = bitrate / 1024;
    /*
     * 帧率
     * 代表1秒有多少帧
     * 帧率时间
     * 当前帧率为25，那么帧率时间我们一般理解成1/25=40ms
     * 但是帧率的单位不是时间，而是一个我们设定的值 i_fps_den/i_timebase_den
     * 例如当前是1000帧了，他对应的时间戳计算方式为：1000（1/25）
     *
     * 如果你的i_fps_den/i_timebase_den 设置的不是 1/fps,那么最终是以这两个参数为单位计算间隔的,一般我们都会
     * 设置成1/fps
    */
    param.i_fps_num = fps;
    param.i_fps_den = 1;
    param.i_timebase_den = param.i_fps_num;
    param.i_timebase_num = param.i_fps_den;
    //使用fps计算帧间距
    param.b_vfr_input = 0;
    //25帧一个I帧
    param.i_keyint_max = fps * 2;
    //sps和pps自动放到I帧前面
    param.b_repeat_headers = 1;
    //开启多线程
    param.i_threads = 1;
    //编码质量
    x264_param_apply_profile(&param, "baseline");
    //打开编码器
    mVideoCodec = x264_encoder_open(&param);
    //输入缓冲区
    pic_in = new x264_picture_t;
    //初始化缓冲区大小
    x264_picture_alloc(pic_in, X264_CSP_I420, width, height);

}
