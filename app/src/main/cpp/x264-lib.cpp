#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG    "音视频"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_example_rtmpdemo_x264_LivePush_native_1init(JNIEnv *env, jobject thiz) {
    // TODO: implement native_init()
}