//
// Created by pengx on 2025/6/11.
//

#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <__memory/unique_ptr.h>

void rotate_nv21_90(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                    int width, int height) {
    for (int col = 0; col < width; ++col) {
        int offset = (height - 1) * width + col;
        for (int row = 0; row < height; ++row) {
            *y_dst++ = y_src[offset];
            offset -= width;
        }
    }

    int uv_width = width / 2;
    int uv_height = height / 2;

    for (int y = uv_height - 1; y >= 0; --y) {
        for (int x = uv_width - 1; x >= 0; --x) {
            int src_offset = y * width + x * 2;
            *vu_dst++ = vu_src[src_offset + 1]; // V
            *vu_dst++ = vu_src[src_offset];     // U
        }
    }
}

void rotate_nv21_180(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                     int width, int height) {
    int y_size = width * height;
    int vu_size = (width * height) / 2;

    // Rotate Y component (reverse all pixels)
    for (int i = 0; i < y_size; ++i) {
        y_dst[i] = y_src[y_size - 1 - i];
    }

    // Rotate VU component (reverse all U/V pairs)
    for (int i = 0; i < vu_size; i += 2) {
        int src_index = vu_size - i - 2;
        vu_dst[i] = vu_src[src_index];
        vu_dst[i + 1] = vu_src[src_index + 1];
    }
}

void rotate_nv21_270(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                     int width, int height) {
    // Rotate Y component (270 degrees CCW)
    for (int x = width - 1; x >= 0; --x) {
        int offset = 0;
        for (int y = 0; y < height; ++y) {
            *y_dst++ = y_src[offset + x];
            offset += width;
        }
    }

    // Rotate VU component (270 degrees CCW)
    int uv_width = width / 2;
    int uv_height = height / 2;

    for (int x = uv_width - 1; x >= 0; --x) {
        int offset = 0;
        for (int y = 0; y < uv_height; ++y) {
            *vu_dst++ = vu_src[offset + x * 2];     // U
            *vu_dst++ = vu_src[offset + x * 2 + 1]; // V
            offset += width;
        }
    }
}

extern "C" {
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "JNI_OnLoad");
    return JNI_VERSION_1_4;
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_android_util_Yuv_rotate(JNIEnv *env, jobject thiz,
                                         jbyteArray input, jint width, jint height, jint rotation) {
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