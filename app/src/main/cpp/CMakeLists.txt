project(yuv)

cmake_minimum_required(VERSION 3.10)

add_library(yuv SHARED yuv.cpp)

find_library(log-lib log)

target_link_libraries(yuv ${log-lib})