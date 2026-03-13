//
// Created by pengx on 2026/3/13.
//

#include <jni.h>
#include <cstring>

#include "logger.hpp"
#include "libyuv.h"

// 预分配旋转缓冲区（线程局部存储，最大支持 2K 分辨率）
static constexpr int MAX_WIDTH = 2560;
static constexpr int MAX_HEIGHT = 1440;
static constexpr int MAX_YUV_SIZE = MAX_WIDTH * MAX_HEIGHT * 3 / 2;

// 线程局部存储，每个线程有自己的缓冲区，避免竞争
thread_local static std::vector<uint8_t> i420_src;
thread_local static std::vector<uint8_t> i420_dst;
thread_local static bool buffers_initialized = false;

static std::unique_ptr<Logger> logger_ptr;

static void ensure_buffers_initialized() {
    if (!buffers_initialized) {
        i420_src.resize(MAX_YUV_SIZE);
        i420_dst.resize(MAX_YUV_SIZE);
        buffers_initialized = true;
    }
}

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

    // 计算旋转后的宽高
    int dst_width = (rotate == 90 || rotate == 270) ? height : width;
    int dst_height = (rotate == 90 || rotate == 270) ? width : height;

    // 0度旋转：直接复制，避免格式转换
    if (rotate == 0) {
        int y_size = width * height;
        int uv_size = y_size / 2;
        memcpy(output_ptr, input_ptr, y_size + uv_size);
        return;
    }

    // 确保缓冲区已初始化（只执行一次）
    ensure_buffers_initialized();

    int y_size = width * height;
    int uv_size = y_size / 4;

    // 使用预分配的缓冲区（无需重新分配内存）
    uint8_t *src_y = i420_src.data();
    uint8_t *src_u = src_y + y_size;
    uint8_t *src_v = src_u + uv_size;

    uint8_t *dst_y = i420_dst.data();
    uint8_t *dst_u = dst_y + dst_width * dst_height;
    uint8_t *dst_v = dst_u + dst_width * dst_height / 4;

    // 1. NV21 -> I420
    int ret = libyuv::NV21ToI420(input_ptr, width,
                                 input_ptr + y_size, width,
                                 src_y, width,
                                 src_u, width / 2,
                                 src_v, width / 2,
                                 width, height);
    if (ret != 0) {
        logger_ptr->e("NV21ToI420 failed");
        return;
    }

    // 2. 旋转 I420
    ret = libyuv::I420Rotate(src_y, width,
                             src_u, width / 2,
                             src_v, width / 2,
                             dst_y, dst_width,
                             dst_u, dst_width / 2,
                             dst_v, dst_width / 2,
                             width, height, static_cast<libyuv::RotationMode>(rotate));
    if (ret != 0) {
        logger_ptr->e("I420Rotate failed");
        return;
    }

    // 3. I420 -> NV21
    ret = libyuv::I420ToNV21(dst_y, dst_width,
                             dst_u, dst_width / 2,
                             dst_v, dst_width / 2,
                             output_ptr, dst_width,
                             output_ptr + dst_width * dst_height, dst_width,
                             dst_width, dst_height);
    if (ret != 0) {
        logger_ptr->e("I420ToNV21 failed");
    }
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    if (logger_ptr) {
        logger_ptr.reset();
    }
    logger_ptr->i("JNI_OnUnload");
}