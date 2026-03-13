//
// Created by pengx on 2026/3/13.
//

#include "logger.hpp"

#include <algorithm>

void Logger::print_border(const char *left, const char *fill, const char *right,
                          const LogLevel level) const {
    std::string border;
    border += left;
    for (int i = 0; i < DEFAULT_WIDTH; ++i) {
        border += fill;
    }
    border += right;
    log_raw(level, border.c_str());
}

void Logger::log_raw(const LogLevel level, const char *msg) const {
    __android_log_print(static_cast<int>(level), _tag_ptr, "%s", msg);
}

void Logger::print_line(const std::string &content, const LogLevel level) const {
    std::string line;
    line += V_LINE;
    line += " ";
    line += content;
    log_raw(level, line.c_str());
}

void Logger::print_box(const LogLevel level, const char *content) const {
    print_border(TOP_LEFT, H_LINE, TOP_RIGHT, level);
    print_line(content, level);
    print_border(BOTTOM_LEFT, H_LINE, BOTTOM_RIGHT, level);
}

// 简单单行边框
void Logger::d(const char *msg) const {
    print_box(LogLevel::DEBUG, msg);
}

void Logger::i(const char *msg) const {
    print_box(LogLevel::INFO, msg);
}

void Logger::w(const char *msg) const {
    print_box(LogLevel::WARN, msg);
}

void Logger::e(const char *msg) const {
    print_box(LogLevel::ERROR, msg);
}

Logger::BoxBuilder &Logger::BoxBuilder::add(const std::string &line) {
    _lines.push_back(line);
    return *this;
}

Logger::BoxBuilder &Logger::BoxBuilder::addBlock(const std::string &content) {
    std::istringstream stream(content);
    std::string line;
    while (std::getline(stream, line)) {
        _lines.push_back(line);
    }
    return *this;
}

void Logger::BoxBuilder::print() const {
    if (_lines.empty())
        return;

    // 上边框
    _logger.print_border(TOP_LEFT, H_LINE, TOP_RIGHT, _level);

    // 内容行
    for (const auto &line: _lines) {
        _logger.print_line(line, _level);
    }

    // 下边框
    _logger.print_border(BOTTOM_LEFT, H_LINE, BOTTOM_RIGHT, _level);
}

Logger::BoxBuilder Logger::box(LogLevel level) {
    return {*this, level};
}
