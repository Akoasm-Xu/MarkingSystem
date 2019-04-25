package com.fhjs.casic.markingsystem.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fhjs.casic.markingsystem.controller.adapter.MainAdapter;
import com.fhjs.casic.markingsystem.util.ConstantUtil;

import java.util.List;

public class MainListBean extends AbstractExpandableItem<ObjectBean> implements MultiItemEntity {
    private String date;
    private List<ObjectBean> lists;
    private String details;
    private String subTitle;
    private Integer count;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public MainListBean(Integer roles, List<ObjectBean> lists) {

        if (roles.equals(null)||roles.equals(ConstantUtil.ROLE_INTERVIEW)){
         this.role="面试评测";
        }else {
            this.role="绩效考核";
        }
        this.lists = lists;
    }

    public MainListBean(String date, Integer count) {
        this.date = date;
        this.count = count;
    }

    public MainListBean(String date, String details) {
        this.date = date;
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ObjectBean> getLists() {
        return lists;
    }

    public void setLists(List<ObjectBean> lists) {
        this.lists = lists;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public int getItemType() {
        return MainAdapter.TYPE_Head;
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
