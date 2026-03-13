//
// Created by pengx on 2026/3/13.
//

#ifndef XANDROIDDEMO_YUV_HPP
#define XANDROIDDEMO_YUV_HPP

#include "logger.hpp"

class Yuv {
public:
    explicit Yuv();

    static Yuv *get() {
        static Yuv instance;
        return &instance;
    }

    Yuv(const Yuv &) = delete;

    Yuv &operator=(const Yuv &) = delete;

    void rotate(uint8_t *input_ptr, int width, int height, int rotate, uint8_t *output_ptr);

private:
    Logger _logger;
};


#endif //XANDROIDDEMO_YUV_HPP
