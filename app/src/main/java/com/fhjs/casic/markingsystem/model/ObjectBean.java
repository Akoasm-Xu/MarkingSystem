package com.fhjs.casic.markingsystem.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fhjs.casic.markingsystem.controller.adapter.MainAdapter;

import androidx.annotation.NonNull;
import cn.bmob.v3.BmobObject;

/**
 * 录入人员的信息实体类（面试者 绩效考核人）
 */
public class ObjectBean extends BmobObject implements MultiItemEntity {

    private String name;//姓名（面试者）
    private String sex;//性别

    private String phone;//联系方式
    private String object_userIDs;//面试人或者考核人ID ObjectId 多个id以逗号分割

    private String remarks;//备注
    private String date;//日期
    private Integer role;//角色   1-应聘者（面试者）  2-绩效考核人
    private Integer age;//年龄
    private Integer post;//岗位
    private Integer department;//面试部门
    private Boolean markType;//评分状态 true-已打分  false-未打分
    private Integer pa_type;//考核类型 123456
    public ObjectBean(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public ObjectBean() {
    }

    public String getObject_userIDs() {
        return object_userIDs;
    }

    public void setObject_userIDs(String object_userIDs) {
        this.object_userIDs = object_userIDs;
    }

    public Integer getPa_type() {
        return pa_type;
    }

    public void setPa_type(Integer pa_type) {
        this.pa_type = pa_type;
    }

    public Boolean getMarkType() {
        return markType;
    }

    public void setMarkType(Boolean markType) {
        this.markType = markType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "ObjectBean{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                ", remarks='" + remarks + '\'' +
                ", date='" + date + '\'' +
                ", role=" + role +
                ", age=" + age +
                ", post=" + post +
                ", department=" + department +
                '}';
    }

    @Override
    public int getItemType() {
        return MainAdapter.TYPE_ITEM;
    }
}
