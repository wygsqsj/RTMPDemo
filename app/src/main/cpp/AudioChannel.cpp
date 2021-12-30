//音频编码类
// Created by DELL on 2021/12/30.
//

#include <malloc.h>
#include <cstring>
#include "AudioChannel.h"
#include "faac.h"

AudioChannel::AudioChannel() {

}

AudioChannel::~AudioChannel() {

}

//实例化音频编码器
void AudioChannel::initCodec(int sampleRate, int channels) {

    //输入的容量大小，要与java层AudioRecord获取的缓冲区大小比较得到缓冲区大小
    unsigned long inputSamples;
    //获取编码器，参数解释：采样率、通道数、输入的音频样本数量（返回给我们）、输出的字节容量（返回给我们）
    codec = faacEncOpen(sampleRate, channels, &inputSamples, &maxOutputBytes);

    //输出的容器
    outputBuffer = new unsigned char[maxOutputBytes];

    //输入的容器的大小，我们的采样位数是16,也就是2个字节，用样本数*2得到输入大小
    inputByteNum = inputSamples * 2;

    //配置参数
    faacEncConfigurationPtr configurationPtr = faacEncGetCurrentConfiguration(codec);

    // 设置编码格式标准, 使用 MPEG4 新标准
    configurationPtr->mpegVersion = MPEG4;
    configurationPtr->aacObjectType = LOW;
    //0 输出aac原始数据 1 添加ADTS头之后的数据
    configurationPtr->outputFormat = 0;
    //采样位数
    configurationPtr->inputFormat = FAAC_INPUT_16BIT;
    //使配置生效
    faacEncSetConfiguration(codec, configurationPtr);
}

//设置rtmp回调
void AudioChannel::setCallBack(AudioCallback callback) {
    this->callbackAudio = callback;
}

//编码音频
void AudioChannel::encode(int8_t *data, int len) {
    LOGI("编码的音频数据大小：%d", len);
    //进行编码 参数：编码器 编码数据，编码数据大小 ，输入容器，输入容器大小
    int encodeLen = faacEncEncode(codec,
                                  reinterpret_cast<int32_t *>(data),
                                  len,
                                  outputBuffer,
                                  maxOutputBytes);
    if (encodeLen > 0) {

        RTMPPacket *packet = new RTMPPacket;
        int body_size = encodeLen + 2;
        RTMPPacket_Alloc(packet, body_size);
        packet->m_body[0] = 0xAF;
        packet->m_body[1] = 0x01;
        memcpy(&packet->m_body[2], outputBuffer, encodeLen);
        //设置音频类型
        packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
        packet->m_nBodySize = body_size;
        //通道值，音视频不能相同
        packet->m_nChannel = 0x05;
        packet->m_nTimeStamp = 0;
        packet->m_hasAbsTimestamp = 0;
        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        callbackAudio(packet);
    }
}

//发送音频头
RTMPPacket *AudioChannel::getAudioHead() {
    LOGI("发送音频头");
    if (!codec) {
        return nullptr;
    }

    u_char *buf;
    u_long len;
    //音频头
    faacEncGetDecoderSpecificInfo(codec, &buf, &len);

    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, len + 2);
    packet->m_body[0] = 0xAF;
    packet->m_body[1] = 0x00;
    memcpy(&packet->m_body[2], buf, len);
    //设置音频类型
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = len + 2;
    //通道值，音视频不能相同
    packet->m_nChannel = 0x05;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;
}
