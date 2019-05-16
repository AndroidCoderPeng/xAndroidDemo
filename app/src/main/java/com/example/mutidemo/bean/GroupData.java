package com.example.mutidemo.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/3.
 */

public class GroupData implements Serializable {
    private String name;
    private String num;

    public GroupData(String name) {
        this.name = name;
    }

    public GroupData(String name, String num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
