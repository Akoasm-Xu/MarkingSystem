package com.fhjs.casic.markingsystem.model;

import cn.bmob.v3.BmobObject;

public class TypeBean extends BmobObject {
    private Integer id_key;
    private String value;
    private String type;

    public Integer getId_key() {
        return id_key;
    }

    public void setId_key(Integer id_key) {
        this.id_key = id_key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
