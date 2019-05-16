package com.example.mutidemo.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/4.
 */

public class TestBean{

    /**
     * status : ok
     * result : [{"date":"1","amount":"1464"},{"date":"2","amount":"1553"},{"date":"3","amount":"1544.5"},{"date":"4","amount":"32"},{"date":"5","amount":"116.5"},{"date":"6","amount":"73"},{"date":"7","amount":"36"},{"date":"8","amount":"251"},{"date":"9","amount":"369"},{"date":"10","amount":"129"},{"date":"11","amount":"131.4"},{"date":"12","amount":"589"}]
     */

    private String status;
    private List<ResultBean> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * date : 1
         * amount : 1464
         */

        private String date;
        private String amount;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
