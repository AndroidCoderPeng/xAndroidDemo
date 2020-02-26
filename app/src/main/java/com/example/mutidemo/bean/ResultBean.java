package com.example.mutidemo.bean;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 16:32
 */
public class ResultBean {

    private List<CategoryBean> beanList;

    public List<CategoryBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<CategoryBean> beanList) {
        this.beanList = beanList;
    }

    public static class CategoryBean {
        private String title;
        private String url;
        private List<ListBean> list;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {

            private String childTitle;
            private String childPicture;
            private String childUrl;

            public String getChildUrl() {
                return childUrl;
            }

            public void setChildUrl(String childUrl) {
                this.childUrl = childUrl;
            }

            public String getChildPicture() {
                return childPicture;
            }

            public void setChildPicture(String childPicture) {
                this.childPicture = childPicture;
            }

            public String getChildTitle() {
                return childTitle;
            }

            public void setChildTitle(String childTitle) {
                this.childTitle = childTitle;
            }
        }
    }
}