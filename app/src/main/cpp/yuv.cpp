//
// Created by pengx on 2025/6/11.
//

#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <__memory/unique_ptr.h>

void rotate_nv21_90(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                    int width, int height) {
    // 把原始yuv（一维数组）转为临时矩阵，C++不支持动态二维数组
    auto y_temp = std::make_unique<uint8_t[]>(width * height);
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            y_temp[j * height + i] = y_src[i * width + j];
        }
    }

    // 矩阵转置
    for (int i = 0; i < width; ++i) {
        for (int j = 0; j < height; ++j) {
            y_dst[i * height + j] = y_temp[i * height + (height - 1 - j)];
        }
    }

    int uv_width = width / 2;
    int uv_height = height / 2;
    auto uv_temp = std::make_unique<uint8_t[]>(uv_width * uv_height * 2); // 每个像素是 VU 对
    for (int i = 0; i < uv_height; ++i) {
        for (int j = 0; j < uv_width; ++j) {
            int src_index = (i * width + j * 2); // 源中的 VU 对起始位置
            uv_temp[(j * uv_height + i) * 2] = vu_src[src_index];     // V
            uv_temp[(j * uv_height + i) * 2 + 1] = vu_src[src_index + 1]; // U
        }
    }

    // 矩阵转置
    int dst_index = 0;
    for (int i = 0; i < uv_width; ++i) {
        for (int j = uv_height - 1; j >= 0; --j) {
            vu_dst[dst_index++] = uv_temp[(i * uv_height + j) * 2];     // V
            vu_dst[dst_index++] = uv_temp[(i * uv_height + j) * 2 + 1]; // U
        }
    }
}

void rotate_nv21_180(const uint8_t *y_src, const uint8_t *vu_src, uint8_t *y_dst, uint8_t *vu_dst,
                     int width, int height) {
    int y_size = width * height;
    int vu_size = (width * height) / 2;

    // 数组翻转（比通过矩阵变换要快：矩阵得先垂直转置90，再水平转置90）
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
    auto y_temp = std::make_unique<uint8_t[]>(width * height);
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            y_temp[j * height + i] = y_src[i * width + j];
        }
    }

    // 矩阵转置
    for (int i = 0; i < width; ++i) {
        for (int j = 0; j < height; ++j) {
            y_dst[i * height + j] = y_temp[(width - 1 - i) * height + j];
        }
    }

    int uv_width = width / 2;
    int uv_height = height / 2;
    auto uv_temp = std::make_unique<uint8_t[]>(uv_width * uv_height * 2);
    for (int i = 0; i < uv_height; ++i) {
        for (int j = 0; j < uv_width; ++j) {
            int src_index = (i * width + j * 2);
            uv_temp[(j * uv_height + i) * 2] = vu_src[src_index];     // V
            uv_temp[(j * uv_height + i) * 2 + 1] = vu_src[src_index + 1]; // U
        }
    }

    int dst_index = 0;
    for (int i = 0; i < uv_width; ++i) {
        for (int j = 0; j < uv_height; ++j) {
            vu_dst[dst_index++] = uv_temp[(uv_width - 1 - i) * uv_height * 2 + j * 2];     // V
            vu_dst[dst_index++] = uv_temp[(uv_width - 1 - i) * uv_height * 2 + j * 2 + 1]; // U
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