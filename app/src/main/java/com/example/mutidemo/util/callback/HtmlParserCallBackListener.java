package com.example.mutidemo.util.callback;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:40
 */
public interface HtmlParserCallBackListener {
    void onParserDone(Document document) throws IOException;
}
