//
// Created by pengx on 2026/3/13.
//

#ifndef XANDROIDDEMO_LOGGER_HPP
#define XANDROIDDEMO_LOGGER_HPP

#include <android/log.h>
#include <cstdarg>
#include <sstream>
#include <string>
#include <vector>

enum class LogLevel {
    DEBUG = ANDROID_LOG_DEBUG,
    INFO = ANDROID_LOG_INFO,
    WARN = ANDROID_LOG_WARN,
    ERROR = ANDROID_LOG_ERROR
};

class Logger {
public:
    explicit Logger(const char *tag) : _tag_ptr(tag) {}

    // ========== 简单单行边框日志 ==========
    void d(const char *msg) const;

    void i(const char *msg) const;

    void w(const char *msg) const;

    void e(const char *msg) const;

    // ========== 带格式的边框日志 ==========
    template<typename... Args>
    void dFmt(const char *fmt, Args... args) {
        log_formatted(LogLevel::DEBUG, fmt, args...);
    }

    template<typename... Args>
    void iFmt(const char *fmt, Args... args) {
        log_formatted(LogLevel::INFO, fmt, args...);
    }

    template<typename... Args>
    void wFmt(const char *fmt, Args... args) {
        log_formatted(LogLevel::WARN, fmt, args...);
    }

    template<typename... Args>
    void eFmt(const char *fmt, Args... args) {
        log_formatted(LogLevel::ERROR, fmt, args...);
    }

    // ========== 多行内容边框日志（流式API） ==========
    // 使用方式: box().add("行1").add("行2").print();
    class BoxBuilder {
    public:
        BoxBuilder(Logger &logger, LogLevel level) : _logger(logger), _level(level) {}

        // 添加一行内容
        BoxBuilder &add(const std::string &line);

        // 添加多行内容（针对sdp或者xml这种带有换行的内容块）
        BoxBuilder &addBlock(const std::string &content);

        // 添加格式化行
        __attribute__((format(printf, 2, 3)))
        BoxBuilder &addFmt(const char *fmt, ...) {
            char buffer[256];
            va_list args;
            va_start(args, fmt);
            vsnprintf(buffer, sizeof(buffer), fmt, args);
            va_end(args);
            return add(buffer);
        }

        // 打印到logcat
        void print() const;

    private:
        Logger &_logger;
        LogLevel _level;
        std::vector<std::string> _lines;
    };

    // 构建多行边框
    BoxBuilder box(LogLevel level = LogLevel::INFO);

    BoxBuilder dBox() {
        return box(LogLevel::DEBUG);
    }

    BoxBuilder iBox() {
        return box(LogLevel::INFO);
    }

    BoxBuilder wBox() {
        return box(LogLevel::WARN);
    }

    BoxBuilder eBox() {
        return box(LogLevel::ERROR);
    }

private:
    const char *_tag_ptr;
    static constexpr auto DEFAULT_WIDTH = 48;
    static constexpr auto H_LINE = "─";
    static constexpr auto V_LINE = "│";
    static constexpr auto TOP_LEFT = "┌";
    static constexpr auto TOP_RIGHT = "┐";
    static constexpr auto BOTTOM_LEFT = "└";
    static constexpr auto BOTTOM_RIGHT = "┘";

    void print_border(const char *left, const char *fill, const char *right, LogLevel level) const;

    void print_line(const std::string &content, LogLevel level) const;

    void log_raw(LogLevel level, const char *msg) const;

    template<typename... Args>
    void log_formatted(const LogLevel level, const char *fmt, Args... args) {
        char buffer[512];
        snprintf(buffer, sizeof(buffer), fmt, args...);
        print_box(level, buffer);
    }

    void print_box(LogLevel level, const char *content) const;
};

#endif //XANDROIDDEMO_LOGGER_HPP
