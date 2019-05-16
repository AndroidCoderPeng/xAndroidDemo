package com.example.mutidemo.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/8/26.
 */

public class User extends BmobUser {

    private static final long serialVersionUID = 1L;

    private Integer age;//年龄

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}
