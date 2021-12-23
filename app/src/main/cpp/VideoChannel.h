//
// Created by DELL on 2021/12/22.
//视频编码工具类
//
#include <inttypes.h>
#include <stdint.h>
#include "x264.h"
#include "RTMP_LOG.h"

class VideoChannel {

public:
    VideoChannel();

    ~VideoChannel();

    void createX264Encode(int width, int height, int fps, int bitrate);

    void encodeData(int8_t *data);

private:
    int mWidth{};
    int mHeight{};
    int mFps{};
    int mBitrate{};
    int mYSize{};
    int mUVSize{};
    //x264编码器
    x264_t *mVideoCodec = nullptr;
    //输入缓冲区，类比与MediaCodec的inputBuffer
    x264_picture_t *pic_in = nullptr;
};