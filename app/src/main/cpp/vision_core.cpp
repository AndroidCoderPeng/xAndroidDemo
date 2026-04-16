//
// Created by pengx on 2026/3/13.
//

#include <jni.h>
#include <cstring>

#include "logger.hpp"
#include "yuv.hpp"

static std::unique_ptr<Logger> logger_ptr;

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    logger_ptr = std::make_unique<Logger>("JNI-Yuv");
    logger_ptr->i("JNI_OnLoad");
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_android_util_VisionCore_rotateYuv(JNIEnv *env, jobject thiz,
                                                        jobject input, jint width, jint height,
                                                        jint rotate, jobject output) {
    if (!input || !output || width <= 0 || height <= 0) {
        logger_ptr->e("Invalid input parameters");
        return;
    }

    // 获取 Direct Buffer 地址（无拷贝）
    auto *input_ptr = static_cast<uint8_t *>(env->GetDirectBufferAddress(input));
    auto *output_ptr = static_cast<uint8_t *>(env->GetDirectBufferAddress(output));

    if (!input_ptr || !output_ptr) {
        logger_ptr->e("Failed to get direct buffer address");
        return;
    }

    // 验证旋转角度
    if (rotate != 0 && rotate != 90 && rotate != 180 && rotate != 270) {
        logger_ptr->eFmt("Invalid rotation angle: %d, only 0/90/180/270 supported", rotate);
        return;
    }

    Yuv::get()->rotate(input_ptr, width, height, rotate, output_ptr);
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    if (logger_ptr) {
        logger_ptr.reset();
    }
    logger_ptr->i("JNI_OnUnload");
}