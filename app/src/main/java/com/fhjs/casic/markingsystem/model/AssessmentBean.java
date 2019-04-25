package com.fhjs.casic.markingsystem.model;

import cn.bmob.v3.BmobObject;

/*
 *考核内容
 */
public class AssessmentBean extends BmobObject {
    private Integer id_key;
    private String value;
    private Integer type;//一、工作态度。二、基础能力 　 三、业务水平 。四、责任感。　 五、协调性。六、自我启发


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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
}
