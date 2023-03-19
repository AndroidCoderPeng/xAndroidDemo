package com.example.mutidemo.model;

import java.util.List;

/**
 * @author Administrator
 * @description TODO
 * @package com.example.mutidemo.bean
 * @date 2022/2/17 23:43
 * @email 290677893@qq.com
 */
public class BannerImageModel {

    /**
     * success : true
     * code : 200
     * message : 请求成功
     * data : [{"imageTitle":"测试标题1","imageLink":"https://images.pexels.com/photos/1036808/pexels-photo-1036808.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"},{"imageTitle":"测试标题2","imageLink":"https://images.pexels.com/photos/796602/pexels-photo-796602.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"},{"imageTitle":"测试标题3","imageLink":"https://images.pexels.com/photos/1109543/pexels-photo-1109543.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"},{"imageTitle":"测试标题4","imageLink":"https://images.pexels.com/photos/296115/pexels-photo-296115.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"},{"imageTitle":"测试标题5","imageLink":"https://images.pexels.com/photos/4158/apple-iphone-smartphone-desk.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500"}]
     */

    private boolean success;
    private int code;
    private String message;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * imageTitle : 测试标题1
         * imageLink : https://images.pexels.com/photos/1036808/pexels-photo-1036808.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500
         */
        private String imageTitle;
        private String imageLink;

        public String getImageTitle() {
            return imageTitle;
        }

        public void setImageTitle(String imageTitle) {
            this.imageTitle = imageTitle;
        }

        public String getImageLink() {
            return imageLink;
        }

        public void setImageLink(String imageLink) {
            this.imageLink = imageLink;
        }
    }
}
