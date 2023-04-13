package com.example.mutidemo.model;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/2/21 19:27
 */
public class NewsListModel {

    /**
     * code : 10000
     * charge : false
     * msg : 查询成功
     * result : {"status":0,"msg":"ok","result":{"channel":"头条","num":10,"list":[{"title":"联播+丨加快建设科技强国 总书记有最新部署","time":"2021-05-28 22:15:00","src":"央视","category":"news","pic":"https://n.sinaimg.cn/default/crawl/575/w550h825/20210529/5040-kquziii3294876.jpg","content":"","url":"https://news.sina.cn/gn/2021-05-29/detail-ikmxzfmm5381873.d.html?vt=4&pos=108","weburl":"https://news.sina.com.cn/c/2021-05-29/doc-ikmxzfmm5381873.shtml"}]}}
     * requestId : beddb7c259b24c5eaf387160e780f72f
     */

    private String code;
    private boolean charge;
    private String msg;
    private ResultBeanX result;
    private String requestId;

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public static class ResultBeanX {
        /**
         * status : 0
         * msg : ok
         * result : {"channel":"头条","num":10,"list":[{"title":"联播+丨加快建设科技强国 总书记有最新部署","time":"2021-05-28 22:15:00","src":"央视","category":"news","pic":"https://n.sinaimg.cn/default/crawl/575/w550h825/20210529/5040-kquziii3294876.jpg","content":"","url":"https://news.sina.cn/gn/2021-05-29/detail-ikmxzfmm5381873.d.html?vt=4&pos=108","weburl":"https://news.sina.com.cn/c/2021-05-29/doc-ikmxzfmm5381873.shtml"}]}
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
             * list : [{"title":"联播+丨加快建设科技强国 总书记有最新部署","time":"2021-05-28 22:15:00","src":"央视","category":"news","pic":"https://n.sinaimg.cn/default/crawl/575/w550h825/20210529/5040-kquziii3294876.jpg","content":"","url":"https://news.sina.cn/gn/2021-05-29/detail-ikmxzfmm5381873.d.html?vt=4&pos=108","weburl":"https://news.sina.com.cn/c/2021-05-29/doc-ikmxzfmm5381873.shtml"}]
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
                 * title : 联播+丨加快建设科技强国 总书记有最新部署
                 * time : 2021-05-28 22:15:00
                 * src : 央视
                 * category : news
                 * pic : https://n.sinaimg.cn/default/crawl/575/w550h825/20210529/5040-kquziii3294876.jpg
                 * content :
                 * url : https://news.sina.cn/gn/2021-05-29/detail-ikmxzfmm5381873.d.html?vt=4&pos=108
                 * weburl : https://news.sina.com.cn/c/2021-05-29/doc-ikmxzfmm5381873.shtml
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
