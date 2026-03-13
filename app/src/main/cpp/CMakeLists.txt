cmake_minimum_required(VERSION 3.16)

project(imgproc)

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 添加 libyuv 共享库
add_library(libyuv SHARED IMPORTED)
set_target_properties(libyuv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libyuv.so)

# 基础源文件
set(BASE_SOURCES
        vision_core.cpp
        logger.cpp
)

# 合并所有源文件
set(ALL_SOURCES ${BASE_SOURCES})

# 创建单一的共享库目标
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${ALL_SOURCES}
)

# 包含头文件路径
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        # android基础库
        android
        log
        jnigraphics

        # Google高性能yuv处理库
        libyuv
)