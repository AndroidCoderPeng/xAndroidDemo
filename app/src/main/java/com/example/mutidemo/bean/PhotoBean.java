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
    private int photoNumber;
    private List<Result> list;

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public int getPhotoNumber() {
        return photoNumber;
    }

    public void setPhotoNumber(int photoNumber) {
        this.photoNumber = photoNumber;
    }

    public List<Result> getList() {
        return list;
    }

    public void setList(List<Result> list) {
        this.list = list;
    }

    public static class Result {
        private String bigImageUrl;//大图地址

        public String getBigImageUrl() {
            return bigImageUrl;
        }

        public void setBigImageUrl(String bigImageUrl) {
            this.bigImageUrl = bigImageUrl;
        }
    }
}
