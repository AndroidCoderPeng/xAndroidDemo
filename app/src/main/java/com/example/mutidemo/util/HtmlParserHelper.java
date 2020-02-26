package com.example.mutidemo.util;

import com.example.mutidemo.bean.ResultBean;
import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public static String HtmlToJson(Document document) {
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
            categoryBean.setUr(moreLinks);
            categoryBean.setList(beanList);

            categoryBeanList.add(categoryBean);
            resultBean.setBeanList(categoryBeanList);
        }
        return new Gson().toJson(resultBean);
    }
}
