package com.example.mutidemo.bean;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/26 14:00
 */
public class PhotoBean {
    private String photoTitle;//图片标题
    private List<PhotoURL> urlList;//缩略图地址集合

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public List<PhotoURL> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<PhotoURL> urlList) {
        this.urlList = urlList;
    }

    private static class PhotoURL {
        private String bigImageUrl;//大图地址

        public String getBigImageUrl() {
            return bigImageUrl;
        }

        public void setBigImageUrl(String bigImageUrl) {
            this.bigImageUrl = bigImageUrl;
        }
    }
}
