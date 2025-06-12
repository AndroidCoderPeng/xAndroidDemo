//
// Created by pengx on 2025/6/11.
//

#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <__memory/unique_ptr.h>

void rotate_nv21_90(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                    int width, int height) {
    // Y plane: 旋转 90 度
    for (int x = 0; x < width; ++x) {
        for (int y = 0; y < height; ++y) {
            y_dst[(height - y - 1) * width + x] = y_src[y * width + x];
        }
    }

    // VU plane: 旋转 90 度（尺寸为 width/2 × height/2）
    for (int x = 0; x < width / 2; ++x) {
        for (int y = 0; y < height / 2; ++y) {
            vu_dst[(height / 2 - y - 1) * width + x * 2] = vu_src[(y * width / 2 + x) * 2 + 1]; // V
            vu_dst[(height / 2 - y - 1) * width + x * 2 + 1] = vu_src[(y * width / 2 + x) * 2]; // U
        }
    }
}

void rotate_nv21_180(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                     int width, int height) {
    for (int i = 0; i < width * height; ++i) {
        y_dst[i] = y_src[width * height - 1 - i];
    }

    for (int i = 0; i < width * height / 2; ++i) {
        vu_dst[i] = vu_src[width * height / 2 - 1 - i];
    }
}

void rotate_nv21_270(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                     int width, int height) {
    for (int x = 0; x < width; ++x) {
        for (int y = 0; y < height; ++y) {
            y_dst[y * width + x] = y_src[(height - x - 1) * width + y];
        }
    }

    for (int x = 0; x < width / 2; ++x) {
        for (int y = 0; y < height / 2; ++y) {
            vu_dst[y * width + x * 2] = vu_src[((height / 2 - x - 1) * width / 2 + y) * 2 + 1]; // V
            vu_dst[y * width + x * 2 + 1] = vu_src[((height / 2 - x - 1) * width / 2 + y) * 2]; // U
        }
    }
}

extern "C" {
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "JNI_OnLoad");
    return JNI_VERSION_1_4;
}

JNIEXPORT jobject JNICALL
Java_com_example_android_util_Yuv_rotate(JNIEnv *env, jobject thiz,
                                         jbyteArray input, jint width, jint height,
                                         jint rotation) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "rotate: width=%d, height=%d", width, height);
    jbyte *data = env->GetByteArrayElements(input, nullptr);
    if (!data) return nullptr;

    int y_plane_size = width * height;
    int u_plane_size = width / 2 * height / 2;
    int v_plane_size = width / 2 * height / 2;
    int total_size = y_plane_size + u_plane_size + v_plane_size;
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "rotate: total size=%d", total_size);

    // 分配输出内存
    auto *out_data = new jbyte[total_size];

    auto *src_y = reinterpret_cast<uint8_t *>(data);
    uint8_t *src_vu = reinterpret_cast<uint8_t *>(data) + y_plane_size;

    auto *dst_y = reinterpret_cast<uint8_t *>(out_data);
    uint8_t *dst_vu = reinterpret_cast<uint8_t *>(out_data) + (width * height);

    switch (rotation) {
        case 90:
            rotate_nv21_90(src_y, src_vu, dst_y, dst_vu, width, height);
            break;
        case 180:
            rotate_nv21_180(src_y, src_vu, dst_y, dst_vu, width, height);
            break;
        case 270:
            rotate_nv21_270(src_y, src_vu, dst_y, dst_vu, width, height);
            break;
        default:
            memcpy(out_data, data, total_size); // 不旋转直接复制
            break;
    }
    jbyteArray result = env->NewByteArray(total_size);
    env->SetByteArrayRegion(result, 0, total_size, out_data);

    delete[] out_data;
    env->ReleaseByteArrayElements(input, data, 0);

    return result;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "JNI_OnUnload");
}

}