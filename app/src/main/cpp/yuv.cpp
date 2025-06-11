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
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "rotate");
    // 获取输入数据指针
    jbyte *data = env->GetByteArrayElements(input, nullptr);
    if (data == nullptr) {
        return nullptr; // 内存错误
    }

    int totalSize = width * height * 3 / 2;
    jbyteArray outputArray = env->NewByteArray(totalSize);
    if (outputArray == nullptr) {
        env->ReleaseByteArrayElements(input, data, 0);
        return nullptr; // 内存分配失败
    }

    std::unique_ptr<jbyte[]> rotatedData(new jbyte[totalSize]);
    memcpy(rotatedData.get(), data, totalSize);

    // 如果不支持的角度，直接返回原图
    bool needRotate = (rotation == 90 || rotation == 180 || rotation == 270);
    if (needRotate) {
        // 创建临时缓冲区
        std::unique_ptr<jbyte[]> tempBuffer(new jbyte[totalSize]);

        // Y 平面大小
        int ySize = width * height;

        // UV 平面宽度和高度
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        switch (rotation) {
            case 90: {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int srcPos = y * width + x;
                        int dstX = height - y - 1;
                        int dstY = x;
                        int dstPos = dstY * height + dstX;
                        tempBuffer[dstPos] = data[srcPos];
                    }
                }

                for (int y = 0; y < uvHeight; ++y) {
                    for (int x = 0; x < uvWidth; ++x) {
                        int srcPos = ySize + (y * uvWidth + x) * 2;
                        int dstX = uvHeight - y - 1;
                        int dstY = x;
                        int dstPos = dstY * uvHeight + dstX;
                        tempBuffer[ySize + dstPos * 2 + 0] = data[srcPos + 1]; // V
                        tempBuffer[ySize + dstPos * 2 + 1] = data[srcPos + 0]; // U
                    }
                }
                break;
            }
            case 180: {
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int srcPos = y * width + x;
                        int dstPos = (height - y - 1) * width + (width - x - 1);
                        tempBuffer[dstPos] = data[srcPos];
                    }
                }

                for (int y = 0; y < uvHeight; ++y) {
                    for (int x = 0; x < uvWidth; ++x) {
                        int srcPos = ySize + (y * uvWidth + x) * 2;
                        int dstPos = (uvHeight - y - 1) * uvWidth + (uvWidth - x - 1);
                        tempBuffer[ySize + dstPos * 2 + 0] = data[srcPos + 0]; // V
                        tempBuffer[ySize + dstPos * 2 + 1] = data[srcPos + 1]; // U
                    }
                }
                break;
            }
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

                for (int y = 0; y < uvHeight; ++y) {
                    for (int x = 0; x < uvWidth; ++x) {
                        int srcPos = ySize + (y * uvWidth + x) * 2;
                        int dstX = x;
                        int dstY = uvWidth - y - 1;
                        int dstPos = dstY * uvHeight + dstX;
                        tempBuffer[ySize + dstPos * 2 + 0] = data[srcPos + 1]; // V
                        tempBuffer[ySize + dstPos * 2 + 1] = data[srcPos + 0]; // U
                    }
                }
                break;
            }
            default:
                break;
        }
        memcpy(rotatedData.get(), tempBuffer.get(), totalSize);
    }

    // 返回结果
    env->SetByteArrayRegion(outputArray, 0, totalSize, rotatedData.get());
    env->ReleaseByteArrayElements(input, data, 0);
    return outputArray;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "yuv", "JNI_OnUnload");
}

}