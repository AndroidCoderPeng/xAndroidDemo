//
// Created by pengx on 2025/6/11.
//

#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <__memory/unique_ptr.h>

extern "C" {
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "JNI_OnLoad");
    return JNI_VERSION_1_4;
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_android_util_Yuv_rotate(JNIEnv *env, jobject thiz,
                                         jbyteArray input, jint width, jint height,
                                         jint rotation) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "rotate: width=%d, height=%d", width, height);
    jbyte *data = env->GetByteArrayElements(input, nullptr);
    if (!data) return nullptr;

    int y_plane_size = width * height;
    int u_plane_size = width / 2 * height / 2;
    int v_plane_size = width / 2 * height / 2;
    int totalSize = y_plane_size + u_plane_size + v_plane_size;
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "rotate: totalSize=%d", totalSize);

    jbyteArray output = env->NewByteArray(totalSize);
    if (!output) {
        env->ReleaseByteArrayElements(input, data, 0);
        return nullptr;
    }

    std::unique_ptr<jbyte[]> rotated_nv21(new jbyte[totalSize]);
    // 复制到缓存中
    memcpy(rotated_nv21.get(), data, totalSize);

    if (rotation == 90 || rotation == 180 || rotation == 270) {
        std::unique_ptr<jbyte[]> tempBuffer(new jbyte[totalSize]);

        int uv_plane_width = width / 2;
        int uv_plane_height = height / 2;

        switch (rotation) {
//            case 90: {
//                // Y Plane - Rotate 90 degrees clockwise
//                for (int y = 0; y < height; ++y) {
//                    for (int x = 0; x < width; ++x) {
//                        int srcPos = y * width + x;
//                        int dstX = height - y - 1;
//                        int dstY = x;
//                        int dstPos = dstY * height + dstX;
//                        tempBuffer[dstPos] = data[srcPos];
//                    }
//                }
//
//                // UV Plane - Rotate 90 degrees clockwise
//                for (int y = 0; y < uvHeight; ++y) {
//                    for (int x = 0; x < uvWidth; x += 2) {
//                        int srcPos = ySize + y * uvWidth + x;
//                        int dstX = uvHeight - y - 1;
//                        int dstY = x / 2;
//                        int dstPos = ySize + dstY * uvHeight + dstX * 2;
//                        tempBuffer[dstPos + 0] = data[srcPos + 1]; // V
//                        tempBuffer[dstPos + 1] = data[srcPos + 0]; // U
//                    }
//                }
//                break;
//            }
//            case 180: {
//                // Y Plane - Rotate 180 degrees
//                for (int y = 0; y < height; ++y) {
//                    for (int x = 0; x < width; ++x) {
//                        int srcPos = y * width + x;
//                        int dstPos = (height - y - 1) * width + (width - x - 1);
//                        tempBuffer[dstPos] = data[srcPos];
//                    }
//                }
//
//                // UV Plane - Rotate 180 degrees
//                for (int y = 0; y < uvHeight; ++y) {
//                    for (int x = 0; x < uvWidth; x += 2) {
//                        int srcPos = ySize + y * uvWidth + x;
//                        int dst_y = uvHeight - y - 1;
//                        int dst_x = uvWidth - x - 2;
//                        int dstPos = ySize + dst_y * uvWidth + dst_x;
//                        tempBuffer[dstPos + 0] = data[srcPos + 1]; // V
//                        tempBuffer[dstPos + 1] = data[srcPos + 0]; // U
//                    }
//                }
//                break;
//            }
            case 270: {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int srcPos = y * width + x;
                        int dstX = y;
                        int dstY = width - x - 1;
                        int dstPos = dstY * height + dstX;
                        tempBuffer[dstPos] = data[srcPos];
                    }
                }

                for (int y = 0; y < uv_plane_height; ++y) {
                    for (int x = 0; x < uv_plane_width; x++) {
                        int srcPos = y_plane_size + y * uv_plane_width + x * 2;
                        // 新图像尺寸：new_width = height, new_height = width
                        int newX = y;
                        int newY = uv_plane_width - x - 1;

                        int dstPos = y_plane_size + newY * uv_plane_width + newX * 2;
                        tempBuffer[dstPos + 0] = data[srcPos + 1]; // V
                        tempBuffer[dstPos + 1] = data[srcPos + 0]; // U
                    }
                }
                break;
            }
            default:
                break;
        }
        memcpy(rotated_nv21.get(), tempBuffer.get(), totalSize);
    }

    env->SetByteArrayRegion(output, 0, totalSize, rotated_nv21.get());
    //释放资源
    env->ReleaseByteArrayElements(input, data, 0);
    return output;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "JNI_OnUnload");
}

}