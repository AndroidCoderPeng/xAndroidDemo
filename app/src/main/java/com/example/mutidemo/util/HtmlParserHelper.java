package com.example.mutidemo.util;

import com.example.mutidemo.bean.PhotoBean;
import com.example.mutidemo.bean.ResultBean;
import com.example.mutidemo.util.callback.HtmlParserCallBackListener;
import com.example.mutidemo.util.callback.PhotoParserCallBackListener;
import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 18:24
 */
public class HtmlParserHelper {

    /**
     * 解析Html数据
     */
    public static String getCategoryList(Document document) {
        Element parentTag = document.getElementsByClass("main_cont").first();//仅一个节点
        Elements elementsByClass = parentTag.getElementsByClass("list_cont list_cont2 w1180");
        ResultBean resultBean = new ResultBean();
        List<ResultBean.CategoryBean> categoryBeanList = new ArrayList<>();
        for (int i = 1; i < elementsByClass.size(); i++) {
            ResultBean.CategoryBean categoryBean = new ResultBean.CategoryBean();
            //获取父级标题和链接
            Element childTitleElement = elementsByClass.get(i).getElementsByClass("tit clearfix").first();
            String title = childTitleElement.text().substring(0, 6).replace("手机", "");
            String moreLinks = childTitleElement.getElementsByClass("tit_more").first().attr("abs:href");

            //获取子级列表
            Element childListElement = elementsByClass.get(i).getElementsByClass("tab_tj").first();
            Elements elementsByTag = childListElement.getElementsByTag("li");
            List<ResultBean.CategoryBean.ListBean> beanList = new ArrayList<>();
            for (Element e : elementsByTag) {
                String childTitle = e.text();
                String childPicture = e.select("img[data-original]").first().attr("data-original");
                String childUrl = e.select("a[href]").first().attr("href");

                ResultBean.CategoryBean.ListBean bean = new ResultBean.CategoryBean.ListBean();
                bean.setChildTitle(childTitle);
                bean.setChildPicture(childPicture);
                bean.setChildUrl(childUrl);
                beanList.add(bean);
            }
            categoryBean.setTitle(title);
            categoryBean.setUrl(moreLinks);
            categoryBean.setList(beanList);

            categoryBeanList.add(categoryBean);
            resultBean.setBeanList(categoryBeanList);
        }
        return new Gson().toJson(resultBean);
    }

    /**
     * 解析刷新或者加载更多的Html数据
     * <p>
     * 需要重新分析html解析
     */
    public static String getLoadMoreList(Document document) {
        Element parentTag = document.getElementsByClass("main_cont").first();//仅一个节点
        Elements elementsByClass = parentTag.getElementsByClass("list_cont Left_list_cont  Left_list_cont2");
        ResultBean resultBean = new ResultBean();
        List<ResultBean.CategoryBean> categoryBeanList = new ArrayList<>();
        for (int i = 0; i < elementsByClass.size(); i++) {
            ResultBean.CategoryBean categoryBean = new ResultBean.CategoryBean();
            Element childListElement = elementsByClass.get(i).getElementsByClass("tab_tj").first();
            Elements elementsByTag = childListElement.getElementsByTag("li");
            List<ResultBean.CategoryBean.ListBean> beanList = new ArrayList<>();
            for (Element e : elementsByTag) {
                String childTitle = e.text();
                String childPicture = e.select("img[data-original]").first().attr("data-original");
                String childUrl = e.select("a[href]").first().attr("href");

                ResultBean.CategoryBean.ListBean bean = new ResultBean.CategoryBean.ListBean();
                bean.setChildTitle(childTitle);
                bean.setChildPicture(childPicture);
                bean.setChildUrl(childUrl);
                beanList.add(bean);
            }
            categoryBean.setTitle("");
            categoryBean.setUrl("");
            categoryBean.setList(beanList);

            categoryBeanList.add(categoryBean);
            resultBean.setBeanList(categoryBeanList);
        }
        return new Gson().toJson(resultBean);
    }

    public static void getPictureList(Document document, PhotoParserCallBackListener listener) {
        Element parentElement = document.getElementsByClass("scroll-img scroll-img02 clearfix").first();
        Elements childElement = parentElement.getElementsByTag("li");

        List<PhotoBean.Result> resultList = new ArrayList<>();
        PhotoBean photoBean = new PhotoBean();
        for (Element element : childElement) {
            //获取大图的地址
            String title = element.select("img[title]").first().attr("title");

            //根据大图地址获取大图
            PhotoBean.Result result = new PhotoBean.Result();
            String s = element.select("a[href]").first().attr("href");

            HttpHelper.captureHtmlData(s, new HtmlParserCallBackListener() {

                @Override
                public void onParserDone(Document document) throws IOException {
                    //解析大图地址
                    Element bigPictureElement = document.getElementsByClass("pic-large").first();
                    String bigPicture = bigPictureElement.select("img[src]").first().attr("src");

                    result.setBigImageUrl(bigPicture);
                    resultList.add(result);

                    photoBean.setPhotoTitle(title);
                    photoBean.setPhotoNumber(childElement.size());
                    photoBean.setList(resultList);

                    if (resultList.size() == childElement.size()) {
                        listener.onPictureDone(photoBean);
                    }
                }
            });
        }
    }
}
