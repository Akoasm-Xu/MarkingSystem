package com.fhjs.casic.markingsystem.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class UserBean extends BmobUser {
    private String userName_CHS;//用户姓名
    private Boolean isAdmin;//是否为管理员
    private Integer post;//岗位
    private Integer department;//部门
    private Integer group;//组别
    private Integer level;//等级

    public String getUserName_CHS() {
        return userName_CHS;
    }

    public void setUserName_CHS(String userName_CHS) {
        this.userName_CHS = userName_CHS;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
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

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userName_CHS='" + userName_CHS + '\'' +
                ", isAdmin=" + isAdmin +
                ", post=" + post +
                ", department=" + department +
                ", group=" + group +
                ", level=" + level +
                '}';
    }
}
