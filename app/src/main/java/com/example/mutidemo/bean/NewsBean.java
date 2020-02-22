package com.example.mutidemo.bean;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:27
 */
public class NewsBean {

    /**
     * code : 10000
     * charge : false
     * msg : 查询成功
     * result : {"status":0,"msg":"ok","result":{"channel":"头条","num":10,"list":[{"title":"快递全面复工10天了 为何快递依然\u201c睡\u201d在路上？","time":"2020-02-21 01:32:00","src":"中新经纬","category":"tech","pic":"https://n.sinaimg.cn/tech/transform/667/w400h267/20200221/f4e8-ipvnsze1922030.jpg","content":"我想整个快递行业的业态也会逐渐做出一些调整或者创新。<\/p>","url":"https://tech.sina.cn/2020-02-21/detail-iimxyqvz4579754.d.html?vt=4&pos=108","weburl":"https://tech.sina.com.cn/roll/2020-02-21/doc-iimxyqvz4579754.shtml"}]}}
     */

    private String code;
    private boolean charge;
    private String msg;
    private ResultBeanX result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBeanX getResult() {
        return result;
    }

    public void setResult(ResultBeanX result) {
        this.result = result;
    }

    public static class ResultBeanX {
        /**
         * status : 0
         * msg : ok
         * result : {"channel":"头条","num":10,"list":[{"title":"快递全面复工10天了 为何快递依然\u201c睡\u201d在路上？","time":"2020-02-21 01:32:00","src":"中新经纬","category":"tech","pic":"https://n.sinaimg.cn/tech/transform/667/w400h267/20200221/f4e8-ipvnsze1922030.jpg","content":"我想整个快递行业的业态也会逐渐做出一些调整或者创新。<\/p>","url":"https://tech.sina.cn/2020-02-21/detail-iimxyqvz4579754.d.html?vt=4&pos=108","weburl":"https://tech.sina.com.cn/roll/2020-02-21/doc-iimxyqvz4579754.shtml"}]}
         */

        private int status;
        private String msg;
        private ResultBean result;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public ResultBean getResult() {
            return result;
        }

        public void setResult(ResultBean result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * channel : 头条
             * num : 10
             * list : [{"title":"快递全面复工10天了 为何快递依然\u201c睡\u201d在路上？","time":"2020-02-21 01:32:00","src":"中新经纬","category":"tech","pic":"https://n.sinaimg.cn/tech/transform/667/w400h267/20200221/f4e8-ipvnsze1922030.jpg","content":"我想整个快递行业的业态也会逐渐做出一些调整或者创新。<\/p>","url":"https://tech.sina.cn/2020-02-21/detail-iimxyqvz4579754.d.html?vt=4&pos=108","weburl":"https://tech.sina.com.cn/roll/2020-02-21/doc-iimxyqvz4579754.shtml"}]
             */

            private String channel;
            private int num;
            private List<ListBean> list;

            public String getChannel() {
                return channel;
            }

            public void setChannel(String channel) {
                this.channel = channel;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                /**
                 * title : 快递全面复工10天了 为何快递依然“睡”在路上？
                 * time : 2020-02-21 01:32:00
                 * src : 中新经纬
                 * category : tech
                 * pic : https://n.sinaimg.cn/tech/transform/667/w400h267/20200221/f4e8-ipvnsze1922030.jpg
                 * content : 我想整个快递行业的业态也会逐渐做出一些调整或者创新。</p>
                 * url : https://tech.sina.cn/2020-02-21/detail-iimxyqvz4579754.d.html?vt=4&pos=108
                 * weburl : https://tech.sina.com.cn/roll/2020-02-21/doc-iimxyqvz4579754.shtml
                 */

                private String title;
                private String time;
                private String src;
                private String category;
                private String pic;
                private String content;
                private String url;
                private String weburl;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public String getSrc() {
                    return src;
                }

                public void setSrc(String src) {
                    this.src = src;
                }

                public String getCategory() {
                    return category;
                }

                public void setCategory(String category) {
                    this.category = category;
                }

                public String getPic() {
                    return pic;
                }

                public void setPic(String pic) {
                    this.pic = pic;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getWeburl() {
                    return weburl;
                }

                public void setWeburl(String weburl) {
                    this.weburl = weburl;
                }
            }
        }
    }
}
