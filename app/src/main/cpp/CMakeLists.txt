cmake_minimum_required(VERSION 3.16)

project(yuv)

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(${CMAKE_PROJECT_NAME} SHARED
        yuv.cpp
        logger.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        # android基础库
        android
        log
        jnigraphics
)